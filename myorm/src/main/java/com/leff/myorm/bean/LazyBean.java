/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean;

import com.leff.myorm.bean.annotation.BeanColumn;
import com.leff.myorm.constant.DataModel;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.util.Util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

/**
 * Bean "perezoso": Los atributos del bean se mantienen en un mapa, mapeados
 * por el nombre de la columna de la tabla asociada.
 *
 * @author leff
 */
public final class LazyBean implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    /**
     * Prefijo para métodos "set".
     */
    private static final String GET_PREFIX = "get";
    /**
     * Separador usado en los nombres.
     */
    private static final String SEPARATOR = DataModel.SEPARATOR;
    /**
     * Mapa de atributo, valor.
     */
    private Map<String, Object> dataMap;

    /**
     * constructor.
     */
    public LazyBean() {
        dataMap = new TreeMap<String, Object>();
    }

    /**
     * constructor
     *
     * @param <B>
     * @param bean
     * @throws ReflectException
     */
    public <B extends BaseBean> LazyBean(B bean) throws ReflectException {
        this();
        lazy(bean);
    }

    /**
     * Obtiene el método GET del atributo indicado.
     *
     * @param objectClass
     * @param field
     * @return
     * @throws NoSuchMethodException
     */
    private static Method getGetterMethod(Class<?> objectClass, Field field) throws NoSuchMethodException {
        Method result = null;
        String methodName = GET_PREFIX + Util.firstUp(fixAttName(field.getName()));
        result = objectClass.getMethod(methodName, (Class<?>[]) null);
        return result;
    }

    /**
     * Recupera el valor de la anotación
     *
     * @param field
     * @return
     */
    private String recoverColumnValue(Field field) {
        String value = null;
        if (field.isAnnotationPresent(BeanColumn.class)) {
            BeanColumn columnAnnotation = field.getAnnotation(BeanColumn.class);
            value = columnAnnotation.value();
        }
        return value;
    }

    /**
     * Elimina el guión bajo del nombre del atributo.
     *
     * @param attName
     * @return
     */
    private static String fixAttName(String attName) {
        return attName.startsWith(SEPARATOR) ? attName.substring(1) : attName;
    }

    /**
     * Establece el valor del atributo.
     *
     * @param name  Nombre del atributo.
     * @param value Valor del atributo.
     */
    public void set(String name, Object value) {
        dataMap.put(name, value);
    }

    /**
     * Devuelve el valor del atributo con el nombre indicado.
     *
     * @param name
     * @return
     */
    public Object get(String name) {
        return dataMap.get(name);
    }

    /**
     * Limpia el bean.
     */
    public void clear() {
        dataMap.clear();
    }

    /**
     * @return
     */
    @Override
    public LazyBean clone() {
        LazyBean cloned;
        try {
            cloned = (LazyBean) super.clone();
            cloned.dataMap = new TreeMap<String, Object>();
            cloned.dataMap.putAll(dataMap);
        } catch (CloneNotSupportedException ex) {
            cloned = null;
        }
        return cloned;
    }

    /**
     * Limpia y carga en el bean todos los atributos del bean que se le pasa.
     *
     * @param <B>
     * @param bean
     * @throws ReflectException
     */
    public <B extends BaseBean> void lazy(B bean) throws ReflectException {
        this.clear();
        try {
            boolean stop = false;
            Class<?> beanClass = bean.getClass();
            while (!stop) {
                for (Field field : beanClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(BeanColumn.class)) {
                        String value = recoverColumnValue(field);
                        Method getterMethod = getGetterMethod(bean.getClass(), field);
                        this.set(value, getterMethod.invoke(bean, (Object[]) null));
                    }
                }
                stop = (beanClass == BaseBean.class);
                beanClass = beanClass.getSuperclass();
            }
        } catch (Exception ex) {
            throw new ReflectException(ex);
        }
    }

    /**
     * @return the dataMap
     */
    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    /**
     * Devuelve los valores contenidos en el mapa en forma de Object[].
     *
     * @return
     */
    public Object[] asObjectArray() {
        return dataMap != null ? dataMap.values().toArray() : null;
    }

    /**
     * @param dataMap the dataMap to set
     */
    protected void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = new TreeMap<String, Object>(dataMap);
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return dataMap != null ? dataMap.toString() : "<no data>";
    }
}
