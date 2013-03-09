/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.dao.impl.oracle;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.bean.helper.BeanRowMapper;
import com.leff.myorm.constant.DataModel;
import com.leff.myorm.dao.impl.JdbcDaoImpl;
import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.jdbc.core.BeanPreparedStatementCreator;
import com.leff.myorm.jdbc.sql.builder.BeanSQLBuilder;
import com.leff.myorm.jdbc.sql.query.SqlQuery;
import com.leff.myorm.util.Util;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementación para Oracle de JdbcDaoImpl.
 *
 * @param <E>
 * @param <PK>
 * @author leff
 */
public abstract class JdbcDaoOracleImpl<E extends BaseBean, PK extends Serializable, T extends Throwable> extends JdbcDaoImpl<E, PK, T> {

    /**
     * NULL de Oracle.
     */
    public static final String ORACLE_NULL = "NULL";
    /**
     * Tipo de Dato VARCHAR2 de Oracle.
     */
    public static final String ORACLE_VARCHAR = "VARCHAR2";
    /**
     * Tipo de Dato NUMBER de Oracle.
     */
    public static final String ORACLE_NUMBER = "NUMBER";
    /**
     * Tipo de Dato NUMBER(4) de Oracle.
     */
    public static final String ORACLE_NUMBER_SHORT = "SHORT";
    /**
     * Tipo de Dato NUMBER(8) de Oracle.
     */
    public static final String ORACLE_NUMBER_INTEGER = "INTEGER";
    /**
     * Tipo de Dato NUMBER(12) de Oracle.
     */
    public static final String ORACLE_NUMBER_LONG = "LONG";
    /**
     * Tipo de Dato NUMBER(x,y) de Oracle.
     */
    public static final String ORACLE_NUMBER_DECIMAL = "DECIMAL";
    /**
     * Tipo de Dato DATE de Oracle.
     */
    public static final String ORACLE_DATE = "DATE";
    /**
     * Tipo de Dato TIMESTAMP de Oracle.
     */
    public static final String ORACLE_TIMESTAMP = "TIMESTAMP";
    /**
     * Stamp de la base de datos en Oracle.
     */
    public static final String ORACLE_SYSTIMESTAMP = "SYSTIMESTAMP";
    /**
     * función regexp_like de Oracle.
     */
    public static final String ORACLE_REGEXP_LIKE = "REGEXP_LIKE";
    /**
     * función upper de Oracle.
     */
    public static final String ORACLE_UPPER = "UPPER";
    /**
     * Expresión para filtrar por fechas. Con variables a sustituir según sea
     * necesario.
     */
    private static final String DATE_CONDITION_USING_TO_DATE = "(to_date($alias$begin,'dd/mm/yyyy') $minor to_date(SYSDATE,'dd/mm/yyyy') OR $alias$begin IS NULL) AND ($alias$end IS NULL OR to_date($alias$end,'dd/mm/yyyy') $mayor to_date(SYSDATE,'dd/mm/yyyy'))";

    /**
     * Realiza un update con BeanPreparedStatementCreator y keyholder.
     *
     * @param sql       Query string.
     * @param bean      bean
     * @param keyHolder keyholder.
     * @return resultado del update.
     * @throws ReflectException
     */
    private int doUpdate(String sql, E bean, KeyHolder keyHolder) throws ReflectException {
        PreparedStatementCreator psc = new BeanPreparedStatementCreator<E>(sql, bean);
        return getJdbcTemplate().update(psc, keyHolder);
    }

    /**
     * Comprueba si el stamp de un bean es null y lo inicia.
     *
     * @param bean bean sobre el que comprobar y establecer el stamp según sea
     *             el caso.
     */
    private void checkNullStamp(E bean) {
        if (bean.getStamp() == null) {
            bean.setStamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        }
    }

    @Override
    public final int insert(E bean) throws InternalException {
        checkNullStamp(bean);
        SqlQuery query = BeanSQLBuilder.buildInsertQuery(getEc());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = doUpdate(query.getQuery(), bean, keyHolder);
        bean.populateKeys(keyHolder);
        return result;
    }

    @Override
    public final int update(E bean) throws InternalException {
        SqlQuery query = BeanSQLBuilder.buildUpdateQuery(getEc());
        return getJdbcTemplate().update(query.getQuery(), getArgsForUpdate(bean));
    }

    @Override
    public final int delete(E bean) throws InternalException {
        SqlQuery query = BeanSQLBuilder.buildDeleteQuery(getEc());
        return getJdbcTemplate().update(query.getQuery(), getArgsForDelete(bean));
    }

    @Override
    public final int deleteById(PK id) throws InternalException, T {
        E e = findById(id);
        return delete(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final E findById(PK id) throws T, InternalException {
        SqlQuery query = BeanSQLBuilder.buildSelectQuery(getEc());
        List<E> list = getJdbcTemplate().query(query.getQuery(), getArg(id), new BeanRowMapper<E>(getEc()));
        if (!Util.emptyList(list)) {
            return list.get(0);
        } else {
            try {
                throw getT().newInstance();
            } catch (InstantiationException e) {
                throw new InternalException(e);
            } catch (IllegalAccessException e) {
                throw new InternalException(e);
            }
        }
    }

    @Override
    public final List<E> listAll() {
        SqlQuery query = BeanSQLBuilder.buildSelectAllQuery(getEc());
        StringBuilder queryString = new StringBuilder(query.getQuery());
        if (isDateFilterRequired()) {
            if (!queryString.toString().contains(SqlQuery.SQL_WHERE)) {
                queryString.append(SqlQuery.SQL_WHERE).append(" ");
            }
            queryString.append(getStandardDateFilterExp(null));
        }
        return getJdbcTemplate().query(queryString.toString(), new BeanRowMapper<E>(getEc()));
    }

    @Override
    public final Long countAll() {
        SqlQuery query = BeanSQLBuilder.buildSelectCountAllQuery(getEc());
        StringBuilder queryString = new StringBuilder(query.getQuery());
        if (isDateFilterRequired()) {
            if (!queryString.toString().contains(SqlQuery.SQL_WHERE)) {
                queryString.append(SqlQuery.SQL_WHERE).append(" ");
            }
            queryString.append(getStandardDateFilterExp(null));
        }
        return getJdbcTemplate().queryForLong(queryString.toString());
    }

    @Override
    public final Date systime() {
        final String query = "SELECT SYSDATE FROM DUAL";
        return getJdbcTemplate().queryForObject(query, Date.class);
    }

    @Override
    protected final String getDateFilterExp(String beginFieldName, String endFieldName, String alias, boolean strictBegin, boolean strictEnd) {
        String result = DATE_CONDITION_USING_TO_DATE;

        result = strictBegin ? result.replace("$minor", "<") : result.replace("$minor", "<=");
        result = strictEnd ? result.replace("$mayor", ">") : result.replace("$mayor", ">=");
        result = Util.emptyString(alias) ? result.replace("$alias", "") : result.replace("$alias", alias + ".");
        result = result.replace("$begin", beginFieldName);
        result = result.replace("$end", endFieldName);

        return result;
    }

    @Override
    protected final String getStandardDateFilterExp(String alias) {
        return getDateFilterExp(DataModel.C_FEC_ALTA, DataModel.C_FEC_BAJA, alias, false, true);
    }
}
