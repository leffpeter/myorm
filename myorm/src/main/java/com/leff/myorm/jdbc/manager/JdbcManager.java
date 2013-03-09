package com.leff.myorm.jdbc.manager;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.exception.internal.JDBCIllegalStateException;

import java.sql.ResultSet;

/**
 * Manager JDBC.
 * <p/>
 * IMPORTANTE: Esta interfaz no dispone de método para cerrar la conexión,
 * ya que dicha conexión se proporcionará o se recuperará del contexto de Spring.
 *
 * @author ALTIA
 */
public interface JdbcManager {

    /**
     * Estados del manager.
     */
    enum State {

        /**
         * Tirado, sin iniciar.
         */
        Down,
        /**
         * Levantado, configurado y con conexión.
         */
        Up,
        /**
         * Preparado para ejecutar.
         */
        Ready,
        /**
         * Ejecutado.
         */
        Executed,
        /**
         * Ejecutado y comprobado si hay resultados de consulta.
         */
        hasResults,
        /**
         * Cerrado, todos los recursos cerrados.
         */
        Closed
    }

    /**
     * Preparar la sentencia.
     *
     * @param query query.
     * @throws InternalException
     */
    void prepareStatement(final String query) throws InternalException;

    /**
     * Preparar una sentencia preparada para paginar.
     *
     * @param query query.
     * @throws InternalException
     */
    void preparePagingStatement(final String query) throws InternalException;

    /**
     * Establece un valor como parámetro en el preparedStament.
     *
     * @param <P>
     * @param index índice del parámetro.
     * @param value valor del parámetro.
     * @param type  Tipo del parámetro, por si el valor es nulo.
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    <P> void setParam(int index, P value, Class<P> type) throws InternalException, JDBCIllegalStateException;

    /**
     * Ejecuta el update de la consulta.
     *
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    void executeUpdate() throws InternalException, JDBCIllegalStateException;

    /**
     * Ejecuta la consulta.
     *
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    void executeQuery() throws InternalException, JDBCIllegalStateException;

    /**
     * Devuelve el ResultSet obtenido con la consulta.
     *
     * @return
     */
    ResultSet getResultSet();

    /**
     * Devuelve el resultado del update.
     *
     * @return
     */
    int getUpdateResult();

    /**
     * Devuelve la claves obtenida, para el caso de un insert.
     *
     * @return
     */
    String getGeneratedKey();

    /**
     * Comprueba si se han obtenido resultados tras la consulta.
     * Importante ejecutar este método para recuperar valores, ya que ejecuta
     * el método next() del resultset.
     *
     * @return
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    boolean hasResults() throws InternalException, JDBCIllegalStateException;

    /**
     * Obtiene un resultado del resultset.
     *
     * @param <R>
     * @param name nombre del resultado.
     * @param type tipo del resultado.
     * @return
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    <R> R getResult(String name, Class<R> type) throws InternalException, JDBCIllegalStateException;

    /**
     * Obtiene un resultado del resultset.
     *
     * @param <R>
     * @param index índice del resultado
     * @param type  tipo del resultado.
     * @return
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    <R> R getResult(int index, Class<R> type) throws InternalException, JDBCIllegalStateException;

    /**
     * Obtiene un bean del resultset.
     *
     * @param <B>
     * @param type tipo del resultado.
     * @return
     * @throws InternalException
     * @throws JDBCIllegalStateException
     */
    <B extends BaseBean> B getResult(Class<B> type) throws InternalException, JDBCIllegalStateException;

    /**
     * Cierra todos los recursos, resultset, preparedStatement.
     *
     * @throws InternalException
     */
    void close() throws InternalException;

    /**
     * Devuelve el estado actual del manager.
     *
     * @return
     */
    State getCurrentState();
}
