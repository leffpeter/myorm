package com.leff.myorm;

import com.leff.myorm.exception.internal.InternalException;
import com.leff.myorm.util.spring.SpringContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//,"classpath:/applicationContext-economico-ws-mappings.xml"

/**
 * @author adrian.riobo
 */
@RunWith(SpringJUnit4ClassRunner.class)
//"classpath:applicationContext-test-persistence.xml",
//  "classpath:applicationContext-core-model-test.xml",
//,  
//    "classpath:/applicationContext-core.xml"
@ContextConfiguration(locations = {
        "classpath:applicationContext-core-model.xml"})
@Ignore
public class BaseTest {

    /**
     * get connection.
     *
     * @param dataSource
     * @return
     * @throws InternalException
     */
    protected final Connection getConnection(final String dataSource) throws InternalException {
        String datasourceName = (dataSource != null) ? dataSource : "dataSource";
        DataSource router = (DataSource) SpringContext.getBean(datasourceName);
        if (router != null) {
            try {
                return router.getConnection();
            } catch (SQLException ex) {
                throw new InternalException(ex);
            }
        } else {
            throw new InternalException();
        }
    }

    @Before
    public void baseSetup() {

    }

    @After
    public void baseTearDown() {

    }
    //    /**
//     * Log para test.
//     */
//    private final Log log = new Log(getClass());
//
//    /**
//     * Obtiene el log para test.
//     * @return log
//     */
//    public Log getLog() {
//        return log;
//    }
//
//
//    @BeforeClass
//    public static void setUpBeforeClass() {
//    }
//
//    @AfterClass
//    public static void tearDownAfterClass() {
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        //Configuracion de log4j
//        BasicConfigurator.configure();
//        //Inicializacion de mocks
////        MockitoAnnotations.initMocks(this);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        BasicConfigurator.resetConfiguration();
//    }
}
