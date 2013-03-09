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
 * Anotación para indicar el nombre de la tabla de Base de Datos asociada al Bean.
 * <p/>
 * value Cadena con el nombre de la tabla en la base de datos.
 *
 * @author nobody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BeanTable {
    /**
     * Database table name.
     *
     * @return
     */
    String value();
}
