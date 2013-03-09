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
 * Anotación para indicar el nombre de la columna de Base de Datos que
 * contiene el STAMP asociada al Bean para chequeo de timestamp.
 * <p/>
 * value Cadena con el nombre de la columna de la base de datos.
 *
 * @author nobody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BeanStamp {
    /**
     * Database stamp column name.
     *
     * @return
     */
    String value();
}
