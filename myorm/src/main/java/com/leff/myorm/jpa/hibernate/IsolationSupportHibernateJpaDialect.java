/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.jpa.hibernate;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Extensión de HibernateJpaDialect con soporte Isolation Support.
 *
 * @author leff
 */
public final class IsolationSupportHibernateJpaDialect extends HibernateJpaDialect {

    private static final long serialVersionUID = 1L;

    /**
     * This method is overridden to set custom isolation levels on the
     * connection.
     *
     * @param entityManager Manager de entidades
     * @param definition    Definición de transacción.
     * @return instancia de IsolationSupportSessionTransactionData con
     *         isolation.
     * @throws PersistenceException
     * @throws SQLException
     * @throws TransactionException
     */
    @Override
    public Object beginTransaction(final EntityManager entityManager, final TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {

        if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
            getSession(entityManager).getTransaction().setTimeout(definition.getTimeout());
        }
        Connection conn = getJdbcConnection(entityManager, definition.isReadOnly()).getConnection();
        Integer pisl = DataSourceUtils.prepareConnectionForTransaction(conn, definition);
        entityManager.getTransaction().begin();
        Object tdfhd = prepareTransaction(entityManager, definition.isReadOnly(), definition.getName());

        return new IsolationSupportSessionTransactionData(tdfhd, pisl, conn);
    }

    /*
     * (non-Javadoc) @see
     * org.springframework.orm.jpa.vendor.HibernateJpaDialect#cleanupTransaction(java.lang.Object)
     */
    @Override
    public void cleanupTransaction(Object transactionData) {
        super.cleanupTransaction(((IsolationSupportSessionTransactionData) transactionData).getSessionTransactionDataFromHibernateTemplate());
        ((IsolationSupportSessionTransactionData) transactionData).resetIsolationLevel();
    }

    /**
     * clase de soporte para mantener los datos de la transacción.
     */
    private static class IsolationSupportSessionTransactionData {

        /**
         * Transaction data.
         */
        private final Object sessionTransactionDataFromHibernateJpaTemplate;
        /**
         * Previous isolation level.
         */
        private final Integer previousIsolationLevel;
        /**
         * Connection.
         */
        private final Connection connection;

        /**
         * constructor.
         *
         * @param sessionTransactionDataFromHibernateJpaTemplate
         *
         * @param previousIsolationLevel
         * @param connection
         */
        public IsolationSupportSessionTransactionData(Object sessionTransactionDataFromHibernateJpaTemplate, Integer previousIsolationLevel, Connection connection) {
            this.sessionTransactionDataFromHibernateJpaTemplate = sessionTransactionDataFromHibernateJpaTemplate;
            this.previousIsolationLevel = previousIsolationLevel;
            this.connection = connection;
        }

        /**
         * reset the isoliation level.
         */
        public void resetIsolationLevel() {
            if (this.previousIsolationLevel != null) {
                DataSourceUtils.resetConnectionAfterTransaction(connection, previousIsolationLevel);
            }
        }

        /**
         * get transaction data.
         *
         * @return
         */
        public Object getSessionTransactionDataFromHibernateTemplate() {
            return this.sessionTransactionDataFromHibernateJpaTemplate;
        }
    }
}
