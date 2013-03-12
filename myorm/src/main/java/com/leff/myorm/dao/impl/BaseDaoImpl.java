/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.dao.impl;

import com.leff.myorm.dao.Dao;
import com.leff.myorm.exception.internal.EntityNotFoundException;
import com.leff.myorm.jdbc.sql.query.SqlQuery;
import com.leff.myorm.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

/**
 * Implementación abstracta de Dao que extiende de JdbcDaoSupport.
 *
 * @param <E>
 * @param <PK>
 * @author leff
 */
@Transactional(propagation = Propagation.MANDATORY)
public abstract class BaseDaoImpl<E, PK, T extends EntityNotFoundException> extends JdbcDaoSupport implements Dao {

    /**
     * Datasource.
     */
    @Autowired
    private DataSource dataSource;
    /**
     * Clase de la entidad asociada al Dao.
     */
    private Class<E> ec;
    /**
     * Clase de la clave primaria de la entidad asociada al Dao.
     */
    private Class<PK> pk;

    private Class<T> t;
    /**
     * Flag para filtro de fechas estandar: fec_alta - fec_baja. Por defecto,
     * true.
     */
    private boolean applyDateFilter = true;

    /**
     * constructor.
     */
    public BaseDaoImpl() {
    }

    /**
     * Método de inicialización del Dao.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public final void postConstruct() {
        setDataSource(dataSource);
        ec = ((Class<E>) GenericTypeResolver.resolveTypeArguments(getClass(), BaseDaoImpl.class)[0]);
        pk = ((Class<PK>) GenericTypeResolver.resolveTypeArguments(getClass(), BaseDaoImpl.class)[1]);
        t = ((Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), BaseDaoImpl.class)[2]);
    }

    /**
     * Método de destrucción del Dao.
     */
    @PreDestroy
    public final void preDestroy() {
    }

    /**
     * @return the ec
     */
    public final Class<E> getEc() {
        return ec;
    }

    /**
     * @param ec the ec to set
     */
    public final void setEc(Class<E> ec) {
        this.ec = ec;
    }

    /**
     * @return the pk
     */
    public final Class<PK> getPk() {
        return pk;
    }

    /**
     * @param pk the pk to set
     */
    public final void setPk(Class<PK> pk) {
        this.pk = pk;
    }

    @Override
    public final boolean isApplyDateFilter() {
        return applyDateFilter;
    }

    @Override
    public final void setApplyDateFilter(boolean applyDateFilter) {
        this.applyDateFilter = applyDateFilter;
        clearOnFilterChange();
    }

    /**
     * Devuelve la lista de elementos en una cadena separada por comas.
     *
     * @param <T>   cualquier T que implemente toString().
     * @param items colección de elementos a transformar en CSV.
     * @return Cadena con el toString() de los elementos, separados por comas
     *         ",".
     */
    protected final <T> String getCSV(Collection<T> items) {
        return Util.getAsCSV(items, ",");
    }

    /**
     * Devuelve la lista de elementos en una cadena separada por comas y con el
     * alias indicado.
     *
     * @param <T>   cualquier T que implemente toString().
     * @param items colección de elementos a transformar en CSV.
     * @param alias adena con el alias a establecer.
     * @return
     */
    protected final <T> String getCSV(Collection<T> items, String alias) {
        return setAlias(getCSV(items), alias);
    }

    /**
     * Devuelve la lista de campos separados por coma con el alias indicado
     *
     * @param commaSeparatedFields cadena con elementos separados por coma ",".
     * @param alias                cadena con el alias a establecer.
     * @return cadena con los elementos separados por coma y con el alias
     *         indicado.
     */
    protected final String setAlias(String commaSeparatedFields, String alias) {
        StringBuilder result = new StringBuilder("");

        for (String field : commaSeparatedFields.split(",")) {
            result.append(alias).append(".").append(Util.trim(field)).append(", ");
        }
        return result.substring(0, result.lastIndexOf(","));
    }

    /**
     * Añade un filtro por un campo a un bloque WHERE.
     *
     * @param where  bloque where donde añadir el filtro.
     * @param field  campo del filtro.
     * @param alias  alias del campo.
     * @param addAnd flag para indicar si se debe añadir o no un conector AND al
     *               where después del filtro (sin espacios adicionales).
     */
    protected final void addFilter(StringBuilder where, final String field, final String alias, final boolean addAnd) {
        if (!where.toString().endsWith(" ")) {
            where.append(" ");
        }
        where.append(alias).append(".").append(Util.trim(field)).append(SqlQuery.SQL_EQUALS_QUESTION);
        where.append(addAnd ? " " + SqlQuery.SQL_AND : "");
    }

    /**
     * Añade una join al from.
     *
     * @param from     bloque from.
     * @param table    tabla de join
     * @param fieldOne campo de la tabla origen.
     * @param fieldTwo campo de la tabla destino.
     * @param aliasOne alias del origen.
     * @param aliasTwo alia del destino.
     * @param outer    indica si se debe incluir OUTEr
     * @param left     indica si el outer debe ser LEFT o RIGHT.
     */
    protected final void addJoin(StringBuilder from, final String table, final String fieldOne,
                                 final String fieldTwo, final String aliasOne, final String aliasTwo, boolean outer, boolean left) {
        if (!from.toString().endsWith(" ")) {
            from.append(" ");
        }
        if (outer) {
            from.append(left ? SqlQuery.SQL_LEFT_OUTER_JOIN : SqlQuery.SQL_RIGHT_OUTER_JOIN);
        } else {
            from.append(SqlQuery.SQL_JOIN);
        }
        from.append(" ").append(table).append(" ").append(aliasTwo).append(" ").append(SqlQuery.SQL_ON).append(" ").append(aliasOne).append(".").append(fieldOne).append(" ").append(SqlQuery.SQL_EQUAL).append(" ").append(aliasTwo).append(".").append(fieldTwo);
    }

    /**
     * Añade una join al from.
     *
     * @param from     bloque from.
     * @param table    tabla de join
     * @param field    campo de join.
     * @param aliasOne alias del origen.
     * @param aliasTwo alia del destino.
     * @param outer    indica si se debe incluir OUTEr
     * @param left     indica si el outer debe ser LEFT o RIGHT.
     */
    protected final void addJoin(StringBuilder from, final String table, final String field,
                                 final String aliasOne, final String aliasTwo, boolean outer, boolean left) {
        addJoin(from, table, field, field, aliasOne, aliasTwo, outer, left);
    }

    /**
     * Construye un bloque WHERE con los nombres de los campos del mapa,
     * estableciendo el "is null" en aquellos cuyo valor en el mapa son nulos.
     *
     * @param map mapa de campos/valores.
     * @return Expresion a usar como parte WHERE de una query SQL.
     */
    protected final String getWhere(final Map<String, Object> map) {
        return getWhere(map, null);
    }

    /**
     * Construye un bloque WHERE con los nombres de los campos del mapa,
     * estableciendo el "is null" en aquellos cuyo valor en el mapa son nulos.
     *
     * @param map   mapa de campos/valores.
     * @param alias alias a establecer.
     * @return Expresion a usar como parte WHERE de una query SQL.
     */
    protected final String getWhere(final Map<String, Object> map, String alias) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String theAlias = Util.emptyString(alias) ? "" : alias + ".";
        StringBuilder result = new StringBuilder("");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (Util.nullObject(entry.getValue())) {
                result.append(theAlias).append(entry.getKey()).append(" ").append(SqlQuery.SQL_IS_NULL).append(" ").append(SqlQuery.SQL_AND).append(" ");
            } else {
                result.append(theAlias).append(entry.getKey()).append(SqlQuery.SQL_EQUALS_QUESTION).append(" ").append(SqlQuery.SQL_AND).append(" ");
            }
        }
        return result.substring(0, result.lastIndexOf(SqlQuery.SQL_AND));
    }

    /**
     * Genera una expresión de filtro por fechas.
     *
     * @param beginFieldName nombre del campo de la fecha inicial.
     * @param endFieldName   nombre del campo de la fecha final.
     * @param alias          alias a establecer.
     * @param strictBegin    Flag para indicar si la fecha inicial debe ser
     *                       estrictamente menor a la actual.
     * @param strictEnd      Flag para indicar si la fecha final debe ser
     *                       estrictamente mayor a la actual.
     * @return
     */
    protected abstract String getDateFilterExp(String beginFieldName, String endFieldName, String alias, boolean strictBegin, boolean strictEnd);

    /**
     * Genera una expresión de filtro por fechas para las fechas estandar:
     * FEC_ALTA - FEC_BAJA.
     *
     * @param alias alias de la tabla definido.
     * @return expresión de filtro de fechas estandar listo para incluir en un
     *         WHERE.
     */
    protected abstract String getStandardDateFilterExp(String alias);

    /**
     * Prepara los filtros establecidos.
     */
    protected abstract void setupFilters();

    /**
     * Limpieza tras un cambio en los filtros: para evitar posible caché de
     * persistencia.
     */
    protected abstract void clearOnFilterChange();

    public Class<T> getT() {
        return t;
    }

    public void setT(Class<T> t) {
        this.t = t;
    }
}