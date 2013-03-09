/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.query;

import java.util.List;

/**
 * Query custom.
 *
 * @author leff
 */
public final class CustomQuery implements SqlQuery {

    /**
     * The query.
     */
    private String query = null;

    /**
     * constructor.
     */
    public CustomQuery() {
    }

    /**
     * constructor.
     *
     * @param plainQuery
     */
    public CustomQuery(String plainQuery) {
        query = plainQuery;
    }

    @Override
    public void setEntities(List<String> entities) {
    }

    @Override
    public void setSelectExpression(String expression) {
    }

    @Override
    public void setInsertFields(List<String> fields) {
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
        return query != null ? query.trim() : SQL_EMPTY;
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