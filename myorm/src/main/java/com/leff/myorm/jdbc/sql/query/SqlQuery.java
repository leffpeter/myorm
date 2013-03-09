/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.query;

import java.util.List;

/**
 * Interfaz básica para querys SQL.
 *
 * @author leff
 */
public interface SqlQuery {

    /**
     * Tipo de sentencia SELECT.
     */
    String SQL_TYPE_SELECT = "SELECT";
    /**
     * Tipo de sentencia UPDATE.
     */
    String SQL_TYPE_UPDATE = "UPDATE";
    /**
     * Tipo de sentencia INSERT.
     */
    String SQL_TYPE_INSERT = "INSERT";
    /**
     * Tipo de sentencia DELETE.
     */
    String SQL_TYPE_DELETE = "DELETE";

    /**
     * Palabra reservada FROM.
     */
    String SQL_FROM = "FROM";
    /**
     * Palabra reservada SET.
     */
    String SQL_SET = "SET";
    /**
     * Palabra reservada VALUES.
     */
    String SQL_VALUES = "VALUES";
    /**
     * Palabra reservada WHERE.
     */
    String SQL_WHERE = "WHERE";
    /**
     * Palabra reservada ORDER BY.
     */
    String SQL_ORDER_BY = "ORDER BY";
    /**
     * Palabra reservada LIKE.
     */
    String SQL_LIKE = "LIKE";
    /**
     * Palabra reservada INTO.
     */
    String SQL_INTO = "INTO";
    /**
     * Asterisco.
     */
    String SQL_ASTERISK = "*";
    /**
     * Cadena vacía.
     */
    String SQL_EMPTY = "";
    /**
     * Palabra reservada MAX.
     */
    String SQL_MAX = "MAX";
    /**
     * Palabra reservada MIN.
     */
    String SQL_MIN = "MIN";
    /**
     * Palabra reservada AND.
     */
    String SQL_AND = "AND";
    /**
     * Palabra reservada OR.
     */
    String SQL_OR = "OR";
    /**
     * Palabra reservada IN.
     */
    String SQL_IN = "IN";
    /**
     * Palabra reservada ASC.
     */
    String SQL_ASC = "ASC";
    /**
     * Palabra reservada DESC.
     */
    String SQL_DESC = "DESC";
    /**
     * Palabra reservada DISTINCT.
     */
    String SQL_DISTINCT = "DISTINCT";
    /**
     * Palabra reservada IS NULL.
     */
    String SQL_IS_NULL = "IS NULL";
    /**
     * Palabra reservada IS NOT NULL.
     */
    String SQL_IS_NOT_NULL = "IS NOT NULL";
    /**
     * Palabra reservada COUNT.
     */
    String SQL_COUNT = "COUNT";
    /**
     * Palabra reservada LEFT.
     */
    String SQL_LEFT = "LEFT";
    /**
     * Palabra reservada RIGHT.
     */
    String SQL_RIGHT = "RIGHT";
    /**
     * Palabra reservada OUTER.
     */
    String SQL_OUTER = "OUTER";
    /**
     * Palabra reservada JOIN.
     */
    String SQL_JOIN = "JOIN";
    /**
     * Palabra reservada NATURAL JOIN.
     */
    String SQL_NATURAL_JOIN = "NATURAL JOIN";
    /**
     * Palabra reservada ON.
     */
    String SQL_ON = "ON";
    /**
     * Palabra reservada AS.
     */
    String SQL_AS = "AS";
    /**
     * Palabra reservada NOT.
     */
    String SQL_NOT = "NOT";
    /**
     * Palabra reservada BETWEEN.
     */
    String SQL_BETWEEN = "BETWEEN";
    /**
     * Palabra reservada GROUP BY.
     */
    String SQL_GROUP_BY = "GROUP BY";
    /**
     * Menor que.
     */
    String SQL_LESS_THAN = "<";
    /**
     * Mayor que.
     */
    String SQL_GREATER_THAN = ">";
    /**
     * Igual.
     */
    String SQL_EQUAL = "=";

    /**
     * Igual a interrogante.
     */
    String SQL_EQUALS_QUESTION = " = ?";
    /**
     * No igual (distinto).
     */
    String SQL_NOT_EQUAL = "!=";
    /**
     * Mayor o igual.
     */
    String SQL_GREATER_OR_EQUAL_THAN = ">=";
    /**
     * Menor o igual.
     */
    String SQL_LESS_OR_EQUAL_THAN = "<=";
    /**
     * Palabra reservada LEFT OUTER JOIN.
     */
    String SQL_LEFT_OUTER_JOIN = "LEFT OUTER JOIN";
    /**
     * Palabra reservada RIGHT OUTER JOIN.
     */
    String SQL_RIGHT_OUTER_JOIN = "RIGHT OUTER JOIN";
    /**
     * Palabra reservada LEFT JOIN.
     */
    String SQL_LEFT_JOIN = "LEFT JOIN";
    /**
     * Palabra reservada CASE WHEN.
     */
    String SQL_CASE_WHEN = "CASE WHEN";
    /**
     * Palabra reservada ELSE.
     */
    String SQL_ELSE = "ELSE";
    /**
     * Palabra reservada END.
     */
    String SQL_END = "END";
    /**
     * Palabra reservada SUM.
     */
    String SQL_SUM = "SUM";
    /**
     * Palabra reservada THEN.
     */
    String SQL_THEN = "THEN";
    /**
     * Palabra reservada CASE.
     */
    String SQL_CASE = "CASE";
    /**
     * Palabra reservada WHEN.
     */
    String SQL_WHEN = "WHEN";
    /**
     * comilla escapada.
     */
    String SQL_COMILLA = "\'";
    /**
     * Doble barra vertical.
     */
    String SQL_DOUBLE_BAR = "||";
    /**
     * Condición "dummy" 0 = 0.
     */
    String SQL_DUMMY_CONDITION = "0 = 0";

    /**
     * Establece las entidades sobre las que se ejecutará la query.
     *
     * @param entities lista de entidades (tablas).
     */
    void setEntities(List<String> entities);

    /**
     * Establece la expresión SELECT.
     *
     * @param expression Cadena con la expresión SELECT.
     */
    void setSelectExpression(String expression);

    /**
     * Establece la expresión SET.
     *
     * @param expression Cadena con la expresión SET.
     */
    void setSetExpression(String expression);

    /**
     * Establece los campos a insertar.
     *
     * @param fields Lista de campos a insertar.
     */
    void setInsertFields(List<String> fields);

    /**
     * Establece la expresion WHERE.
     *
     * @param expression cadena con la expresión WHERE.
     */
    void setWhereExpression(String expression);

    /**
     * Establece la expresión ORDER BY
     *
     * @param expression cadena con la expresión ORDER BY.
     */
    void setOrderByExpression(String expression);

    /**
     * Establece una query "en plano".
     *
     * @param plainQuery cadena con la query en plano.
     */
    void setPlainQuery(String plainQuery);

    /**
     * Devuelve la cadena que respresenta la query.
     *
     * @return
     */
    String getQuery();

    /**
     * Añade una JOIN a la query.
     *
     * @param leftOuter flag para indicar que es LEFT JOIN.
     * @param entity    entidad sobre la que aplicar la JOIN.
     * @param alias     alias a establecer.
     * @param onExp     cadena con la expresión de ON.
     */
    void addJoin(boolean leftOuter, String entity, String alias, String onExp);

    /**
     * Establece la expresión GROUP BY.
     *
     * @param expression Cadena con la expresión GROUP BY.
     */
    void setGroupByExpression(String expression);
}
