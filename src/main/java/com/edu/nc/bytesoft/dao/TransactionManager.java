package com.edu.nc.bytesoft.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionManager {
    private DataSource dataSource;
    private Connection connection;
    private List<SettableConnection> clientList = new ArrayList<>();


    public TransactionManager() throws SQLException {
        dataSource = new AutoconfiguratedDataSource();
    }



    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void registerClient(SettableConnection client) {
        clientList.add(client);
    }

    public void unregisterClient(SettableConnection client) {
        clientList.remove(client);
    }

    public void unregisterAllClients() {
        clientList.clear();
    }



    public Transaction startTransaction() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        putConnectionToClients();
        return new Transaction();
    }



    private void putConnectionToClients() throws SQLException {
        clientList.removeAll(Collections.<SettableConnection>singleton(null));
        for (SettableConnection client : clientList) {
            client.setConnection(connection);
        }
    }

    public class Transaction implements AutoCloseable {

        private Transaction() {
        }

        public boolean isClosed() throws SQLException {
            return (connection == null || connection.isClosed());
        }


        public void commit() throws SQLException {
            if (isClosed()) throw new IllegalStateException();
            connection.commit();
        }

        public void rollback() throws SQLException {
            if (isClosed()) throw new IllegalStateException();
            connection.rollback();
        }

        @Override
        public void close() throws SQLException {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
    }

}
