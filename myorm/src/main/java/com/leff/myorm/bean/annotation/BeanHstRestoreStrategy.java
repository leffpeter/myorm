/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean.annotation;

import com.leff.myorm.constant.DataModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para indicar la estrategia de restauración asociada al Bean.
 * <p/>
 * value identificador de estrategia de restauración de histórico.
 *
 * @author nobody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BeanHstRestoreStrategy {

    /**
     * restore strategy Id.
     *
     * @return
     */
    int value() default DataModel.HST_RESTORE_UPDATE_STRATEGY;

    ;
}
