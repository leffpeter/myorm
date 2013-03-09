/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.util;

import com.leff.myorm.exception.internal.InternalException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metodos de utilidad para tratamiento de textos, cadenas, comprobaciones,
 * formateo, etc.
 */
public final class Util {

    /**
     * Configuracion necesaria para que el copyProperties del BeanUtils copie
     * los tipos correctamente
     */
    static {
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
    }

    /**
     * Formato fecha por defecto.
     */
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    /**
     * Días de la semana en gallego.
     */
    public static final String[] DAYS_GL = {"", "Domingo", "Luns", "Martes", "Mércores", "Xoves", "Venres", "Sábado"};
    /**
     * Meses del año en gallego.
     */
    public static final String[] MONTHS_GL = {"xaneiro", "febreiro", "marzo", "abril", "maio", "xuño", "xullo",
            "agosto", "setembro", "outubro", "novembro", "decembro"};
    /**
     * Días de la semana en español.
     */
    public static final String[] DAYS_ES = {"", "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
    /**
     * Meses del año en español.
     */
    public static final String[] MONTHS_ES = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
            "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
    private static final String PATTERN = "###,###,###,###,##0.00";
    private static final String PATTERN_OPTIONAL_DECIMAL = "###,###,###,###,###.##";
    private static final String PATTERN_PRECISION = "###,###,###,###,##0";
    private static final String PATTERN_NO_GROUPING = "##############0.00";
    private static final char DECIMAL_SEPARATOR = ',';
    private static final char GROUPING_SEPARATOR = '.';
    private static final String REG_EXP_DATE = "(((0[1-9]|[12][0-9]|3[01])([/])(0[13578]|10|12)([/])(\\d{4}))|(([0][1-9]|[12][0-9]|30)([/])(0[469]|11)([/])(\\d{4}))|((0[1-9]|1[0-9]|2[0-8])([/])(02)([/])(\\d{4}))|((29)(\\.|-|\\/)(02)([/])([02468][048]00))|((29)([/])(02)([/])([13579][26]00))|((29)([/])(02)([/])([0-9][0-9][0][48]))|((29)([/])(02)([/])([0-9][0-9][2468][048]))|((29)([/])(02)([/])([0-9][0-9][13579][26])))";

    /**
     * Constructor de la clase Util.
     */
    private Util() {
    }

    /**
     * Comprueba si la cadena es null, está vacía o sólo tiene blancos.
     *
     * @param cadena
     * @return <i>true</i> si la cadena es null, está vacía o sólo tiene
     *         blancos.
     */
    public static boolean emptyString(String cadena) {
//        if ((cadena == null) || (cadena.trim().isEmpty()) || (cadena.trim().length() == 0)) {
        if ((cadena == null) || (cadena.trim().isEmpty())) {
            return true;
        }
        return false;
    }

    /**
     * Comrpueba si un identificador es null o vale cero, por lo que no es un
     * identificador válido.
     *
     * @param id
     * @return
     */
    public static boolean emptyId(Integer id) {
        if ((id == null) || (id.equals(0))) {
            return true;
        }
        return false;
    }

    /**
     * Comrpueba si un identificador es null o vale cero, por lo que no es un
     * identificador válido.
     *
     * @param id
     * @return
     */
    public static boolean emptyId(Short id) {
        if ((id == null) || (id.equals(0))) {
            return true;
        }
        return false;
    }


    /**
     * Comrpueba si un identificador es null o vale cero, por lo que no es un
     * identificador válido.
     *
     * @param id
     * @return
     */
    public static boolean emptyId(Long id) {
        if ((id == null) || (id.equals(Long.valueOf(0)))) {
            return true;
        }
        return false;
    }

    /**
     * Comrpueba si un identificador es null o vale cero, por lo que no es un
     * identificador válido.
     *
     * @param id
     * @return
     */
    public static boolean emptyId(int id) {
        return emptyId(Integer.valueOf(id));
    }

    /**
     * Comrpueba si un Integer es null o vale cero.
     *
     * @param id
     * @return
     */
    public static boolean emptyInteger(Integer id) {
        return emptyId(id);
    }

    /**
     * Comrpueba si un Short es null o vale cero.
     *
     * @param id
     * @return
     */
    public static boolean emptyShort(Short id) {
        if ((id == null) || (id.equals((short) 0))) {
            return true;
        }
        return false;
    }

    /**
     * Comprueba si una colección es null, o es vacía o todos sus elementos son
     * null.
     *
     * @param collection
     * @return <i>true</i> si la lista es null o está vacía.
     */
    public static boolean emptyList(Collection<?> collection) {
        return ((collection == null) || (collection.isEmpty()) || (allElementsNull(collection))) ? true : false;
    }

    /**
     * Comprueba si los elementos de la colección son todos null
     *
     * @param <T>
     * @param lista
     * @return <i>true</i> si la lista es null o está vacía.
     */
    public static <T> boolean allElementsNull(Collection<T> lista) {
        Boolean result = Boolean.TRUE;
        if (!nullObject(lista)) {
            for (T element : lista) {
                if (!nullObject(element)) {
                    result = Boolean.FALSE;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Comprueba si un array es null, o el tamaño es cero.
     *
     * @param lista
     * @return <i>true</i> si el array es null o está vacío.
     */
    public static boolean emptyArray(Object[] lista) {
        return ((lista == null) || (lista.length <= 0)) ? true : false;
    }

    /**
     * @param x
     * @return x.toUpperCase().
     */
    public static String toUpperCase(String x) {
        return (x != null) ? x.toUpperCase() : x;
    }

    /**
     * Normaliza: Trim + toUpperCase
     *
     * @param x
     * @return
     */
    public static String normalize(String x) {
        return !emptyString(x) ? x.trim().toUpperCase() : x;
    }

    /**
     * Formatea una fecha con el patron indicado.
     *
     * @param d
     * @param pattern
     * @return
     */
    public static String format(Date d, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return (d != null && d.getTime() != 0L) ? sdf.format(d) : "";
    }

    /**
     * Formatea una fecha con el patron y el locale indicado.
     *
     * @param d
     * @param pattern
     * @param locale
     * @return
     */
    public static String format(Date d, String pattern, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return (d != null && d.getTime() != 0L) ? sdf.format(d) : "";
    }

    /**
     * Sustituye en la cadena los "{i}" parametros por los valores de la lista
     * values.
     *
     * @param target Cadena destino.
     * @param values Array de cadenas a sustituir.
     * @return Cadena destino con los valores sustituídos por los proporcionados
     *         en la lista.
     */
    public static String replaceValues(String target, List<String> values) {
        if (emptyString(target)) {
            return null;
        }
        if (values == null) {
            return target;
        }
        String[] params = values.toArray(new String[values.size()]);
        for (int i = 0; i < params.length; i++) {
            String pattern = "\\{" + i + "\\}";
            target = target.replaceAll(pattern, params[i]);
        }
        return target;
    }

    /**
     * Comprueba si un objeto es nulo o no.
     *
     * @param obj Objeto.
     * @return True si el objeto es null, o false en caso contrario.
     */
    public static boolean nullObject(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }

    /**
     * Transforma un array en una lista.
     *
     * @param <T>
     * @param arr
     * @return
     */
    public static <T> List<T> toList(T[] arr) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(arr));
        return list;
    }

    /**
     * Pasa una cadena de elementos separados por comas en una lista.
     *
     * @param commaSeparatedItems
     * @return
     */
    public static List<String> toList(String commaSeparatedItems) {
        List<String> list = new ArrayList<String>();
        for (String element : toList(commaSeparatedItems.split(","))) {
            list.add(trim(element));
        }
        return list;
    }

    /**
     * Transforma un array de int en una lista de Integer.
     *
     * @param arr
     * @return
     */
    public static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<Integer>();
        for (Integer element : arr) {
            list.add(element);
        }
        return list;
    }

    /**
     * Transforma una lista en un array.
     *
     * @param <T>
     * @param l
     * @param clazz
     * @return
     */
    public static <T> T[] toArray(List<T> l, Class<T> clazz) {
        T[] arr = (T[]) Array.newInstance(clazz, l.size());
        arr = l.toArray(arr);
        return arr;
    }

    /**
     * Devuelve la lista de elementos en una cadena separada por el separador
     * indicado.
     *
     * @param <T>
     * @param items     colección de elementos.
     * @param separator separador. Si es nulo o cadena vacía, se usa coma (",").
     * @return
     */
    public static <T> String getAsCSV(Collection<T> items, String separator) {
        StringBuilder result = new StringBuilder("");

        String sprt = emptyString(separator) ? "," : separator;
        for (T item : items) {
            result.append(item.toString()).append(sprt);
        }
        return emptyString(result.toString()) ? result.toString() : result.toString().substring(0, result.toString().lastIndexOf(sprt));
    }

    /**
     * Rellena con ceros hasta
     * <code>length</code>
     *
     * @param value
     * @param length
     * @return
     */
    public static String fillWithZeros(String value, int length) {

        StringBuilder zeros = new StringBuilder("");
        if (value.length() < length) {

            for (int i = 0; i < (length - value.length()); i++) {
                zeros.append("0");
            }

        }

        return zeros + value;
    }

    /**
     * Trim o null
     *
     * @param value
     * @return
     */
    public static String trim(String value) {

        if (value != null) {
            return value.trim();
        }
        return value;

    }

    /**
     * Trim o empty String ("")
     *
     * @param value
     * @return
     */
    public static String trimEmpty(String value) {

        if (value != null) {
            return value.trim();
        }
        return "";
    }

    /**
     * Pasa un
     * <code>java.util.Date</code> a
     * <code>String</code> en formato latino
     *
     * @param fecha
     * @return
     */
    public static String format(Date fecha) {
        DateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return formatter.format(fecha);
    }

    /**
     * Vacia una lista excepto su primer elemento.
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> List<T> clearExceptFirst(List<T> list) {

        List<T> newList = new ArrayList<T>();

        if (list.size() > 0) {
            newList.add(list.get(0));
        }
        return newList;
    }

    /**
     * Traduce una lista de beans en un mapa.
     *
     * @param <R>         Tipo de la key del HashMap.
     * @param <S>         Tipo del valor del HashMap.
     * @param <T>         Tipo del los elementos de la lista.
     * @param l           lista.
     * @param keyMethod   Nombre del método del bean que devuelve las claves para
     *                    el HashMap.
     * @param valueMethod Nombre del método del bean que devuelve los valores
     *                    para el HashMap.
     * @return
     * @throws InternalException
     */
    @SuppressWarnings("unchecked")
    public static <R, S, T> Map<R, S> toHashMap(List<T> l, String keyMethod, String valueMethod) throws InternalException {
        Map<R, S> result = new HashMap<R, S>();

        for (T t : l) {
            try {
                result.put((R) t.getClass().getDeclaredMethod(keyMethod, (Class[]) null).invoke(t),
                        (S) t.getClass().getDeclaredMethod(valueMethod, (Class[]) null).invoke(t));
            } catch (Exception ex) {
                throw new InternalException(ex);
            }
        }
        return result;
    }

    /**
     * Traduce una lista de beans en un mapa.
     *
     * @param <K>       Tipo de la key del HashMap.
     * @param <T>       Tipo del valor, que es el propio objeto de la lista.
     * @param l         lista
     * @param keyMethod Nombre del método del bean que devuelve las claves para
     *                  el HashMap.
     * @return
     * @throws InternalException
     */
    @SuppressWarnings("unchecked")
    public static <K, T> Map<K, T> toHashMap(List<T> l, String keyMethod) throws InternalException {
        Map<K, T> result = new HashMap<K, T>();

        for (T t : l) {
            try {
                result.put((K) t.getClass().getDeclaredMethod(keyMethod, (Class[]) null).invoke(t), t);
            } catch (Exception ex) {
                throw new InternalException(ex);
            }
        }
        return result;
    }

    /**
     * Método que crea un
     * <code>BigDecimal</code> a partir de un
     * <code>String</code> independientemente de que este venga con "." o con
     * "," separando los decimales.
     *
     * @param value Valor a convertir en <code>BigDecimal</code>
     * @return
     */
    public static BigDecimal fixComma(String value) {
        if (Util.emptyString(value)) {
            return BigDecimal.valueOf(0);
        } else {
            try {
                return new BigDecimal(value.replace(',', '.'));
            } catch (NumberFormatException e) {
                return BigDecimal.valueOf(0);
            }
        }
    }

    /**
     * @param value
     * @return
     */
    public static BigDecimal getBigDecimal(String value) {
        if (Util.emptyString(value)) {
            return BigDecimal.valueOf(0);
        } else {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                return BigDecimal.valueOf(0);
            }
        }
    }

    /**
     * Formatea un
     * <code>BigDecimal</code> con puntos y comas
     *
     * @param value
     * @return
     */
    public static String formatMoney(BigDecimal value) {
        String output = "0";

        if (value != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
            symbols.setGroupingSeparator(GROUPING_SEPARATOR);
            DecimalFormat df = new DecimalFormat(PATTERN, symbols);
            output = df.format(value);
        }

        return output;
    }

    /**
     * Formatea un
     * <code>BigDecimal</code> con puntos y comas
     *
     * @param value
     * @return
     */
    public static String formatNumber(BigDecimal value) {
        String output = "0";

        if (value != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
            symbols.setGroupingSeparator(GROUPING_SEPARATOR);

            DecimalFormat df = new DecimalFormat(PATTERN_OPTIONAL_DECIMAL, symbols);

            output = df.format(value);
        }


        return output;
    }

    /**
     * Formatea un
     * <code>BigDecimal</code> con puntos y comas indicando el numero de
     * decimales
     *
     * @param value
     * @param precision
     * @return
     */
    public static String formatNumber(BigDecimal value, int precision) {
        String output = "0";

        if (value != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
            symbols.setGroupingSeparator(GROUPING_SEPARATOR);

            String decimals = "";
            for (int i = 0; i < precision; i++) {
                if (Util.emptyString(decimals)) {
                    decimals = ".";
                }
                decimals += "0";
            }

            DecimalFormat df = new DecimalFormat(PATTERN_PRECISION + decimals, symbols);

            output = df.format(value);
        }


        return output;
    }

    /**
     * Formatea un
     * <code>BigDecimal</code> sólo con comas en la parte decimal
     *
     * @param value
     * @return
     */
    public static String formatMoneyNoGrouping(BigDecimal value) {

        String output = "0";

        if (value != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(DECIMAL_SEPARATOR);

            DecimalFormat df = new DecimalFormat(PATTERN_NO_GROUPING, symbols);

            output = df.format(value);
        }

        return output;
    }

    /**
     * Crea una lista de elementos
     * <code>amount</code> elementos de una clase
     * <code>c</code>
     *
     * @param <T>
     * @param c
     * @param amount
     * @return
     * @throws InternalException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> dummyList(Class<T> c, int amount) throws InternalException {

        List<T> dummy = new ArrayList<T>();

        for (int i = 0; i < amount; i++) {
            try {
                dummy.add((T) Class.forName(c.getName()).newInstance());
            } catch (Exception ex) {
                throw new InternalException(ex);
            }
        }


        return dummy;


    }

    /**
     * Método auxiliar para poner la primera letra de un String en mayúscula
     *
     * @param value
     * @return
     */
    public static String firstUp(String value) {
        char chars[] = value.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Cuenta el número de elementos de un tipo (clase) que aparecen en la lista
     *
     * @param <T>
     * @param list
     * @param type
     * @return
     */
    public static <T> int countItems(List<T> list, Class<?> type) {
        int count = 0;
        for (T item : list) {
            if (item.getClass().equals(type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Comprueba si una fecha es valida
     *
     * @param value
     * @return
     */
    public static boolean isDateValid(String value) {
        Pattern p = Pattern.compile(REG_EXP_DATE);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    /**
     * Devuelve el mínimo valor de la lista.
     *
     * @param values
     * @return
     */
    public static Integer min(Integer... values) {
        Integer min = null;

        for (Integer value : values) {
            if (Util.nullObject(min)) {
                min = value;
            } else {
                if (value < min) {
                    min = value;
                }
            }
        }
        return min;
    }

    /**
     * Extrae una subcadena que cumple la expresión regular indicada.
     *
     * @param target cadena donde buscar.
     * @param exp    expresión regular a aplicar
     * @return
     */
    public static String extract(String target, String exp) {
        String result = "";
        Pattern pattern = Pattern.compile(exp);
        Matcher m = pattern.matcher(target);
        while (m.find()) {
            result = m.group();
        }
        return result;
    }

    /**
     * Devuelve la fecha de hoy en un date "limpio". Solo con valor dd/MM/yyyy
     * Pone a cero las horas, minutos, segundos y milisegundos.
     *
     * @return
     */
    public static Date today() {
        return cleanDate(Calendar.getInstance().getTime());
    }

    /**
     * "Limpia" un date. Solo con valor dd/MM/yyyy Pone a cero las horas,
     * minutos, segundos y milisegundos.
     *
     * @param date
     * @return
     */
    public static Date cleanDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Devuelve un timestamp com XMLGregoriaCalendar
     *
     * @param timestamp fecha a convertir
     * @return fecha del tipo XMLGregoriaCalendar
     * @throws BaseException Si existe algun problema en la conversion
     */
    public static XMLGregorianCalendar getXMLGregorianCalendar(Timestamp timestamp)
            throws InternalException {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(timestamp.getTime());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            throw new InternalException(ex);
        }
    }

    /**
     * Devuelve un timestamp com XMLGregoriaCalendar
     *
     * @param date fecha a convertir
     * @return fecha del tipo XMLGregoriaCalendar
     * @throws BaseException Si existe algun problema en la conversion
     */
    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date)
            throws InternalException {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            throw new InternalException(ex);
        }
    }

    /**
     * Devuelve un timestamp com XMLGregoriaCalendar
     *
     * @param date fecha a convertir
     * @return fecha del tipo XMLGregoriaCalendar
     * @throws BaseException Si existe algun problema en la conversion
     */
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Convierte timestamp de XMLGregorianCalendar a java.sql.Timestamp
     *
     * @param xgc timestamp fecha a convertir
     * @return fecha del tipo XMLGregoriaCalendar
     */
    public static java.sql.Timestamp getSqlTimeStamp(XMLGregorianCalendar xgc) {
        if (xgc == null) {
            return null;
        } else {
            return new Timestamp(xgc.toGregorianCalendar().getTime().getTime());
        }
    }

    /**
     * Comprueba si algún elemento de una lista está presente en otra.
     *
     * @param <T>
     * @param source lista fuente sobre la que se buscarán elementos del target
     * @param target lsita objetivo que contiene los elementos que se buscarán.
     * @return True si algún elemento de target está presente en source. False
     *         en caso contrario.
     */
    public static <T> boolean containsAny(List<T> source, List<T> target) {
        boolean any = false;
        if (!emptyList(source) && !emptyList(target)) {
            for (T t : target) {
                if (source.contains(t)) {
                    any = true;
                    break;
                }
            }
        }
        return any;
    }

    /**
     * Comprueba si algún elemento de una lista está presente en otra.
     *
     * @param <T>
     * @param source lista fuente sobre la que se buscarán elementos del target
     * @param target lsita objetivo que contiene los elementos que se buscarán.
     * @return True si algún elemento de target está presente en source. False
     *         en caso contrario.
     */
    public static <T> boolean containsAny(List<T> source, T... target) {
        return containsAny(source, toList(target));
    }

    /**
     * Comprueba si algún elemento de una lista está presente en otra.
     *
     * @param <T>
     * @param source     lista fuente sobre la que se buscarán elementos del target
     * @param ignoreCase Flag para indicar si se debe diferenciar o no
     *                   mayúsculas y minúsculas
     * @param target     lsita objetivo que contiene los elementos que se buscarán.
     * @return True si algún elemento de target está presente en source. False
     *         en caso contrario.
     */
    public static <T> boolean containsAny(List<T> source, boolean ignoreCase, T... target) {
        return containsAny(source, toList(target), ignoreCase);
    }

    /**
     * Comprueba si algún elemento de una lista está presente en otra.
     *
     * @param <T>
     * @param source     lista fuente sobre la que se buscarán elementos del target
     * @param target     lsita objetivo que contiene los elementos que se buscarán.
     * @param ignoreCase Flag para indicar si se debe diferenciar o no
     *                   mayúsculas y minúsculas cuando T es de tipo String
     * @return True si algún elemento de target está presente en source. False
     *         en caso contrario.
     */
    public static <T> boolean containsAny(List<T> source, List<T> target, boolean ignoreCase) {
        boolean any = false;
        if (!emptyList(source) && !emptyList(target)) {
            for (T currentTarget : target) {
                if (currentTarget.getClass().equals(String.class)
                        && ignoreCase) {
                    for (T currentSource : source) {
                        if (((String) currentTarget).equalsIgnoreCase((String) currentSource)) {
                            any = true;
                            break;
                        }
                    }
                } else {
                    if (source.contains(currentTarget)) {
                        any = true;
                        break;
                    }
                }
            }
        }
        return any;
    }

    /**
     * Calcula el número de meses comprendidos entre dos fechas.
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int monthsBetween(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // different date might have different offset
        cal1.setTime(date1);
        //long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);

        cal2.setTime(date2);
        //long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);

        // Use integer calculation, truncate the decimals
        //int hr1 = (int) (ldate1 / 3600000); //60*60*1000
        //int hr2 = (int) (ldate2 / 3600000);

        //int days1 = hr1 / 24;
        //int days2 = hr2 / 24;

        //int dateDiff = days2 - days1;
        //int weekOffset = (cal2.get(Calendar.DAY_OF_WEEK) - cal1.get(Calendar.DAY_OF_WEEK)) < 0 ? 1 : 0;
        //int weekDiff = dateDiff / 7 + weekOffset;
        int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
        int monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);

        return monthDiff;
    }

    /**
     * Devuelve la lista de identificadores de mes que están entre dos fechas.
     * Ejemplos: 15/03/2011 a 15/05/2011 devuelve {3, 4, 5} 15/12/2011 a
     * 15/01/2012 devuelve {12, 1}
     *
     * @param iniDate
     * @param endDate
     * @return
     */
    public static List<Integer> monthsInTime(Date iniDate, Date endDate) {
        List<Integer> months = new ArrayList<Integer>();

        Calendar iniCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        iniCalendar.setTime(iniDate);
        Integer iniMonth = iniCalendar.get(Calendar.MONTH);

        endCalendar.setTime(endDate);
        Integer endMonth = endCalendar.get(Calendar.MONTH);

        int month = iniMonth;
        months.add(iniMonth + 1);

        if (month > endMonth) {
            for (int i = month; i <= 11; i++) {
                if (!months.contains(i + 1)) {
                    months.add(i + 1);
                }
            }
            month = 0;
        }
        for (int i = month; i <= endMonth; i++) {
            if (!months.contains(i + 1)) {
                months.add(i + 1);
            }
        }
        if (!months.contains(endMonth + 1)) {
            months.add(endMonth + 1);
        }

        return months;
    }

    /**
     * Comprueba si una cadena termina con un sufijo, ignorando mayúsculas y
     * minúsculas.
     *
     * @param target String a comprobar.
     * @param suffix sufijo.
     * @return
     */
    public static boolean endsWithIgnoreCase(String target, String suffix) {
        if (!emptyString(target) && !emptyString(suffix)) {
            return target.toUpperCase().endsWith(suffix.toUpperCase());
        }
        return false;
    }

    /**
     * @param one
     * @param other
     * @return
     */
    public static boolean equals(Object one, Object other) {
        return one == null ? other == null : one.equals(other);
    }

    /**
     * Comprueba si un elemento es vació: null, o cadena vacía, entero 0.
     *
     * @param <T>
     * @param obj
     * @return
     */
    public static <T> boolean emptyValue(T obj) {
        if (obj == null) {
            return true;
        } else if (obj.getClass().equals(String.class)) {
            return Util.emptyString((String) obj);
        } else if (obj.getClass().equals(Integer.class)) {
            return ((Integer) obj == 0);
        }

        return false;
    }

    /**
     * Comprueba si algun elemento es nulo o vacío.
     *
     * @param <T>
     * @param elements
     * @return
     */
    public static <T> boolean anyEmpty(T... elements) {
        for (T element : elements) {
            if (emptyValue(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rellena con
     * <code>filler</code> hasta
     * <code>length</code> por la izquierda
     *
     * @param target cadena objetivo
     * @param lenght longitud total del resultado deseado
     * @param filler cadena a usar para rellenar
     * @return
     */
    public static String fillLeft(String target, int lenght, char filler) {

        StringBuilder fillStr = new StringBuilder("");
        if (target.length() < lenght) {

            for (int i = 0; i < (lenght - target.length()); i++) {
                fillStr.append(filler);
            }
        }

        return fillStr + target;
    }

    /**
     * Rellena con
     * <code>filler</code> hasta
     * <code>length</code> por la derecha
     *
     * @param target
     * @param lenght
     * @param filler
     * @return
     */
    public static String fillRight(String target, int lenght, char filler) {

        StringBuilder fillStr = new StringBuilder("");

        if (target.length() < lenght) {
            for (int i = 0; i < (lenght - target.length()); i++) {
                fillStr.append(filler);
            }
        }

        return target + fillStr;
    }


    /**
     * Formatea un string con el formato correcto a un date
     */
    public static Date stringToDate(String strDate, String dateFormat) throws ParseException {
        Date date;
        DateFormat formatter;
        formatter = new SimpleDateFormat(dateFormat);
        date = formatter.parse(strDate);
        return date;
    }

    /**
     * Sustituye un valor nulo por un valor especificado.
     *
     * @param <T>
     * @param a   valor a sustituir
     * @param b   valor por el que se sutituye
     * @return un valor distinto de nulo
     */
    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }
}
