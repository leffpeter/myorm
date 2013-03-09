/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.exception.internal;

import com.leff.myorm.exception.BaseException;

/**
 * Excepción de parseo de sentencias SQL.
 *
 * @author leff
 */
public class SQLParseException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     *
     * @param message
     */
    public SQLParseException(String message) {
        super(message);
    }

    /**
     * constructor
     *
     * @param causa
     */
    public SQLParseException(Throwable causa) {
        super(causa);
    }
}
