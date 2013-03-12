/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.dao.impl;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.bean.StandardBean;
import com.leff.myorm.dao.JdbcDao;
import com.leff.myorm.exception.internal.EntityNotFoundException;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.jdbc.manager.JdbcManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación abstracta de Dao Jdbc que extiende de BaseDaoImpl.
 *
 * @param <E>
 * @param <PK>
 * @author leff
 */
public abstract class JdbcDaoImpl<E extends BaseBean, PK extends Serializable, T extends EntityNotFoundException> extends BaseDaoImpl<E, PK, T> implements JdbcDao<E, PK, T> {

    /**
     * Manager JDBC.
     */
    @Autowired
    private JdbcManager jm;

    /**
     * constructor.
     */
    public JdbcDaoImpl() {
    }

    /**
     * getter.
     *
     * @return the jm
     */
    protected final JdbcManager getJm() {
        return jm;
    }

    /**
     * setter.
     *
     * @param jm the jm to set
     */
    protected final void setJm(JdbcManager jm) {
        this.jm = jm;
    }

    /**
     * Indica si se debe o no aplicar el filtro de fechas.
     *
     * @return true si el filtro de fechas está activado. False en caso
     *         contrario.
     */
    protected final boolean isDateFilterRequired() {
        return isApplyDateFilter() && getEc().getSuperclass().equals(StandardBean.class);
    }

    /**
     * Devuelve el Id de la clave primaria como argumentos para consulta
     *
     * @param id Id de la clave primaria.
     * @return Id como array de objetos.
     */
    @SuppressWarnings("unchecked")
    protected final Object[] getArg(PK id) {
        Object[] result = null;
        if (id instanceof Object[]) {
            result = (Object[]) id;
        } else {
            result = new Object[]{id};
        }
        return result;
    }

    /**
     * Devuelve el bean como argumentos para insertar.
     *
     * @param e Bean del que extraer los argumentos.
     * @return Array de objetos con los argumentos.
     * @throws ReflectException cuando ocurre algún error al acceder al bean
     *                          mediante reflect.
     */
    protected final Object[] getArgsForInsert(E e) throws ReflectException {
        return e.getValues().toArray();
    }

    /**
     * Devuelve el bean como argumentos para actualizar.
     *
     * @param e Bean del que extraer los argumentos.
     * @return rray de objetos con los argumentos.
     *         * @throws ReflectException cuando ocurre algún error al acceder al bean
     *         mediante reflect.
     */
    protected final Object[] getArgsForUpdate(E e) throws ReflectException {
        List<Object> result = new ArrayList<Object>();
        result.addAll(e.getValues());
        result.addAll(e.getValuesPk());
        return result.toArray();
    }

    /**
     * Devuelve el bean como argumentos para borrar.
     *
     * @param e Bean del que extraer los argumentos.
     * @return rray de objetos con los argumentos.
     * @throws ReflectException cuando ocurre algún error al acceder al bean
     *                          mediante reflect.
     */
    protected final Object[] getArgsForDelete(E e) throws ReflectException {
        return e.getValuesPk().toArray();
    }

    @Override
    protected final void setupFilters() {
        //no se requiere, ya que se comprueban y establecen durante la construcción de las querys.
    }

    @Override
    protected final void clearOnFilterChange() {
        //no se requiere.
    }
}
