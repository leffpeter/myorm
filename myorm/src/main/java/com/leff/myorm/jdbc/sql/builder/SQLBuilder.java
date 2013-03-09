/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.builder;


import com.leff.myorm.jdbc.sql.query.*;
import com.leff.myorm.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor simple de sentencias SQL.
 * Se trata de simplificar la construcción de sentencias SQL típicas, como son
 * INSERT y SELECT básicas, para las tareas típicas de BBDD.
 *
 * @author leff
 */
public final class SQLBuilder {

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entities  Entidades sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @param orderBy   Expresión del blque ORDER BY.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, List<String> entities, String whereExp, String orderBy) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(selectExp);
        query.setWhereExpression(whereExp);
        query.setEntities(entities);
        query.setOrderByExpression(orderBy);
        query.setGroupByExpression(null);

        return query;
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entities  Entidades sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @param orderBy   Expresión del bloque ORDER BY.
     * @param groupBy   Expresión del bloque GROUP BY.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, List<String> entities, String whereExp, String orderBy, String groupBy) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(selectExp);
        query.setWhereExpression(whereExp);
        query.setEntities(entities);
        query.setOrderByExpression(orderBy);
        query.setGroupByExpression(groupBy);

        return query;
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entity    Entidads sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @param orderBy   Expresión del blque ORDER BY.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, String entity, String whereExp, String orderBy) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(selectExp);
        query.setWhereExpression(whereExp);
        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);
        query.setOrderByExpression(orderBy);
        query.setGroupByExpression(null);

        return query;
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entity    Entidads sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @param groupBy   Expresión del bloque GROUP BY.
     * @param orderBy   Expresión del bloque ORDER BY.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, String entity, String whereExp, String orderBy, String groupBy) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(selectExp);
        query.setWhereExpression(whereExp);
        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);
        query.setOrderByExpression(orderBy);
        query.setGroupByExpression(groupBy);

        return query;
    }

    /**
     * Construye una Query SELECT MAX.
     *
     * @param field  Campo.
     * @param entity Entidad sobre la que se realiza la Query.
     * @return
     */
    public static SqlQuery buildSelectMaxQuery(String field, String entity) {
        return buildSelectQuery(SqlQuery.SQL_MAX + "(" + field + ")", entity, null, null);
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entities  Entidades sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, List<String> entities, String whereExp) {
        return buildSelectQuery(selectExp, entities, whereExp, null, null);
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entity    Entidad sobre la que se realiza la Query.
     * @param whereExp  Expresión del bloque WHERE.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, String entity, String whereExp) {
        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        return buildSelectQuery(selectExp, entities, whereExp, null, null);
    }

    /**
     * Construye una Query SELECT.
     *
     * @param selectExp Expresión del bloque SELECT.
     * @param entity    Entidad sobre la que se realiza la Query.
     * @return
     */
    public static SqlQuery buildSelectQuery(String selectExp, String entity) {
        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        return buildSelectQuery(selectExp, entities, null, null, null);
    }

    /**
     * Construye una Query INSERT.
     *
     * @param entity Entidad sobre la que se realiza la Query.
     * @param fields Lista de campos de la entidad.
     * @return
     */
    public static SqlQuery buildInsertQuery(String entity, List<String> fields) {
        SqlQuery query = new InsertQuery();

        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);
        query.setInsertFields(fields);

        return query;
    }

    /**
     * Construye una Query INSERT.
     *
     * @param entity               Entidad sobre la que se realiza la Query.
     * @param commaSeparatedFields String con los campos de la entidad, separados por coma ",".
     * @return
     */
    public static SqlQuery buildInsertQuery(String entity, String commaSeparatedFields) {
        SqlQuery query = new InsertQuery();

        String[] fields = commaSeparatedFields.split(",");
        query.setInsertFields(Util.toList(fields));

        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);

        return query;
    }

    /**
     * Construye una Query UPDATE.
     *
     * @param setExp   Expresión del bloque SET.
     * @param entity   Entidad sobre la que se realiza la Query.
     * @param whereExp Expresión del bloque WHERE.
     * @return
     */
    public static SqlQuery buildUpdateQuery(String setExp, String entity, String whereExp) {
        SqlQuery query = new UpdateQuery();

        query.setSetExpression(setExp);
        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);
        query.setWhereExpression(whereExp);

        return query;
    }

    /**
     * Construye una Query en crudo.
     *
     * @param plainQuery Cadena con la query en crudo, tal y como se desea.
     * @return
     */
    public static SqlQuery buildCustomQuery(String plainQuery) {
        return new CustomQuery(plainQuery);
    }

    /**
     * Construye una Query DELETE.
     *
     * @param entity
     * @param whereExp
     * @return
     */
    public static SqlQuery buildDeleteQuery(String entity, String whereExp) {
        SqlQuery query = new DeleteQuery();

        List<String> entities = new ArrayList<String>();
        entities.add(entity);
        query.setEntities(entities);
        query.setWhereExpression(whereExp);

        return query;
    }

    /**
     * Constructor.
     */
    private SQLBuilder() {
    }
}
