/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.query;

import java.util.List;

/**
 * Query insert.
 *
 * @author leff
 */
public final class InsertQuery implements SqlQuery {

    /**
     *
     */
    private static final String TYPE = SQL_TYPE_INSERT;
    private static final String FIELDS_EXP = "$f";
    private static final String FIELDVALUES_EXP = "$v";
    private static final String ENTITY_EXP = "$e";
    private String entity = null;
    private String fields = null;
    private String fieldValues = null;
    /**
     * The query.
     */
    private String query = TYPE + " " + SQL_INTO + " " + ENTITY_EXP + " (" + FIELDS_EXP + ") " + SQL_VALUES + " (" + FIELDVALUES_EXP + ")";


    /**
     * constructor.
     */
    public InsertQuery() {
    }

    /**
     *
     */
    private void setValues() {
        query = query.replace(ENTITY_EXP, entity);
        query = query.replace(FIELDS_EXP, fields);
        query = query.replace(FIELDVALUES_EXP, fieldValues);
    }

    @Override
    public void setEntities(List<String> entities) {
        entity = "";
        for (String ent : entities) {
            entity += ent;
            entity += ", ";
        }
        entity = entity.substring(0, entity.lastIndexOf(','));
    }

    @Override
    public void setSelectExpression(String expression) {
    }

    @Override
    public void setInsertFields(List<String> fieldList) {
        fields = "";
        fieldValues = "";
        for (String field : fieldList) {
            fields += field.trim();
            fields += ", ";
            fieldValues += "?, ";
        }
        fields = fields.substring(0, fields.lastIndexOf(','));
        fieldValues = fieldValues.substring(0, fieldValues.lastIndexOf(','));
    }

    @Override
    public void setWhereExpression(String expression) {
    }

    @Override
    public void setOrderByExpression(String expression) {
    }

    @Override
    public void setSetExpression(String expression) {
    }

    @Override
    public String getQuery() {
        setValues();
        return query.trim();
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return getQuery();
    }

    @Override
    public void setPlainQuery(String plainQuery) {
        query = plainQuery;
    }

    @Override
    public void addJoin(boolean leftOuter, String entity, String alias, String onExp) {
    }

    @Override
    public void setGroupByExpression(String expression) {
    }
}