package com.leff.myorm.exception.internal;


/**
 * Error durante alguna operación mediante Reflect.
 */
public class ReflectException extends InternalException {

    private static final long serialVersionUID = 1L;

    /**
     * Mensaje de la excepción
     */
    public ReflectException() {
        super();
    }

    /**
     * Mensaje de la excepción
     */
    public ReflectException(Throwable t) {
        super(t);
    }
}
