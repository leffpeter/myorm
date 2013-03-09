/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leff.myorm.jdbc.sql.query;

import com.leff.myorm.util.Util;

import java.util.List;

/**
 * Query Select.
 *
 * @author leff
 */
public final class SelectQuery implements SqlQuery {
    /**
     *
     */
    private static final String TYPE = SQL_TYPE_SELECT;

    private static final String SELECT_EXP = "$s";
    private static final String FROM_EXP = "$f";
    private static final String ORDERBY_EXP = "$o";
    private static final String WHERE_EXP = "$w";
    private static final String GROUPBY_EXP = "$g";

    private String select = SQL_EMPTY;
    private String from = SQL_EMPTY;
    private String orderBy = SQL_EMPTY;
    private String where = SQL_EMPTY;
    private String groupBy = SQL_EMPTY;

    /**
     * The query.
     */
    private String query = TYPE + " " + SELECT_EXP + " " + SQL_FROM + " " + FROM_EXP + " " + WHERE_EXP + " " + GROUPBY_EXP + " " + ORDERBY_EXP;

    /**
     * constructor.
     */
    public SelectQuery() {
    }


    /**
     *
     */
    private void setValues() {
        query = query.replace(SELECT_EXP, select);
        query = query.replace(FROM_EXP, from);
        query = query.replace(WHERE_EXP, where);
        query = query.replace(GROUPBY_EXP, groupBy);
        query = query.replace(ORDERBY_EXP, orderBy);
    }


    @Override
    public void setEntities(List<String> entities) {
        from = "";
        for (String entity : entities) {
            from += entity;
            from += ", ";
        }
        from = from.substring(0, from.lastIndexOf(','));
    }

    @Override
    public void setSelectExpression(String expression) {
        select = !Util.emptyString(expression) ? expression : SQL_ASTERISK;
    }

    @Override
    public void setInsertFields(List<String> fields) {
    }

    @Override
    public void setWhereExpression(String expression) {
        where = !Util.emptyString(expression) ? SQL_WHERE + " " + expression : SQL_EMPTY;
    }

    @Override
    public void setOrderByExpression(String expression) {
        orderBy = !Util.emptyString(expression) ? SQL_ORDER_BY + " " + expression : SQL_EMPTY;
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
        String join = "$join" + " " + entity + " " + "$alias" + " " + SQL_ON + " " + onExp;
        String aliasExp = Util.emptyString(alias) ? "" : alias;
        String joinExp = (leftOuter ? " " + SQL_LEFT + " " + SQL_OUTER : " ") + SQL_JOIN;
        join = join.replace("$join", joinExp);
        join = join.replace("$alias", aliasExp);
        from += join;
    }

    @Override
    public void setGroupByExpression(String expression) {
        groupBy = !Util.emptyString(expression) ? SQL_GROUP_BY + " " + expression : SQL_EMPTY;
    }
}