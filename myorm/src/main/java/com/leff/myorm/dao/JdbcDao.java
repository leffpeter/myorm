/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.dao;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.exception.internal.EntityNotFoundException;
import com.leff.myorm.exception.internal.InternalException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Interfaz básica para Dao Jdbc.
 *
 * @param <E>
 * @param <PK>
 * @author leff
 */
public interface JdbcDao<E extends BaseBean, PK extends Serializable, T extends EntityNotFoundException> extends Dao {

    /**
     * Inserta un bean.
     *
     * @param bean bean a insertar.
     * @return resultado del insert.
     * @throws InternalException cuando ocurre algún error interno al crear la
     *                           sentencia correspondiente.
     */
    int insert(E bean) throws InternalException;

    /**
     * Actualiza un bean.
     *
     * @param bean bean a actualizar.
     * @return resultado del update.
     * @throws InternalException cuando ocurre algún error interno al crear la
     *                           sentencia correspondiente.
     */
    int update(E bean) throws InternalException;

    /**
     * Elimina un bean.
     *
     * @param bean bean a eliminar.
     * @return resultado del delete.
     * @throws InternalException cuando ocurre algún error interno al crear la
     *                           sentencia correspondiente.
     */
    int delete(E bean) throws InternalException;

    /**
     * Eliminar una entidad por Id.
     *
     * @param id Identificador.
     * @return resultado del delete.
     * @throws InternalException cuando ocurre algún error interno al crear la
     *                           sentencia correspondiente.
     * @throws T                 cuando no se encuentra la entidad.
     */
    int deleteById(PK id) throws InternalException, T;

    /**
     * Busca un bean por Id.
     *
     * @param id Identificador.
     * @return bean encontrado.
     * @throws T cuando no se encuentra la entidad.
     */
    E findById(PK id) throws T, InternalException;

    /**
     * Lista todos los elementos.
     *
     * @return lista de todos los beans.
     */
    List<E> listAll();

    /**
     * Cuenta todos los elementos.
     *
     * @return el número de elementos encontrados.
     */
    Long countAll();

    /**
     * Obtener la hora.
     *
     * @return hora actual.
     */
    Date systime();
}
