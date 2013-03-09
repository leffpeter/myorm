/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean.helper;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.exception.internal.ReflectException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de resultados SQL (ResultSet) a Beans.
 *
 * @param <B> cualquier B que extienda de BaseBean.
 * @author nobody.
 */
public final class BeanRowMapper<B extends BaseBean> implements RowMapper<B> {

    /**
     * the class of the bean.
     */
    private Class<B> clazz;

    /**
     * constructor.
     *
     * @param clazz
     */
    public BeanRowMapper(Class<B> clazz) {
        this.clazz = clazz;
    }

    @Override
    public B mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return BeanHelper.recover(rs, clazz);
        } catch (ReflectException ex) {
            throw new SQLException("while recovering", ex);
        }
    }
}
