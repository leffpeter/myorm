/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.sql.builder;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.bean.annotation.BeanColumn;
import com.leff.myorm.bean.annotation.BeanPrimaryKey;
import com.leff.myorm.bean.annotation.BeanTable;
import com.leff.myorm.jdbc.sql.query.*;
import com.leff.myorm.util.Util;
import com.leff.myorm.util.list.SortedList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructor simple de sentencias SQL para Beans. Se trata de simplificar la
 * construcción de sentencias SQL típicas para el caso de Beans, como son
 * INSERT, SELECT, DELETE y UPDATE básicas (por PK).
 *
 * @author leff
 */
public final class BeanSQLBuilder {

    /**
     * Constructor.
     */
    private BeanSQLBuilder() {
    }

    /**
     * @param fields
     * @return
     */
    private static String getWhere(final List<String> fields) {
        StringBuilder result = new StringBuilder("");

        for (String field : fields) {
            result.append(field).append(" = ? AND ");
        }
        return Util.trimEmpty(result.substring(0, result.lastIndexOf("AND")));
    }

    /**
     * @param fields
     * @return
     */
    private static String getSet(final List<String> fields) {
        StringBuilder result = new StringBuilder("");

        for (String field : fields) {
            result.append(field).append(" = ?, ");
        }
        return Util.trimEmpty(result.substring(0, result.lastIndexOf(",")));
    }

    /**
     * @param <B>
     * @param beanClass
     * @return
     */
    private static <B extends BaseBean> List<String> getColumnNames(Class<?> beanClass) {
        List<String> result = new SortedList<String>();
        boolean stop = false;
        while (!stop) {
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(BeanColumn.class)) {
                    BeanColumn columnAnnotation = field.getAnnotation(BeanColumn.class);
                    if (result.contains(columnAnnotation.value())) {
                        continue;
                    }
                    //Se salta el campo del stamp para beans con tablas que no tienen: revisar!
//                    if (DataModel.C_STAMP.equalsIgnoreCase(columnAnnotation.value()) &&
//                            !beanClass.isAnnotationPresent(BeanStamp.class)) {
//                        continue;
//                    }
                    result.add(columnAnnotation.value());
                }
            }
            stop = (beanClass == BaseBean.class);
            beanClass = beanClass.getSuperclass();
        }
        return result;
    }

    /**
     * @param <B>
     * @param beanClass
     * @return
     */
    private static <B extends BaseBean> List<String> getPrimaryKey(final Class<B> beanClass) {
        List<String> result = new SortedList<String>();
        if (beanClass.isAnnotationPresent(BeanPrimaryKey.class)) {
            BeanPrimaryKey primaryKeyAnnotation = beanClass.getAnnotation(BeanPrimaryKey.class);
            result.addAll(Util.toList(primaryKeyAnnotation.value()));
        }
        return result;
    }

    /**
     * Recupera el nombre de la tabla anotada en el bean.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    private static <B extends BaseBean> String getTableName(final Class<B> beanClass) {
        if (beanClass.isAnnotationPresent(BeanTable.class)) {
            BeanTable tableAnnotation = beanClass.getAnnotation(BeanTable.class);
            return tableAnnotation.value();
        }
        return null;
    }

    /**
     * Construye una Query SELECT.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildSelectQuery(Class<B> beanClass) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(Util.getAsCSV(getColumnNames(beanClass), ","));
        query.setWhereExpression(getWhere(getPrimaryKey(beanClass)));
        List<String> l = new ArrayList<String>();
        l.add(getTableName(beanClass));
        query.setEntities(l);

        return query;
    }

    /**
     * Construye una Query SELECT.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildSelectAllQuery(Class<B> beanClass) {
        SqlQuery query = new SelectQuery();

        query.setSelectExpression(Util.getAsCSV(getColumnNames(beanClass), ","));
        query.setWhereExpression("");
        List<String> l = new ArrayList<String>();
        l.add(getTableName(beanClass));
        query.setEntities(l);

        return query;
    }

    /**
     * Construye una Query SELECT count(*).
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildSelectCountAllQuery(Class<B> beanClass) {
        String result = SqlQuery.SQL_TYPE_SELECT
                + " " + SqlQuery.SQL_COUNT + " "
                + "(" + SqlQuery.SQL_ASTERISK + ")"
                + " " + SqlQuery.SQL_FROM + " "
                + getTableName(beanClass);
        return new CustomQuery(result);
    }

    /**
     * @param <B>
     * @param bean
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildSelectQuery(B bean) {
        return buildSelectQuery(bean.getClass());
    }

    /**
     * Construye una Query SELECT MAX.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildSelectMaxQuery(Class<B> beanClass) {
        List<String> primaryKey = getPrimaryKey(beanClass);
        String key = null;
        if (!Util.emptyList(primaryKey)) {
            key = primaryKey.get(0);
        }
        String result = SqlQuery.SQL_TYPE_SELECT + " "
                + SqlQuery.SQL_MAX + "(" + key + ")" + " "
                + SqlQuery.SQL_FROM + " "
                + getTableName(beanClass);
        return new CustomQuery(result);
    }

    /**
     * Construye una Query INSERT.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildInsertQuery(Class<B> beanClass) {
        SqlQuery query = new InsertQuery();

        List<String> l = new ArrayList<String>();
        l.add(getTableName(beanClass));
        query.setEntities(l);
        query.setInsertFields(getColumnNames(beanClass));

        return query;
    }

    /**
     * Construye una Query UPDATE.
     *
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildUpdateQuery(Class<B> beanClass) {
        SqlQuery query = new UpdateQuery();

        query.setSetExpression(getSet(getColumnNames(beanClass)));
        List<String> l = new ArrayList<String>();
        l.add(getTableName(beanClass));
        query.setEntities(l);
        query.setWhereExpression(getWhere(getPrimaryKey(beanClass)));

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
     * @param <B>
     * @param beanClass
     * @return
     */
    public static <B extends BaseBean> SqlQuery buildDeleteQuery(Class<B> beanClass) {
        SqlQuery query = new DeleteQuery();

        List<String> l = new ArrayList<String>();
        l.add(getTableName(beanClass));
        query.setEntities(l);
        query.setWhereExpression(getWhere(getPrimaryKey(beanClass)));

        return query;
    }
}
