/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.core;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.util.Util;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementación básica de PreparedStatementCreator para Beans.
 *
 * @param <B>
 * @author leff
 */
public final class BeanPreparedStatementCreator<B extends BaseBean> implements PreparedStatementCreator {

    /**
     * SQL Query string.
     */
    private String sql;
    /**
     * Args.
     */
    private Object[] args;
    /**
     * Primary key column names.
     */
    private String[] columnNames;

    /**
     * Create a new BeanPreparedStatementCreator for the given arguments.
     *
     * @param sql  query string.
     * @param bean bean
     * @throws ReflectException cuando ocurre algún error al recuperar datos del bean.
     */
    public BeanPreparedStatementCreator(String sql, B bean) throws ReflectException {
        this.sql = sql;
        this.args = bean.getValues().toArray();
        this.columnNames = Util.toArray(bean.getPrimaryKeyColumnNames(), String.class);
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, columnNames);
        int index = 1;
        for (Object propertyValue : args) {
            ps.setObject(index++, propertyValue);
        }
        return ps;
    }
}
