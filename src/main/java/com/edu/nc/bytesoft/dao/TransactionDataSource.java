package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.Log;
import com.google.common.io.Resources;
import oracle.jdbc.pool.OracleDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Callable;

public class TransactionDataSource extends OracleDataSource {

    private static Log LOG = Log.get(TransactionDataSource.class);

    private boolean isConfigurated = false;

    public TransactionDataSource() throws SQLException {
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!isConfigurated) loadConfiguration();
        return super.getConnection();
    }

    private void loadConfiguration() {
        try (InputStream is = Resources.getResource("db/connection.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(is);
            super.setURL(properties.getProperty("db.url"));
            super.setUser(properties.getProperty("db.user"));
            super.setPassword(properties.getProperty("db.password"));
            isConfigurated = true;
        } catch (IOException e) {
            LOG.error("Cannot load connection properties", e);
        }
    }

    public <T> T invoke(Callable<T> c) throws Exception {
        return c.call();
    }

    @Override
    public synchronized void setURL(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void setUser(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void setPassword(String s) {
        throw new UnsupportedOperationException();
    }
}
