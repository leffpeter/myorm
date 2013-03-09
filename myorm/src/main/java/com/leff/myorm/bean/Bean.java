package com.leff.myorm.bean;

import com.leff.myorm.exception.internal.ReflectException;
import org.springframework.jdbc.support.KeyHolder;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Interfaz para Beans.
 *
 * @author leff
 */
public interface Bean extends Serializable, Cloneable {

    /**
     * Valor de estado del bean: NO MODIFICADO.
     */
    int CLEAN = 0;
    /**
     * Valor de estado del bean: NUEVO.
     */
    int NEW = 1;
    /**
     * Valor de estado del bean: MODIFICADO.
     */
    int MODIFIED = 2;
    /**
     * Valor de estado del bean: BORRADO.
     */
    int DELETED = 3;

    /**
     * Devuelve el mapa de claves/valores (campos que componen la clave
     * primaria, y sus valores).
     *
     * @return
     */
    Map<String, Object> getKeyMap();

    /**
     * Devuelve el nombre de la tabla a la que hacer referencia el Bean.
     *
     * @return
     */
    String getTableName();

    /**
     * Devuelve el nombre del campo que contiene el timestamp.
     *
     * @return
     */
    String getStampColumnName();

    /**
     * Devuelve el valor del timestamp que contiene el Bean.
     *
     * @return
     */
    Timestamp getStamp();

    /**
     * Devuelve los campos que forman la clave primaria.
     *
     * @return
     */
    List<String> getPrimaryKeyColumnNames();

    /**
     * Añade un valor a una clave. (mapa de campos PK/valores PK).
     *
     * @param key   Nombre de la clave.
     * @param value Valor de la clave.
     */
    void setKey(String key, Object value);

    /**
     * Establece un nuevo mapa de campos PK/valores PK.
     *
     * @param keyMap nuevo mapa de claves.
     */
    void setKeys(Map<String, Object> keyMap);

    /**
     * Establece un nuevo valor del timestamp del Bean.
     *
     * @param stamp
     */
    void setStamp(Timestamp stamp);

    /**
     * Restaura el valor original del timestamp.
     */
    void revertStamp();

    /**
     * Indica si el bean es nuevo.
     *
     * @return
     */
    boolean isNew();

    /**
     * Indica si el bean ha sido modificado.
     *
     * @return
     */
    boolean isModified();

    /**
     * Indica si el bean ha sido eliminado.
     *
     * @return
     */
    boolean isDeleted();

    /**
     * Indica si el bean no ha sido afectado por ningún cambio.
     *
     * @return
     */
    boolean isClean();

    /**
     * Establece el estado
     *
     * @param state
     */
    void setState(int state);

    /**
     * @return
     */
    int getState();

    /**
     * Devuelve los campos que forman la clave primaria.
     *
     * @return
     */
    List<String> getColumnNames();

    /**
     * Distribuye un nuevo mapa de claves, estableciendo el mapa y guardando en
     * cada atributo el valor correspondiente.
     *
     * @param keyMap nuevo mapa de claves.
     * @throws ReflectException cuando algún objeto del mapa no es un Number, o
     *                          no se ha podido establecer el valor en el atributo mediante reflect.
     */
    void populateKeys(Map<String, Object> keyMap) throws ReflectException;

    /**
     * Distribuye un nuevo mapa de claves a partir de un keyholder,
     * estableciendo el mapa y guardando en cada atributo el valor
     * correspondiente.
     *
     * @param keyHolder keyholder.
     * @throws ReflectException cuando algún objeto del mapa no es un Number, o
     *                          no se ha podido establecer el valor en el atributo mediante reflect.
     */
    void populateKeys(KeyHolder keyHolder) throws ReflectException;
}
