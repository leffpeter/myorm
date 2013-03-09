/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean;

import com.leff.myorm.bean.annotation.BeanColumn;
import com.leff.myorm.bean.annotation.BeanPrimaryKey;
import com.leff.myorm.bean.annotation.BeanStamp;
import com.leff.myorm.bean.annotation.BeanTable;
import com.leff.myorm.constant.DataModel;
import com.leff.myorm.exception.internal.ReflectException;
import com.leff.myorm.util.Util;
import com.leff.myorm.util.list.SortedList;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Bean base para beans del modelo.
 *
 * @author leff
 */
public abstract class BaseBean implements Bean {

    /**
     * Nombre de la tabla principal a la que corresponde el DAO.
     */
    private String tableName = null;
    /**
     * Campos que componen la clave primaria.
     */
    private List<String> primaryKeyFieldNames = new SortedList<String>();
    /**
     * Nombre del campo que contiene el TIMESTAMP.
     */
    private String timestampFieldName = null;
    /**
     * Mapa de campos-clave/valor
     */
    private Map<String, Object> keyMap = new TreeMap<String, Object>();
    /**
     * Timestamp.
     */
    @BeanColumn(DataModel.C_STAMP)
    private Timestamp stamp = null;
    /**
     * Backup del Timestamp.
     */
    private Timestamp stampBack = null;
    /**
     * Flag que indica si se ha realizado un backup del timestamp.
     */
    private boolean backupDone = false;
    /**
     * Campo que indica el estado de modificación del bean.
     */
    private int state = CLEAN;
    /**
     * Flag para activar desactivar el chequedo de keys a ejecutar un setKey().
     */
    private boolean validateKeysOnSet = true;

    /**
     * constructor.
     */
    public BaseBean() {
        recoverTable();
        recoverPrimaryKey();
        recoverStamp();
    }

    /**
     * constructor
     *
     * @param bean Bean
     */
    public BaseBean(Bean bean) {
        tableName = bean.getTableName();
        primaryKeyFieldNames = bean.getPrimaryKeyColumnNames();
        timestampFieldName = bean.getStampColumnName();
    }

    /**
     * Constructor protegido.
     *
     * @param validateKeysOnSet flag para activar/desactivar el chequedo de keys
     *                          válidas durante el setKey.
     */
    protected BaseBean(boolean validateKeysOnSet) {
        this();
        this.validateKeysOnSet = validateKeysOnSet;
    }

    /**
     * Comprueba si una clave es válida (declarada como parte de la clave
     * primaria).
     *
     * @param key clave.
     * @return True si la clave está en la lista de claves, false si no está o
     *         la lista no está definida.
     */
    private boolean isValidKey(String key) {
        if (validateKeysOnSet) {
            if (Util.emptyList(primaryKeyFieldNames)) {
                return false;
            }
            return primaryKeyFieldNames.contains(key);
        }
        return true;
    }

    /**
     * Añade un valor al mapa de claves.
     *
     * @param key   Nombre de la clave.
     * @param value Valor de la clave.
     */
    private void doSetKey(String key, Object value) {
        if (Util.nullObject(keyMap)) {
            keyMap = new TreeMap<String, Object>();
        }
        if (isValidKey(key)) {
            keyMap.put(key, value);
        }
    }

    /**
     * Recupera la clave primaria anotadas en el bean.
     */
    private void recoverPrimaryKey() {
        if (Util.emptyList(primaryKeyFieldNames)) {
            primaryKeyFieldNames = new SortedList<String>();
        }
        if (this.getClass().isAnnotationPresent(BeanPrimaryKey.class)) {
            BeanPrimaryKey primaryKeyAnnotation = this.getClass().getAnnotation(BeanPrimaryKey.class);
            primaryKeyFieldNames.addAll(Util.toList(primaryKeyAnnotation.value()));
        }
    }

    /**
     * Recupera el nombre del campo STAMP anotada en el bean.
     */
    private void recoverStamp() {
        if (this.getClass().isAnnotationPresent(BeanStamp.class)) {
            BeanStamp stampAnnotation = this.getClass().getAnnotation(BeanStamp.class);
            timestampFieldName = stampAnnotation.value();
        }
    }

    /**
     * Recupera el nombre de la tabla anotada en el bean.
     */
    private void recoverTable() {
        if (this.getClass().isAnnotationPresent(BeanTable.class)) {
            BeanTable tableAnnotation = this.getClass().getAnnotation(BeanTable.class);
            tableName = tableAnnotation.value();
        }
    }

    /**
     * Elimina el guión bajo del nombre del atributo.
     *
     * @param attName
     * @return
     */
    protected static String fixAttName(String attName) {
        return attName.startsWith("_") ? attName.substring(1) : attName;
    }

    @Override
    public final void setKeys(Map<String, Object> keyMap) {
        this.keyMap = keyMap;
    }

    @Override
    public final Map<String, Object> getKeyMap() {
        return keyMap;
    }

    @Override
    public final Timestamp getStamp() {
        return (Timestamp) (stamp != null ? stamp.clone() : null);
    }

    @Override
    public final void setKey(String key, Object value) {
        doSetKey(key, value);
    }

    @Override
    public final void setStamp(Timestamp stamp) {
        this.stamp = (Timestamp) (stamp != null ? stamp.clone() : null);
        stampBack = backupDone ? stampBack : stamp;
    }

    @Override
    public final void revertStamp() {
        stamp = stampBack;
        backupDone = false;
    }

    @Override
    public final String getTableName() {
        return tableName;
    }

    @Override
    public final String getStampColumnName() {
        return timestampFieldName;
    }

    @Override
    public final List<String> getPrimaryKeyColumnNames() {
        return primaryKeyFieldNames;
    }

    @Override
    public final List<String> getColumnNames() {
        List<String> fieldNames = new SortedList<String>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BeanColumn.class)) {
                BeanColumn columnAnnotation = field.getAnnotation(BeanColumn.class);
                fieldNames.add(columnAnnotation.value());
            }
        }
        return fieldNames;
    }

    /**
     * @return the newBean
     */
    @Override
    public final boolean isNew() {
        return state == NEW;
    }

    /**
     * @return the newBean
     */
    @Override
    public final boolean isModified() {
        return state == MODIFIED;
    }

    /**
     * @return
     */
    @Override
    public final boolean isDeleted() {
        return state == DELETED;
    }

    /**
     * @return
     */
    @Override
    public final boolean isClean() {
        return state == CLEAN;
    }

    /**
     * Establece el estado
     *
     * @param state
     */
    @Override
    public final void setState(int state) {
        switch (state) {
            case CLEAN:
            case NEW:
            case MODIFIED:
            case DELETED:
                this.state = state;
                break;
            default:
                this.state = CLEAN;
        }
    }

    /**
     * @return
     */
    @Override
    public final int getState() {
        return state;
    }

    /**
     * Construye un LazyBean a partir del propio objeto.
     *
     * @param <B>
     * @return LazyBean creado
     * @throws ReflectException cuando ocurre algún error al construir el objeto
     *                          con reflect.
     */
    public final <B extends BaseBean> LazyBean lazy() throws ReflectException {
        return new LazyBean(this);
    }

    /**
     * Devuelve los valores del bean como una lista de objetos.
     *
     * @param <B>
     * @return
     * @throws ReflectException cuando ocurre algún error al acceder al objeto
     *                          con reflect.
     */
    public final <B extends BaseBean> List<Object> getValues() throws ReflectException {
        return new ArrayList<Object>(this.lazy().getDataMap().values());
    }

    /**
     * Devuelve los valores del bean como una lista de objetos, obviando los
     * correspondientes a la clave primaria.
     *
     * @param <B>
     * @return
     * @throws ReflectException cuando ocurre algún error al acceder al objeto
     *                          con reflect.
     */
    public final <B extends BaseBean> List<Object> getValuesNoPk() throws ReflectException {
        List<Object> result = new ArrayList<Object>();
        LazyBean lazy = this.lazy();
        for (Entry<String, Object> entry : lazy.getDataMap().entrySet()) {
            String columnName = entry.getKey();
            if (!this.primaryKeyFieldNames.contains(columnName)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * Devuelve los valores de la clave primaria del bean como una lista de
     * objetos.
     *
     * @param <B>
     * @return
     * @throws ReflectException cuando ocurre algún error al acceder al objeto
     *                          con reflect.
     */
    public final <B extends BaseBean> List<Object> getValuesPk() throws ReflectException {
        List<Object> result = new ArrayList<Object>();
        LazyBean lazy = this.lazy();
        for (Entry<String, Object> entry : lazy.getDataMap().entrySet()) {
            String columnName = entry.getKey();
            if (this.primaryKeyFieldNames.contains(columnName)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * @return
     */
    @Override
    public final String toString() {
        StringBuilder str = new StringBuilder(this.getClass().getSimpleName());
        try {
            LazyBean lazy = this.lazy();
            str.append(lazy.toString());
        } catch (ReflectException ex) {
            str.append("<no data (reflect exception)>");
        }
        return str.toString();
    }

    /**
     * @param columnName
     * @return
     */
    private Field getField(String columnName) {
        Field result = null;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BeanColumn.class)) {
                String columnValue = field.getAnnotation(BeanColumn.class).value();
                if (columnValue.equalsIgnoreCase(columnName)) {
                    result = field;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @param from
     * @param to
     * @param value
     * @return
     */
    private static Object smartNumberCast(Number value, Class<?> clazz) throws ReflectException {
        Object result = null;
        if (value == null) {
            return null;
        }
        if (clazz.equals(BigDecimal.class)) {
            result = new BigDecimal(value.doubleValue());
        } else if (clazz.equals(BigInteger.class)) {
            result = new BigInteger(value.toString(), 10);
        } else if (clazz.equals(Integer.class)) {
            result = Integer.valueOf(value.intValue());
        } else if (clazz.equals(Long.class)) {
            result = Long.valueOf(value.longValue());
        } else if (clazz.equals(Short.class)) {
            result = Short.valueOf(value.shortValue());
        } else if (clazz.equals(Byte.class)) {
            result = Byte.valueOf(value.byteValue());
        } else if (clazz.equals(Double.class)) {
            result = Double.valueOf(value.doubleValue());
        } else if (clazz.equals(Float.class)) {
            result = Float.valueOf(value.floatValue());
        }
        return result;
    }

    /**
     * Establece el nuevo mapa de claves y distrubuye los valores a los
     * correspondientes atributos del bean.
     *
     * @param keys mapa de claves
     * @throws ReflectException
     */
    @Override
    public final void populateKeys(Map<String, Object> keys) throws ReflectException {
        if (keys == null) {
            return;
        }
        try {
            keyMap = new TreeMap<String, Object>();
            for (Entry<String, Object> entry : keys.entrySet()) {
                String keyName = entry.getKey();
                Object keyValue = entry.getValue();
                if (!(keyValue instanceof Number)) {
                    throw new ReflectException();
                }
                Field field = getField(keyName);
                Object castValue = smartNumberCast((Number) keyValue, field.getType());
                keyMap.put(keyName, castValue);
                field.setAccessible(true);
                field.set(this, castValue);
            }
        } catch (IllegalAccessException ex) {
            throw new ReflectException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectException(ex);
        }
    }

    @Override
    public final void populateKeys(KeyHolder keyHolder) throws ReflectException {
        if (keyHolder == null || keyHolder.getKeys() == null || keyHolder.getKeys().isEmpty()) {
            return;
        }
        populateKeys(keyHolder.getKeys());
    }
}
