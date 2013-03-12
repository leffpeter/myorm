package com.leff.myorm.jdbc.manager.impl;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.bean.helper.BeanHelper;
import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.exception.internal.JDBCIllegalStateException;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.jdbc.manager.JdbcManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.*;

/**
 * Implementación del Manager JDBC.
 *
 * @author leff
 */
@Component
public final class JdbcManagerImpl implements JdbcManager {

    /**
     * Datasource.
     */
    @Autowired
    private DataSource dataSource;
    /**
     * Etiqueta para logs.
     */
    private static final String TAG = JdbcManagerImpl.class.getSimpleName();

    /**
     * Tipos de datos soportados por el manager.
     */
    private static enum ClassType {

        String,
        Integer,
        Long,
        BigDecimal,
        Date,
        TimeStamp,
        Boolean,
        Short,
        Byte,
        Float,
        ByteArray,
        Other
    }

    /**
     * Estado actual del manager.
     */
    private State currentState = State.Down;
    /**
     * Connection.
     */
    private Connection conn = null;
    /**
     * PreparedStatement.
     */
    private PreparedStatement ps = null;
    /**
     * ResultSet.
     */
    private ResultSet rs = null;
    /**
     * Resultado del update.
     */
    private int result = -1;
    /**
     * Flag para indicar que si se está auditando o no.
     */
    private boolean auditing = false;
    /**
     * Flag para indicar que si se está paginando o no.
     */
    private boolean paging = false;
    /**
     * Consulta SQL.
     */
    private String queryString = null;
    /**
     * generatedKey
     */
    private String generatedKey = null;

    /**
     * Recupera la conexión.
     *
     * @return @throws InternalException
     */
    private void getConnection() throws InternalException {
        if (conn == null) {
            try {
                conn = dataSource.getConnection();
            } catch (SQLException ex) {
                throw new InternalException(ex);
            }
        }
    }

    /**
     * Crea un objeto para enviar sentencias parametrizadas a la base de datos.
     *
     * @param query Consulta SQL.
     */
    @Override
    public void prepareStatement(final String query) throws InternalException {
        setup(query, false, false);
        prepareStatement();
    }

    /**
     * Crea un objeto para enviar sentencias parametrizadas y paginadas a la
     * base de datos.
     *
     * @param query Consulta SQL.
     */
    @Override
    public void preparePagingStatement(final String query) throws InternalException {
        setup(query, false, true);
        prepareStatement();
    }

    /**
     * Libera los recursos utilizados por la sentencia SQL.
     */
    private void closePreparedStatement() throws InternalException {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                throw new InternalException(ex);
            }
        }
    }

    /**
     * Cierra un ResultSet.
     *
     * @throws InternalException
     */
    private void closeResultSet() throws InternalException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new InternalException(ex);
            }
        }
    }

    /**
     * constructor.
     */
    public JdbcManagerImpl() {
    }

    /**
     * Método de inicialización anotado con @PostConstruct
     *
     * @throws InternalException
     */
    @PostConstruct
    public void postConstruct() throws InternalException {
        getConnection();
    }

    /**
     * Método de destrucción anotado con @PostConstruct
     *
     * @throws InternalException
     */
    @PreDestroy
    public void preDestroy() throws InternalException {
        close();
    }

    /**
     * setup para utilizar un preparedStatement
     *
     * @param queryString
     * @param auditing
     * @param paging
     */
    private void setup(final String queryString, final boolean auditing, final boolean paging) throws InternalException {
        this.auditing = auditing;
        this.paging = paging;
        this.queryString = queryString;
        currentState = State.Up;
    }

    /**
     * Resetea el manager.
     */
    private void reset() {
        this.auditing = false;
        this.paging = false;
        this.queryString = null;
        this.generatedKey = null;
        this.result = -1;
        this.ps = null;
        this.rs = null;
    }

    /**
     * Prepara el statement para ejecución de consulta SQL.
     *
     * @throws InternalException
     */
    private void prepareStatement() throws InternalException {
        try {


            if (paging) {
                ps = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } else {
                ps = conn.prepareStatement(queryString);
            }

        } catch (SQLException ex) {
            close();
            throw new InternalException(ex);
        }
        currentState = State.Ready;
    }

    @Override
    public void executeUpdate() throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.Ready)) {
            try {

                result = ps.executeUpdate();

                recoverGeneratedKey();
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for executeUpdate (" + currentState + ").");
        }
        currentState = State.Executed;
    }

    @Override
    public void executeQuery() throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.Ready)) {
            try {

                rs = ps.executeQuery();

            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for executeQuery (" + currentState + ").");
        }
        currentState = State.Executed;
    }

    @Override
    public void close() throws InternalException {
        closeResultSet();
        closePreparedStatement();
        reset();
        currentState = State.Closed;
    }

    @Override
    public ResultSet getResultSet() {
        return rs;
    }

    @Override
    public int getUpdateResult() {
        return result;
    }

    /**
     * Recupera la clave generada para el caso de un INSERT.
     *
     * @throws SQLException
     */
    private void recoverGeneratedKey() throws SQLException {

        ResultSet rsGenKeys = null;
        try {
            rsGenKeys = ps.getGeneratedKeys();
            if (rsGenKeys.next()) {
                generatedKey = rsGenKeys.getString(1);
            }
        } catch (SQLException ex) {
            generatedKey = null;
        } finally {
            if (rsGenKeys != null) {
                rsGenKeys.close();
            }
        }

    }

    @Override
    public String getGeneratedKey() {
        return generatedKey;
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setString(int index, String value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.VARCHAR);

        } else {

            ps.setString(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setInteger(int index, Integer value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setInt(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setLong(int index, Long value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setLong(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setBigDecimal(int index, BigDecimal value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setBigDecimal(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setDate(int index, Date value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.DATE);

        } else {

            ps.setDate(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setTimestamp(int index, Timestamp value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.TIMESTAMP);

        } else {

            ps.setTimestamp(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setBoolean(int index, Boolean value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.BOOLEAN);

        } else {

            ps.setBoolean(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setShort(int index, Short value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setShort(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setByte(int index, Byte value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setByte(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setFloat(int index, Float value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.NUMERIC);

        } else {

            ps.setFloat(index, value);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setByteArray(int index, byte[] value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.BINARY);

        } else {

            ps.setBinaryStream(index, new ByteArrayInputStream(value), value.length);

        }
    }

    /**
     * @param index
     * @param value
     * @throws SQLException
     */
    private void setObject(int index, Object value) throws SQLException {
        if (value == null) {

            ps.setNull(index, Types.OTHER);

        } else {

            ps.setObject(index, value);

        }
    }

    /**
     * Establece un valor en el PreparedStatement
     *
     * @param <V>
     * @param index
     * @param value
     * @param type
     * @throws SQLException
     */
    private <V> void setValue(int index, V value, Class<V> type) throws SQLException {
        switch (resolveClass(type)) {
            case String:
                setString(index, (String) value);
                break;
            case Integer:
                setInteger(index, (Integer) value);
                break;
            case Long:
                setLong(index, (Long) value);
                break;
            case BigDecimal:
                setBigDecimal(index, (BigDecimal) value);
                break;
            case Date:
                setDate(index, (Date) value);
                break;
            case TimeStamp:
                setTimestamp(index, (Timestamp) value);
                break;
            case Boolean:
                setBoolean(index, (Boolean) value);
                break;
            case Short:
                setShort(index, (Short) value);
                break;
            case Byte:
                setByte(index, (Byte) value);
                break;
            case Float:
                setFloat(index, (Float) value);
                break;
            case ByteArray:
                setByteArray(index, (byte[]) value);
                break;
            case Other:
            default:
                setObject(index, value);
        }
    }

    /**
     * Recupera un objeto del ResultSet.
     *
     * @param type Tipo del objeto a recuperar.
     * @param name nombre del objeto a recuperar.
     * @return
     */
    private <V> V getValue(String name, Integer index, Class<V> type) throws SQLException {
        Object value = null;
        boolean byName = name != null;

        switch (resolveClass(type)) {
            case String:
                value = byName ? rs.getString(name) : rs.getString(index);
                break;
            case Integer:
                value = byName ? rs.getInt(name) : rs.getInt(index);
                break;
            case Long:
                value = byName ? rs.getLong(name) : rs.getLong(index);
                break;
            case BigDecimal:
                value = byName ? rs.getBigDecimal(name) : rs.getBigDecimal(index);
                break;
            case Date:
                value = byName ? rs.getDate(name) : rs.getDate(index);
                break;
            case TimeStamp:
                value = byName ? rs.getTimestamp(name) : rs.getTimestamp(index);
                break;
            case Boolean:
                value = byName ? rs.getBoolean(name) : rs.getBoolean(index);
                break;
            case Short:
                value = byName ? rs.getShort(name) : rs.getShort(index);
                break;
            case Byte:
                value = byName ? rs.getByte(name) : rs.getByte(index);
                break;
            case Float:
                value = byName ? rs.getFloat(name) : rs.getFloat(index);
                break;
            case ByteArray:
                value = byName ? rs.getBytes(name) : rs.getBytes(index);
                break;
            case Other:
            default:
                value = byName ? rs.getObject(name) : rs.getObject(index);
        }

        return (V) value;


    }

    /**
     * Resuelve la clase indicada al enumerado ClassType, que mantiene los tipos
     * soportados por el manager.
     *
     * @param clazz clase a resolver.
     * @return Tipo de la clase resuelto como valor de enumerado.
     */
    private ClassType resolveClass(Class<?> clazz) {

        if (clazz.equals(String.class)) {
            return ClassType.String;
        } else if (clazz.equals(Integer.class)) {
            return ClassType.Integer;
        } else if (clazz.equals(Long.class)) {
            return ClassType.Long;
        } else if (clazz.equals(BigDecimal.class)) {
            return ClassType.BigDecimal;
        } else if (clazz.equals(java.util.Date.class)) {
            return ClassType.Date;
        } else if (clazz.equals(Date.class)) {
            return ClassType.Date;
        } else if (clazz.equals(Timestamp.class)) {
            return ClassType.TimeStamp;
        } else if (clazz.equals(Boolean.class)) {
            return ClassType.Boolean;
        } else if (clazz.equals(Short.class)) {
            return ClassType.Short;
        } else if (clazz.equals(Byte.class)) {
            return ClassType.Byte;
        } else if (clazz.equals(Float.class)) {
            return ClassType.Float;
        } else if (clazz.equals(byte[].class)) {
            return ClassType.ByteArray;
        } else if (clazz.equals(Array.newInstance(byte.class, 0).getClass())) {
            return ClassType.ByteArray;
        } else {
            return ClassType.Other;
        }
    }

    @Override
    public <R> R getResult(String name, Class<R> type) throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.hasResults)) {
            try {
                return getValue(name, null, type);
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for getResult (" + currentState + ").");
        }
    }

    @Override
    public <R> R getResult(int columnIndex, Class<R> type) throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.hasResults)) {
            try {
                return getValue(null, columnIndex, type);
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for getResult (" + currentState + ").");
        }
    }

    @Override
    public <B extends BaseBean> B getResult(Class<B> type) throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.hasResults)) {
            try {
                return BeanHelper.recover(rs, type);
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            } catch (ReflectException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for getResult (" + currentState + ").");
        }
    }

    @Override
    public boolean hasResults() throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.Executed)) {
            try {
                currentState = State.hasResults;
                return rs != null && rs.next();
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for hasResults (" + currentState + ").");
        }
    }

    @Override
    public <P> void setParam(int index, P value, Class<P> type) throws InternalException, JDBCIllegalStateException {
        if (currentState.equals(State.Ready)) {
            try {
                setValue(index, value, type);
            } catch (SQLException ex) {
                close();
                throw new InternalException(ex);
            }
        } else {
            throw new JDBCIllegalStateException("wrong state for setParam (" + currentState + ").");
        }
    }

    /**
     * Devuelve el estado actual del manager.
     *
     * @return
     */
    @Override
    public State getCurrentState() {
        return currentState;
    }
}