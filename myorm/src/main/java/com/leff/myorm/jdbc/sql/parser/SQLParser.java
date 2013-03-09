package com.leff.myorm.jdbc.sql.parser;

import com.leff.myorm.exception.internal.SQLParseException;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para parsear sentencias SQL.
 *
 * @author leff
 */
public final class SQLParser {

    /**
     * Identificador de operacion INSERT.
     */
    public static final int INSERT = 1;
    /**
     * Identificador de operacion SELECT.
     */
    public static final int SELECT = 2;
    /**
     * Identificador de operacion UPDATE.
     */
    public static final int UPDATE = 3;
    /**
     * Identificador de operacion DELETE.
     */
    public static final int DELETE = 4;
    private static final String SQL_EMPTY = "";
    private static final String SQL_SPACE = " ";
    private static final String SQL_COMMA = ",";
    private static final char SQL_CHAR_COMMA = ',';
    private static final String SQL_EQUALS = "=";
    private static final String SQL_ASTERISK = "*";
    private static final String SQL_FROM = "FROM";
    private static final String SQL_WHERE = "WHERE";
    private static final String SQL_SET = "SET";
    private static final String SQL_OUTER = "OUTER";
    private static final String SQL_VALUES = "VALUES";
    private static final String SQL_SELECT = "SELECT";
    private static final String SQL_INSERT = "INSERT";
    private static final String SQL_UPDATE = "UPDATE";
    private static final String SQL_DELETE = "DELETE";
    private static final String REGEX_PARENTESIS = "\\(|\\)";
    private static final String REGEX_AND_OR = "((\\s)+AND(\\s)+)|((\\s)+OR(\\s)+)";
    private static final String REGEX_NAME = "(\\s)*(([A-Z_])+\\.){0,1}([A-Z_])+(\\s)*=(\\s)*\\?(\\s)*";
    private static final String REGEX_CONDITION = "(\\s)*(([A-Z_])+\\.){0,1}([A-Z_])+(\\s)*([=<>!]+|LIKE)(\\s)*\\?(\\s)*";
    private static final String REGEX_NAME_IN_PARENTESIS = "(\\s)+";
    /**
     * Cadena con la sentencia SQL.
     */
    private String queryString = null;
    /**
     * Operación.
     */
    private int operation = -1;
    /**
     * Bloque WHERE.
     */
    private String preWhere = SQL_EMPTY;
    /**
     * Bloque SET.
     */
    private String set = SQL_EMPTY;

    /**
     * Parseo del FROM en caso de un SELECT o DELETE.
     *
     * @return from Bloque from resultante.
     */
    private String parseFromSelectDelete() {
        StringBuilder fromBuilder = new StringBuilder(SQL_EMPTY);
        String[] values;
        String[] tokens;
        Pattern pattern = null;
        Matcher matcher = null;

        values = queryString.split(SQL_FROM);
        values = values[1].split(SQL_WHERE, 2);
        fromBuilder.append(values[0].trim());

        if (values.length > 1) {
            preWhere = values[1];
        } else {
            preWhere = SQL_EMPTY;
        }

        values = fromBuilder.toString().split(SQL_COMMA);
        fromBuilder = new StringBuilder(SQL_EMPTY);

        for (int z = 0; z < values.length; z++) {
            tokens = values[z].trim().split(SQL_SPACE);
            if (tokens[0].equals(SQL_OUTER)) {
                fromBuilder.append(tokens[1]);
            } else {
                fromBuilder.append(tokens[0]);
            }
            fromBuilder.append(SQL_COMMA);
        }
        fromBuilder.substring(0, fromBuilder.length() - 1);

        pattern = Pattern.compile(REGEX_AND_OR);
        matcher = pattern.matcher(preWhere);

        preWhere = matcher.replaceAll(SQL_COMMA);
        values = preWhere.split(SQL_COMMA);
        preWhere = SQL_EMPTY;

        for (int z = 0; z < values.length; z++) {
            pattern = Pattern.compile(REGEX_NAME);
            matcher = pattern.matcher(values[z]);

            if (matcher.matches()) {
                preWhere += values[z] + SQL_COMMA + SQL_SPACE;
            }
        }
        if (!SQL_EMPTY.equals(preWhere)) {
            preWhere = preWhere.substring(0, preWhere.lastIndexOf(SQL_CHAR_COMMA));
        }
        return fromBuilder.toString();
    }

    /**
     * Parseo del FROM en caso de un UPDATE.
     *
     * @return from Bloque from resultante.
     */
    private String parseFromUpdate() {
        String from = SQL_EMPTY;
        String[] values;
        Pattern pattern = null;
        Matcher matcher = null;

        values = queryString.split(SQL_WHERE, 2);
        String[] otherValues = values[0].split(SQL_SET);
        from = otherValues[0].split(REGEX_NAME_IN_PARENTESIS)[1];
        set = otherValues[1];
        preWhere = values[1];

        pattern = Pattern.compile(REGEX_AND_OR);
        matcher = pattern.matcher(preWhere);

        preWhere = matcher.replaceAll(SQL_COMMA);

        pattern = Pattern.compile(REGEX_PARENTESIS);
        matcher = pattern.matcher(preWhere);

        preWhere = matcher.replaceAll(SQL_SPACE);
        values = preWhere.split(SQL_COMMA);
        preWhere = SQL_EMPTY;

        for (int z = 0; z < values.length; z++) {
            pattern = Pattern.compile(REGEX_CONDITION);
            matcher = pattern.matcher(values[z]);
            if (matcher.matches()) {
                preWhere += values[z] + SQL_COMMA + SQL_SPACE;
            }
        }
        if (!SQL_EMPTY.equals(preWhere)) {
            preWhere = preWhere.substring(0, preWhere.lastIndexOf(SQL_CHAR_COMMA));
        }
        return from;
    }

    /**
     * Parseo del FROM en caso de un INSERT.
     *
     * @return from Bloque from resultante.
     */
    private String parseFromInsert() {
        String from = SQL_EMPTY;
        String[] values;

        String queryStringPartido = queryString.replaceAll(REGEX_PARENTESIS, SQL_SPACE);
        values = queryStringPartido.split(SQL_SPACE);
        from = values[2];

        values = queryString.split(from, 2);

        values = values[1].replaceAll(REGEX_PARENTESIS, SQL_EMPTY).split(SQL_VALUES);

        String[] atts = values[0].split(SQL_COMMA);
        String[] vals = values[1].split(SQL_COMMA);

        preWhere = SQL_EMPTY;
        for (int i = 0; i < atts.length; i++) {
            preWhere += atts[i].trim() + SQL_SPACE + SQL_EQUALS + SQL_SPACE + vals[i].trim() + SQL_COMMA + SQL_SPACE;
        }
        if (!SQL_EMPTY.equals(preWhere)) {
            preWhere = preWhere.substring(0, preWhere.lastIndexOf(SQL_CHAR_COMMA));
        }

        return from;
    }

    /**
     * Parseo del bloque WHERE.
     *
     * @return Bolque where resultante.
     * @throws SQLParseException Cuando ocurre algún error durante el parseo.
     */
    public String parseWhere() throws SQLParseException {
        if (operation == -1) {
            parseFrom();
        }
        return SQL_EMPTY.equals(preWhere) ? SQL_ASTERISK : preWhere.trim();
    }

    /**
     * Parseo del bloque SET para el caso de una UPDATE.
     *
     * @return Bloque set resultante.
     * @throws SQLParseException Cuando ocurre algún error durante el parseo.
     */
    public String parseSet() throws SQLParseException {
        if (operation == -1) {
            parseFrom();
        }
        return set.trim();
    }

    /**
     * Parseo de la operación.
     *
     * @return Identificador de la operación SQL resultante.
     * @throws SQLParseException En caso de no encontrar una operación SQL
     *                           válida.
     */
    public int parseOperation() throws SQLParseException {
        StringTokenizer tk;
        String token = SQL_EMPTY;

        tk = new StringTokenizer(queryString);
        if (tk.hasMoreTokens()) {
            token = tk.nextToken();
        }

        if (token.indexOf(SQL_SELECT) != -1) {
            operation = SELECT;
        }
        if (token.indexOf(SQL_UPDATE) != -1) {
            operation = UPDATE;
        }
        if (token.indexOf(SQL_INSERT) != -1) {
            operation = INSERT;
        }
        if (token.indexOf(SQL_DELETE) != -1) {
            operation = DELETE;
        }
        if (operation == -1) {
            throw new SQLParseException("unknown SQL query");
        }
        return operation;
    }

    /**
     * Parseo del bloque FROM
     *
     * @return
     * @throws SQLParseException Cuando ocurre algún error durante el parseo.
     */
    public String parseFrom() throws SQLParseException {
        String from = SQL_EMPTY;

        if (operation == -1) {
            parseOperation();
        }
        switch (operation) {

            case SELECT:
            case DELETE:
                from = parseFromSelectDelete();
                break;
            case UPDATE:
                from = parseFromUpdate();
                break;
            case INSERT:
                from = parseFromInsert();
                break;
            default:
                break;
        }
        return from.trim();
    }

    /**
     * Constructor de la clase SQLParser.
     *
     * @param queryString Cadena con la sentencia SQL.
     */
    public SQLParser(String queryString) {
        this.queryString = queryString != null ? queryString.trim().toUpperCase() : null;
    }
}
