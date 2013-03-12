/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leff.myorm.exception.internal;


/**
 * Excepción de entidad no encontrada.
 *
 * @author leff
 */
public class EntityNotFoundException extends InternalException {

    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     */
    public EntityNotFoundException() {
        super();
    }

    /**
     * constructor
     *
     * @param cause
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
