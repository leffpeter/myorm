/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.dao;

/**
 * Interfaz básica para Dao
 *
 * @author leff
 */
public interface Dao {

    /**
     * Indica si el dao está configurado para aplicar el filtro por fechas o no.
     *
     * @return True si está activado el filtro, False en caso contrario.
     */
    boolean isApplyDateFilter();

    /**
     * Permite establecer el flag para que el dao aplique el filtro de fechas.
     *
     * @param apply Flag de filtro de fechas.
     */
    void setApplyDateFilter(boolean apply);
}
