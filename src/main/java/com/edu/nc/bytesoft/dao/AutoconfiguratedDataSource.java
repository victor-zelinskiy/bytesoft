package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.Log;
import com.google.common.io.Resources;
import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class AutoconfiguratedDataSource implements DataSource {

    private static Log LOG = Log.get(AutoconfiguratedDataSource.class);

    private boolean isConfigurated = false;

    private final OracleDataSource dataSource = new OracleDataSource();

    public AutoconfiguratedDataSource() throws SQLException {
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!isConfigurated) loadConfiguration();
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (!isConfigurated) loadConfiguration();
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource.getConnection();
    }

    private void loadConfiguration() {
        try (InputStream is = Resources.getResource("db/connection.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(is);
            dataSource.setURL(properties.getProperty("db.url"));
            dataSource.setUser(properties.getProperty("db.user"));
            dataSource.setPassword(properties.getProperty("db.password"));
            isConfigurated = true;
        } catch (IOException e) {
            LOG.error("Cannot load connection properties", e);
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
