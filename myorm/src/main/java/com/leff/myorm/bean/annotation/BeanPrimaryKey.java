/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leff.myorm.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para indicar los nombres de las columnas de la Base de datos
 * que forman la clave primaria de la tabla asociada al Bean.
 * <p/>
 * value Cadenas con los nombres de columna de la base de datos.
 *
 * @author nobody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BeanPrimaryKey {
    /**
     * Database primary key columns.
     *
     * @return
     */
    String[] value();
}
