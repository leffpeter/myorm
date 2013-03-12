/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jdbc.manager.impl;

import com.leff.myorm.BaseTest;
import com.leff.myorm.bean.BaseBean;
import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.exception.internal.JDBCIllegalStateException;
import com.leff.myorm.jdbc.manager.JdbcManager;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author leff
 */
public class JdbcManagerImplUnitTest extends BaseTest {

    @Autowired
    private JdbcManager jm;

    public JdbcManagerImplUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        String createDummyQuery = "CREATE TABLE DUMMY (ID VARCHAR2(500 BYTE))";
        try {
            jm.prepareStatement(createDummyQuery);
            jm.executeUpdate();
            jm.close();
        } catch (InternalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @After
    public void tearDown() {
        String dropDummyQuery = "DROP TABLE DUMMY";
        try {
            jm.prepareStatement(dropDummyQuery);
            jm.executeUpdate();
            jm.close();
        } catch (InternalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Test of prepareStatement method, of class JdbcManagerImpl.
     */
    @Test
    public void testPrepareStatement_String() throws Exception {
        System.out.println("prepareStatement");
        String query = "SELECT SYSDATE FROM DUAL";
        jm.prepareStatement(query);
    }

    /**
     * Test of preparePagingStatement method, of class JdbcManagerImpl.
     */
    @Test
    public void testPreparePagingStatement() throws Exception {
        System.out.println("preparePagingStatement");
        String query = "SELECT SYSDATE FROM DUAL";
        jm.preparePagingStatement(query);
    }

    /**
     * Test of open method, of class JdbcManagerImpl.
     */
    @Test
    public void testOpen() {
        System.out.println("open");
    }

    /**
     * Test of executeUpdate method, of class JdbcManagerImpl.
     */
    @Test
    public void testExecuteUpdate() throws Exception {
        System.out.println("executeUpdate");
        String query = "UPDATE DUMMY SET ID = ?";
//        String query = "insert into post (id,des,content,binary,stamp,tst) values (?,?,?,?,?,?)";
        try {
            System.out.println("jm state: " + jm.getCurrentState());
            jm.prepareStatement(query);
            System.out.println("jm state: " + jm.getCurrentState());

            jm.setParam(1, String.valueOf(Calendar.getInstance().getTimeInMillis()), String.class);
//            jm.setParam(2, "testdes", String.class);
//            jm.setParam(3, "testcontent", String.class);
//            jm.setParam(4, null, String.class);
//            jm.setParam(5, new Timestamp(0), Timestamp.class);
//            jm.setParam(6, null, Timestamp.class);
            jm.executeUpdate();
            System.out.println("jm state: " + jm.getCurrentState());
            assertNotNull(jm.getUpdateResult());
        } finally {
            jm.close();
            System.out.println("jm state: " + jm.getCurrentState());
        }
    }

    /**
     * Test of executeQuery method, of class JdbcManagerImpl.
     */
    @Test
    public void testExecuteQuery() throws Exception {
        System.out.println("executeQuery");
        String query = "SELECT SYSDATE FROM DUAL";
        try {
            System.out.println("jm state: " + jm.getCurrentState());
            jm.prepareStatement(query);
            System.out.println("jm state: " + jm.getCurrentState());
            jm.executeQuery();
            System.out.println("jm state: " + jm.getCurrentState());
            jm.hasResults();
            System.out.println("jm state: " + jm.getCurrentState());
            assertNotNull(jm.getResult(1, Date.class));
        } finally {
            jm.close();
            System.out.println("jm state: " + jm.getCurrentState());
        }
    }

    /**
     * Test of close method, of class JdbcManagerImpl.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        jm.close();
    }

    /**
     * Test of getResultSet method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetResultSet() {
        System.out.println("getResultSet");
        ResultSet expResult = null;
        ResultSet result = jm.getResultSet();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUpdateResult method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetUpdateResult() {
        System.out.println("getUpdateResult");
        int expResult = -1;
        int result = jm.getUpdateResult();
        assertEquals(expResult, result);
    }

    /**
     * Test of getGeneratedKey method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetGeneratedKey() throws Exception {
        System.out.println("getGeneratedKey");
        String expResult = null;
        String result = jm.getGeneratedKey();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResult method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetResult_String_Class() throws Exception {
        System.out.println("getResult");
        String query = "SELECT SYSDATE NOW FROM DUAL";
        try {
            jm.prepareStatement(query);
            jm.executeQuery();
            jm.hasResults();
            assertNotNull(jm.getResult("NOW", Date.class));
        } finally {
            jm.close();
        }
    }

    /**
     * Test of getResult method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetResult_int_Class() throws Exception {
        System.out.println("getResult");
        String query = "SELECT SYSDATE FROM DUAL";
        try {
            jm.prepareStatement(query);
            jm.executeQuery();
            jm.hasResults();
            assertNotNull(jm.getResult(1, Date.class));
        } finally {
            jm.close();
        }
    }

    /**
     * Test of hasResults method, of class JdbcManagerImpl.
     */
    @Test(expected = JDBCIllegalStateException.class)
    public void testHasResults() throws Exception {
        System.out.println("hasResults");
        boolean expResult = false;
        boolean result = jm.hasResults();
        assertEquals(expResult, result);
    }

    /**
     * Test of setParam method, of class JdbcManagerImpl.
     */
    @Test
    public void testSetParam() throws InternalException {
        System.out.println("setParam");

        String sqlTemp = "create table pruebajdbc   ("
                + "COL1   VARCHAR2(1)," +
                "  COL2   INTEGER," +
                "  COL3   LONG," +
                "  COL4   NUMBER(5,2)," +
                "  COL5   DATE," +
                "  COL6   TIMESTAMP(6)," +
                "  COL7   NUMBER(1)," +
                "  COL8   INTEGER," +
                "  COL9   CHAR(1)," +
                "  COL10  FLOAT(12)," +
                "  COL11  BLOB)";

        jm.prepareStatement(sqlTemp);
        jm.executeUpdate();

        String sqlquery = "insert into pruebajdbc values (?,?,?,?,?,?,?,?,?,?,?) ";
        jm.prepareStatement(sqlquery);
        jm.setParam(1, "1", String.class);
        jm.setParam(2, 1, Integer.class);
        jm.setParam(3, (long) 1, Long.class);
        jm.setParam(4, new BigDecimal(1), BigDecimal.class);
        jm.setParam(5, new java.sql.Date(new Date().getTime()), java.sql.Date.class);
        jm.setParam(6, new Timestamp(new Date().getTime()), Timestamp.class);
        jm.setParam(7, true, Boolean.class);
        jm.setParam(8, (short) 1, Short.class);
        jm.setParam(9, (byte) 1, Byte.class);
        jm.setParam(10, (float) 1, Float.class);
        jm.setParam(11, "".getBytes(), byte[].class);
        jm.executeUpdate();

        System.out.println("Resultado de la inserción " + jm.getUpdateResult());

        sqlquery = "select * from pruebajdbc where col1 = ? and col2 = ? and col3= ?"
                + " and col4= ? and col5= ? and col6=? and col7=? and col8= ? and"
                + " col9=? and col10=? and col11=? ";
        jm.prepareStatement(sqlquery);
        jm.setParam(1, null, String.class);
        jm.setParam(2, null, Integer.class);
        jm.setParam(3, null, Long.class);
        jm.setParam(4, null, BigDecimal.class);
        jm.setParam(5, null, java.sql.Date.class);
        jm.setParam(6, null, Timestamp.class);
        jm.setParam(7, null, Boolean.class);
        jm.setParam(8, null, Short.class);
        jm.setParam(9, null, Byte.class);
        jm.setParam(10, null, Float.class);
        jm.setParam(11, null, byte[].class);
        jm.executeQuery();
        assertEquals(jm.hasResults(), false);

        sqlquery = "select * from pruebajdbc";
        jm.prepareStatement(sqlquery);
        //jm.setParam(1, "1", String.class);
        jm.executeQuery();
        System.out.println("Resultado de la consulta " + jm.hasResults());
        //assertEquals(jm.hasResults(), true);

        assertEquals(jm.getResult(1, String.class), jm.getResult("COL1", String.class));
        assertEquals(jm.getResult(2, Integer.class), jm.getResult("COL2", Integer.class));
        assertEquals(jm.getResult(3, Long.class), jm.getResult("COL3", Long.class));
        assertEquals(jm.getResult(4, BigDecimal.class), jm.getResult("COL4", BigDecimal.class));
        assertEquals(jm.getResult(5, java.sql.Date.class), jm.getResult("COL5", java.sql.Date.class));
        assertEquals(jm.getResult(6, Timestamp.class), jm.getResult("COL6", Timestamp.class));
        assertEquals(jm.getResult(7, Boolean.class), jm.getResult("COL7", Boolean.class));
        assertEquals(jm.getResult(8, Short.class), jm.getResult("COL8", Short.class));
        assertEquals(jm.getResult(9, Byte.class), jm.getResult("COL9", Byte.class));
        assertEquals(jm.getResult(10, Float.class), jm.getResult("COL10", Float.class));
        assertEquals(jm.getResult(11, byte[].class), jm.getResult("COL11", byte[].class));

        sqlquery = "select * from pruebajdbc where col1 = ? and col2 = ? and col3= ?"
                + " and col4= ? and col5= ? and col6=? and col7=? and col8= ? and"
                + " col9=? and col10=? and col11=? ";
        jm.preparePagingStatement(sqlquery);
        jm.setParam(1, null, String.class);
        jm.setParam(2, null, Integer.class);
        jm.setParam(3, null, Long.class);
        jm.setParam(4, null, BigDecimal.class);
        jm.setParam(5, null, java.sql.Date.class);
        jm.setParam(6, null, Timestamp.class);
        jm.setParam(7, null, Boolean.class);
        jm.setParam(8, null, Short.class);
        jm.setParam(9, null, Byte.class);
        jm.setParam(10, null, Float.class);
        jm.setParam(11, null, byte[].class);
        jm.executeQuery();

        jm.preparePagingStatement(sqlquery);
        jm.setParam(1, "1", String.class);
        jm.setParam(2, 1, Integer.class);
        jm.setParam(3, (long) 1, Long.class);
        jm.setParam(4, new BigDecimal(1), BigDecimal.class);
        jm.setParam(5, new java.sql.Date(new Date().getTime()), java.sql.Date.class);
        jm.setParam(6, new Timestamp(new Date().getTime()), Timestamp.class);
        jm.setParam(7, true, Boolean.class);
        jm.setParam(8, (short) 1, Short.class);
        jm.setParam(9, (byte) 1, Byte.class);
        jm.setParam(10, (float) 1, Float.class);
        jm.setParam(11, "".getBytes(), byte[].class);
        jm.executeQuery();
    }

    /**
     * Test of prepareStatement method, of class JdbcManagerImpl.
     */
    @Test
    public void testPrepareStatement() throws Exception {
        System.out.println("prepareStatement");
        String query = "SELECT SYSDATE FROM DUAL";
        jm.prepareStatement(query);
    }

    /**
     * Test of getResult method, of class JdbcManagerImpl.
     */
    @Test(expected = JDBCIllegalStateException.class)
    public void testGetResult_Class() throws Exception {
        System.out.println("getResult");
        Class<BaseBean> type = BaseBean.class;
        BaseBean expResult = null;
        BaseBean result = jm.getResult(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentState method, of class JdbcManagerImpl.
     */
    @Test
    public void testGetCurrentState() throws InternalException {
        System.out.println("getCurrentState");
        JdbcManager.State expResult = JdbcManager.State.Ready;
        String query = "SELECT SYSDATE FROM DUAL";
        jm.prepareStatement(query);
        JdbcManager.State result = jm.getCurrentState();
        assertEquals(expResult, result);
    }
}
