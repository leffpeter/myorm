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

/**
 * @author leff
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:applicationContext-core.xml"})
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
}
