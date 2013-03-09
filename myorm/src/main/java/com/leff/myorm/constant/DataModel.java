/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.constant;

/**
 * Constantes generales del modelo de datos.
 *
 * @author leff
 */
public final class DataModel {

    /**
     * Separador usado en los nombres.
     */
    public static final String SEPARATOR = "_";
    /**
     * Prefijo de identificadores (claves primarias).
     */
    public static final String PK_PREFIX = "ID" + SEPARATOR;
    /**
     * Prefijo para tablas COM (modelo Común).
     */
    public static final String COM_PREFIX = "COM" + SEPARATOR;
    /**
     * Prefijo para tablas GEC (modelo Gestión económica).
     */
    public static final String GEC_PREFIX = "GEC" + SEPARATOR;
    /**
     * Prefijo para tablas SLC (modelo Solicitud).
     */
    public static final String SLC_PREFIX = "SLC" + SEPARATOR;
    /**
     * Prefijo para tablas EXE (modelo Ejecución).
     */
    public static final String EXE_PREFIX = "EXE" + SEPARATOR;
    /**
     * Sin prefijo.
     */
    public static final String NO_PREFIX = "";
    /**
     * Prefijo para secuencias.
     */
    public static final String SQ_PREFIX = "SQ" + SEPARATOR;
    /**
     * Prefijo para secuencias.
     */
    public static final String VW_PREFIX = "VW" + SEPARATOR;
    /**
     * Prefijo de históricos sin separador.
     */
    public static final String HST_PREFIX_NO_SEPARATOR = "HST";
    /**
     * Prefijo de históricos.
     */
    public static final String HST_PREFIX = HST_PREFIX_NO_SEPARATOR + SEPARATOR;
    /**
     * Nombre por defecto de las columnas Timestamp, TST.
     */
    public static final String C_STAMP = "TST";
    /**
     * Nombre por defecto de las columnas Observaciones.
     */
    public static final String C_OBS = "OBS";
    /**
     * Nombre por defecto de las columnas fecha de alta.
     */
    public static final String C_FEC_ALTA = "FEC_ALTA";
    /**
     * Nombre por defecto de las columnas fecha de baja.
     */
    public static final String C_FEC_BAJA = "FEC_BAJA";
    /**
     * Longitud por defecto de columnas tipo Abreviatura.
     */
    public static final int DEFAULT_ABR_LENGTH = 2;
    /**
     * Longitud por defecto de columnas tipo Código.
     */
    public static final int DEFAULT_CODE_LENGTH = 20;
    /**
     * Longitud por defecto de columnas tipo Nombre.
     */
    public static final int DEFAULT_NAME_LENGTH = 250;
    /**
     * Longitud por defecto de columnas tipo Descripción.
     */
    public static final int DEFAULT_DES_LENGTH = 500;
    /**
     * Longitud por defecto de columnas tipo Observaciones.
     */
    public static final int DEFAULT_OBS_LENGTH = 500;
    /**
     * Definición de columna que soporta un booleano.
     */
    public static final String BOOLEAN_COLUMN_DEF = "CHAR(1)";
    /**
     * Orden de recuperación de histórico ancendente por STAMP.
     */
    public static final int HST_ORDER_BY_STAMP_ASC = 0;
    /**
     * Orden de recuperación de histórico descendente por STAMP.
     */
    public static final int HST_ORDER_BY_STAMP_DESC = 1;
    /**
     * Estrategia de recuperación de histórico mediante actualización.
     */
    public static final int HST_RESTORE_UPDATE_STRATEGY = 1;
    /**
     * Estrategia de recuperación de histórico mediante borrado e inserción.
     */
    public static final int HST_RESTORE_DELETE_INSERT_STRATEGY = 2;
    /**
     * Tabla de Históricos.
     */
    public static final String T_HST = COM_PREFIX + "HST";
    /**
     * Columna ID_HST de tabla HST.
     */
    public static final String C_HST_ID_HST = "ID_HST";
    /**
     * Columna ID_USR de tabla HST.
     */
    public static final String C_HST_ID_USR = "ID_USR";
    /**
     * Columna ID_OPR de tabla HST.
     */
    public static final String C_HST_ID_OPR = "ID_OPR";
    /**
     * Columna ID_TMT de tabla HST.
     */
    public static final String C_HST_ID_TMT = "ID_TMT";
    /**
     * Columna ITR de tabla HST.
     */
    public static final String C_HST_ITR = "ITR";
    /**
     * Columna TST de tabla HST.
     */
    public static final String C_HST_TST = "TST";
    /**
     * Todas las columnas de la tabla HST separadas por coma.
     */
    public static final String C_HST_ALL =
            C_HST_ID_HST + ", "
                    + C_HST_ID_USR + ", "
                    + C_HST_ID_OPR + ", "
                    + C_HST_ID_TMT + ", "
                    + C_HST_ITR + ", "
                    + C_HST_TST;
    /**
     * Tabla USER_TAB_COLS de Oracle.
     */
    public static final String T_USER_TAB_COLS = "USER_TAB_COLS";
    /**
     * Vista sobre USER_TAB_COLS de Oracle.
     */
    public static final String VW_USER_TAB_COLS = COM_PREFIX + VW_PREFIX + T_USER_TAB_COLS;
    /**
     * Columna TABLE_NAME de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_TABLE_NAME = "TABLE_NAME";
    /**
     * Columna COLUMN_NAME de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_COLUMN_NAME = "COLUMN_NAME";
    /**
     * Columna DATA_LENGTH de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_DATA_LENGTH = "DATA_LENGTH";
    /**
     * Columna DATA_TYPE de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_DATA_TYPE = "DATA_TYPE";
    /**
     * Columna DATA_PRECISION de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_DATA_PRECISION = "DATA_PRECISION";
    /**
     * Columna DATA_SCALE de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_DATA_SCALE = "DATA_SCALE";
    /**
     * Columna COLUMN_ID de tabla USER_TAB_COLS.
     */
    public static final String C_USER_TAB_COLS_COLUMN_ID = "COLUMN_ID";
    /**
     * Todas las columnas de la tabla USER_TAB_COLS separadas por coma.
     */
    public static final String C_USER_TAB_COLS_ALL =
            C_USER_TAB_COLS_TABLE_NAME + ", "
                    + C_USER_TAB_COLS_COLUMN_NAME + ", "
                    + C_USER_TAB_COLS_DATA_LENGTH + ", "
                    + C_USER_TAB_COLS_DATA_TYPE + ", "
                    + C_USER_TAB_COLS_DATA_PRECISION + ", "
                    + C_USER_TAB_COLS_DATA_SCALE + ", "
                    + C_USER_TAB_COLS_COLUMN_ID;
    /**
     * Tabla de auditoría.
     */
    public static final String T_ADT = COM_PREFIX + "ADT";
    /**
     * Columna ID_ADT de tabla ADT.
     */
    public static final String C_ADT_ID_ADT = "ID_ADT";
    /**
     * Columna IP_USR de tabla ADT.
     */
    public static final String C_ADT_IP_USR = "IP_USR";
    /**
     * Columna TBL de tabla ADT.
     */
    public static final String C_ADT_TBL = "TBL";
    /**
     * Columna COND de tabla ADT.
     */
    public static final String C_ADT_COND = "COND";
    /**
     * Columna STC_SQL de tabla ADT.
     */
    public static final String C_ADT_STC_SQL = "STC_SQL";
    /**
     * Columna ID_TP_STC de tabla ADT.
     */
    public static final String C_ADT_ID_TP_STC = "ID_TP_STC";
    /**
     * Columna ID_OPR de tabla ADT.
     */
    public static final String C_ADT_ID_OPR = "ID_OPR";
    /**
     * Columna ID_USR de tabla ADT.
     */
    public static final String C_ADT_ID_USR = "ID_USR";
    /**
     * Columna TST de tabla ADT.
     */
    public static final String C_ADT_TST = C_STAMP;

    /**
     * clave de Hint para caché hibernate.
     */
    public static final String HIBERNATE_CACHEABLE_HINT_KEY = "org.hibernate.cacheable";

    /**
     * Representa el estado incial de tramitacion de cualquier entidad.
     */
    public static final Short INITIAL_STATE = Integer.valueOf(0).shortValue();

    /**
     * private constructor.
     */
    private DataModel() {
    }
}
