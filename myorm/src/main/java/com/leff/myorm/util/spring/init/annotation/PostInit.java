package com.leff.myorm.util.spring.init.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods, annotated with this annotation will be called in the given order
 * after the full constuction of Application context (all the beans constructed
 * and the PostProcessors finished to run). Those methods can't have parameters.
 *
 * @author leff
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface PostInit {

    /**
     * The order in which this post initializer should run. Default is undefined
     * order.
     *
     * @return Set meaningful number in respect to other initializares if order
     *         is important.
     */
    int order() default 0;
}