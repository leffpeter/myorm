/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.exception.internal;

/**
 * Excepción de estado ilegal de JDBC.
 * Esta excepción se lanza cuando se invoca un método del manager JDBC (JdbcManager)
 * de forma equivocada. Por ejemplo, getResult() antes de hasResults();
 *
 * @author leff
 */
public class JDBCIllegalStateException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     *
     * @param message
     */
    public JDBCIllegalStateException(String message) {
        super(message);
    }

    /**
     * constructor
     *
     * @param message
     * @param cause
     */
    public JDBCIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * constructor
     *
     * @param cause
     */
    public JDBCIllegalStateException(Throwable cause) {
        super(cause);
    }

    /**
     * constructor
     */
    public JDBCIllegalStateException() {
        super();
    }
}
