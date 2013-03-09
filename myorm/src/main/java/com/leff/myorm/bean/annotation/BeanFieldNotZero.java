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
 * Anotación para indicar que un atributo de un bean nunca puede ser cero 0:
 * Normalmente, se aplica a campos que deben tener un valor distinto de cero o null (IDs).
 *
 * @author nobody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BeanFieldNotZero {
}
