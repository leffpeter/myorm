package com.leff.myorm.exception;

/**
 * Base exception
 */
public abstract class BaseException extends java.lang.Exception {

    private static final long serialVersionUID = 1L;

    private String message;
    private String code;

    /**
     * Construcuto vacio
     */
    public BaseException() {
    }

    /**
     * @param t
     */
    public BaseException(Throwable t) {
        super(t);
    }

    /**
     * Constructor de Exception
     *
     * @param t
     * @param msg
     */
    public BaseException(Throwable t, String msg) {
        this(t);
        this.setMessage(msg);
    }

    /**
     * Constructor indicando message de la exception
     *
     * @param message message que cause exception
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * Obtiene el codigo de la exception
     *
     * @return codigo
     */
    public final String getCode() {
        return code;
    }

    /**
     * Fija el codigo de la exception
     *
     * @param code codigo
     */
    public final void setCode(String code) {
        this.code = code;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }
}
