/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean;

import java.io.Serializable;

/**
 * Clase que representa un Integer "not zero": Integer null o not null > 0.
 *
 * @author leff
 */
public final class NumberNotZero implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * valor del NumberNotZero
     */
    private Number value;

    /**
     *
     */
    public NumberNotZero() {
    }

    /**
     * constructor.
     *
     * @param value
     */
    public NumberNotZero(int value) {
        this.value = value == 0 ? null : Integer.valueOf(value);
    }

    /**
     * constructor.
     *
     * @param value
     */
    public NumberNotZero(Integer value) {
        setValue(value);
    }

    /**
     * @param intValue
     */
    public NumberNotZero(String intValue) {
        this.value = Integer.valueOf(intValue);
    }

    /**
     * @param value
     * @return
     */
    public static NumberNotZero valueOf(Integer value) {
        return new NumberNotZero(value);
    }

    /**
     * @param value
     * @return
     */
    public static NumberNotZero valueOf(int value) {
        return new NumberNotZero(value);
    }

    /**
     * @param value
     * @return
     */
    public static NumberNotZero valueOf(String value) {
        return new NumberNotZero(value);
    }

    /**
     * @return
     */
    public int intValue() {
        return value != null ? value.intValue() : 0;
    }

    /**
     * @return the value
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Number value) {
        this.value = value != null ? (value.longValue() == 0L ? null : value) : null;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}
