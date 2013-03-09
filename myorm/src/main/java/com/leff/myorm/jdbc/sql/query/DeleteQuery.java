/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.query;

import java.util.Iterator;
import java.util.List;

/**
 * Query delete.
 *
 * @author leff
 */
public final class DeleteQuery implements SqlQuery {

    /**
     *
     */
    private static final String TYPE = SQL_TYPE_DELETE;
    private static final String FROM_EXP = "$f";
    private static final String WHERE_EXP = "$w";
    private String from = SQL_EMPTY;
    private String where = SQL_EMPTY;
    /**
     * The query.
     */
    private String query = TYPE + " " + SQL_FROM + " " + FROM_EXP + " " + WHERE_EXP;

    /**
     * constructor.
     */
    public DeleteQuery() {
    }

    /**
     *
     */
    private void setValues() {
        query = query.replace(FROM_EXP, from);
        query = query.replace(WHERE_EXP, where);
    }

    @Override
    public void setEntities(List<String> entities) {
        from = "";
        for (Iterator<String> it = entities.iterator(); it.hasNext(); ) {
            from += it.next();
            from += ", ";
        }
        from = from.substring(0, from.lastIndexOf(','));
    }

    @Override
    public void setSelectExpression(String expression) {
    }

    @Override
    public void setInsertFields(List<String> fields) {
    }

    @Override
    public void setWhereExpression(String expression) {
        where = expression != null ? SQL_WHERE + " " + expression : SQL_EMPTY;
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