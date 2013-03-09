/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean.helper;

import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.bean.Bean;
import com.leff.myorm.bean.LazyBean;
import com.leff.myorm.bean.annotation.BeanColumn;
import com.leff.myorm.bean.annotation.BeanFieldNotZero;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.util.Util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper de Recuperación/construcción de Beans: Se basa en el uso de las
 * anotaciones @Bean*, con la que se anotan los atributos de los beans para
 * relacionarlos la base de datos.
 *
 * @author nobody
 */
@SuppressWarnings("unchecked")
public final class BeanHelper {

    /**
     * Prefijo para métodos "set".
     */
    private static final String SET_PREFIX = "set";
    /**
     * Prefijo para métodos "set".
     */
    private static final String GET_PREFIX = "get";

    /**
     * constructor.
     */
    private BeanHelper() {
    }

    /**
     * Obtiene el mapa de tipos de los atributos anotados.
     *
     * @param <T>  Cualquier T que extienda de BaseBean.
     * @param bean Bean a tratar.
     * @return Mapa de string, class con los tipos de los atributos.
     */
    public static <T extends BaseBean> Map<String, Class<?>> getTypes(T bean) {
        Map<String, Class<?>> result = new TreeMap<String, Class<?>>();
        boolean stop = false;
        Class<?> beanClass = bean.getClass();
        while (!stop) {
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(BeanColumn.class)) {
                    result.put(recoverColumnValue(field), field.getType());
                }
            }
            stop = (beanClass == BaseBean.class);
            beanClass = beanClass.getSuperclass();
        }
        return result;
    }

    /**
     * Recupera el valor de la anotación @BeanColumn
     *
     * @param field campo a inspeccionar.
     * @return valor contenido en la anotación.
     */
    private static String recoverColumnValue(Field field) {
        String value = null;
        if (field.isAnnotationPresent(BeanColumn.class)) {
            BeanColumn columnAnnotation = field.getAnnotation(BeanColumn.class);
            value = columnAnnotation.value();
        }
        return value;
    }

    /**
     * Determina el tipo del atributo anotado con @BeanColumn cuyo nombre
     * coincide con el proporcionado.
     *
     * @param <T>        cualquier T que extienda de BaseBean.
     * @param bean       Bean a tratar.
     * @param columnName Nombre de columna que debe tener @BeanColumn.
     * @return Tipo del atributo encontrado con la anotación. Null en caso
     *         contrario.
     */
    public static <T extends BaseBean> Class<?> getType(T bean, String columnName) {
        Class<?> result = null;
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BeanColumn.class)
                    && columnName.equalsIgnoreCase(recoverColumnValue(field))) {
                result = field.getType();
                break;

            }
        }
        return result;
    }

    /**
     * Determina el valor del atributo anotado con @BeanColumn cuyo nombre
     * coincide con el proporcionado.
     *
     * @param <V>        cualquier valor.
     * @param <T>        cualquier T que extiende BaseBean
     * @param bean       Bean a inspeccionar.
     * @param columnName Nombre de columna que debe tener @BeanColumn
     * @return valor del atributo encontrado. Null en caso contrario.
     * @throws ReflectException cuando no se ha podido acceder mediante reflect.
     */
    public static <V, T extends BaseBean> V getValue(T bean, String columnName) throws ReflectException {
        V result = null;
        try {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(BeanColumn.class)
                        && columnName.equalsIgnoreCase(recoverColumnValue(field))) {
                    Method getterMethod = getGetterMethod(bean.getClass(), field);
                    result = (V) getterMethod.invoke(bean, (Object[]) null);
                    break;

                }
            }
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectException(ex);
        }
        return result;
    }

    /**
     * Elimina el posible guión bajo al principio del nombre del atributo.
     *
     * @param attName nombre del atributo.
     * @return nombre del atributo sin "_".
     */
    private static String fixAttName(String attName) {
        return attName.startsWith("_") ? attName.substring(1) : attName;
    }

    /**
     * Obtiene el método SET del atributo indicado.
     *
     * @param objectClass clase objectivo.
     * @param field       campo objetivo.
     * @return método setter correspondiente.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     */
    private static Method getSetterMethod(Class<?> objectClass, Field field) throws NoSuchMethodException {
        Method result = null;
        String methodName = SET_PREFIX + Util.firstUp(fixAttName(field.getName()));
        result = objectClass.getMethod(methodName, field.getType());
        return result;
    }

    /**
     * Obtiene el método GET del atributo indicado.
     *
     * @param objectClass clase objetivo.
     * @param field       campo objetivo.
     * @return Método getter.
     * @throws NoSuchMethodException cuando no se encuentra el método.
     */
    private static Method getGetterMethod(Class<?> objectClass, Field field) throws NoSuchMethodException {
        Method result = null;
        String methodName = GET_PREFIX + Util.firstUp(fixAttName(field.getName()));
        result = objectClass.getMethod(methodName, (Class<?>[]) null);
        return result;
    }

    /**
     * Recupera un objeto del ResultSet.
     *
     * @param rs     ResultSet a tratar
     * @param type   Tipo del objeto a recuperar.
     * @param name   nombre del objeto a recuperar.
     * @param noZero flag para indicar que el objeto no puede ser cero (0): o
     *               null o valor > 0.
     * @return valor del objeto recuperado del resultset.
     * @throws SQLException cuando ocurre alguna excepción al tratar de recupera
     *                      el objeto del resultset.
     */
    private static <T> T getValue(ResultSet rs, Class<?> type, String name, boolean noZero) throws SQLException {
        Object result = null;

        if (type.equals(String.class)) {
            result = rs.getString(name);
        } else if (type.equals(Integer.class)) {
            Integer value = rs.getInt(name);
            result = noZero && value.intValue() == 0 ? null : value;
        } else if (type.equals(Long.class)) {
            Long value = rs.getLong(name);
            result = noZero && value.longValue() == 0L ? null : value;
        } else if (type.equals(BigDecimal.class)) {
            result = rs.getBigDecimal(name);
        } else if (type.equals(Date.class)) {
            result = rs.getDate(name);
        } else if (type.equals(Timestamp.class)) {
            result = rs.getTimestamp(name);
        } else if (type.equals(Boolean.class)) {
            result = rs.getBoolean(name);
        } else if (type.equals(Short.class)) {
            Short value = rs.getShort(name);
            result = noZero && value.intValue() == (short) 0 ? null : value;
        } else if (type.equals(Byte.class)) {
            Byte value = rs.getByte(name);
            result = noZero && value.intValue() == (byte) 0 ? null : value;
        } else if (type.equals(Float.class)) {
            Float value = rs.getFloat(name);
            result = noZero && value.floatValue() == (float) 0 ? null : value;
        } else if (type.equals(Array.newInstance(byte.class, 0).getClass())) {
            result = rs.getBytes(name);
        } else {
            result = rs.getObject(name);
        }

        return (T) result;
    }

    /**
     * Recupera el KeyMap de un Bean de un LazyBean.
     *
     * @param <T>
     * @param bean Bean que recibirá el keyMap.
     * @param data LazyBean con los datos.
     */
    public static <T extends Bean> void recoverKeyMap(T bean, LazyBean data) {
        for (String key : bean.getPrimaryKeyColumnNames()) {
            bean.setKey(key, data.get(key));
        }
    }

    /**
     * Recover no recursivo de Beans.
     *
     * @param <T>       cualquier T que extienda de BaseBean.
     * @param input     resultset de donde recuperar el objeto.
     * @param beanClass Clase del objeto a recuperar.
     * @return Instancia del objeto recuperado.
     * @throws ReflectException cuando ocurre algún error al construir el objeto
     *                          con reflect.
     * @throws SQLException     cuando ocurre algún error al recuperar datos del
     *                          resultset.
     */
    public static <T extends BaseBean> T recover(ResultSet input, Class<?> beanClass) throws ReflectException, SQLException {
        T result = null;
        try {
            boolean stop = false;
            result = (T) beanClass.newInstance();
            while (!stop) {
                for (Field field : beanClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(BeanColumn.class)) {
                        String value = recoverColumnValue(field);
                        Method setterMethod = getSetterMethod(result.getClass(), field);
                        setterMethod.invoke(result, getValue(input, field.getType(), value, field.isAnnotationPresent(BeanFieldNotZero.class)));
                    }
                }
                stop = (beanClass == BaseBean.class);
                beanClass = beanClass.getSuperclass();
            }
        } catch (InstantiationException ex) {
            throw new ReflectException(ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectException(ex);
        }
        return result;
    }

    /**
     * Recover.
     *
     * @param <T>       cualquier T que extienda de BaseBean.
     * @param input     LazyBean con los datos.
     * @param beanClass Clase del bean que se desea recuperar.
     * @return Instancia de la clase recuperada.
     * @throws ReflectException cuando ocurre algún error al construir el objeto
     *                          con reflect.
     */
    public static <T extends BaseBean> T recover(LazyBean input, Class<?> beanClass) throws ReflectException {
        T result = null;
        try {
            boolean stop = false;
            result = (T) beanClass.newInstance();
            while (!stop) {
                for (Field field : beanClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(BeanColumn.class)) {
                        String value = recoverColumnValue(field);
                        Method setterMethod = getSetterMethod(result.getClass(), field);
                        setterMethod.invoke(result, input.get(value));
                    }
                }
                stop = (beanClass == BaseBean.class);
                beanClass = beanClass.getSuperclass();
            }
        } catch (InstantiationException ex) {
            throw new ReflectException(ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectException(ex);
        }
        return result;
    }
}
