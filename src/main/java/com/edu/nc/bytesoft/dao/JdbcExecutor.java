package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcExecutor {
    private Connection connection;

    private PreparedStatementHelper preparedStatementHelper = new PreparedStatementHelper();

    public JdbcExecutor(Connection connection) {
        this.connection = connection;
    }

    public JdbcExecutor() {
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /*private PreparedStatement prepareSingleGetIdByIdQuery(long id, String query) throws SQLException {
        PreparedStatement result = connection.prepareStatement(query);
        result.setLong(1, id);
        return result;
    }

    private PreparedStatement prepareSingleGetIdByNameQuery(String name, String query) throws SQLException {
        PreparedStatement result = connection.prepareStatement(query);
        result.setString(1, name);
        return result;
    }

    private PreparedStatement prepareSingleGetNameByIdQuery(long id, String query) throws SQLException {
        PreparedStatement result = connection.prepareStatement(query);
        result.setLong(1, id);
        return result;
    }

    private long executeSingleGetIdByIdQuery(long id, String query) throws SQLException, NoSuchObjectException {
        try (PreparedStatement statement = prepareSingleGetIdByIdQuery(id, query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            throw new NoSuchObjectException();
        }
    }

    private String executeSingleGetNameByIdQuery(long id, String query) throws SQLException, NoSuchObjectException {
        try (PreparedStatement statement = prepareSingleGetNameByIdQuery(id, query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            throw new NoSuchObjectException();
        }
    }*/


    @SuppressWarnings("unchecked")
    public <R> R execute(String query, Class<R> resultType, Object parameter) throws SQLException, NoSuchObjectException {
        try (PreparedStatement statement = prepareSingleGetDataQuery(parameter, query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return (R) preparedStatementHelper.getOperation(resultType).getOutOperation().apply(resultSet);
            }
            throw new NoSuchObjectException();
        }
    }

    @SuppressWarnings("unchecked")
    private PreparedStatement prepareSingleGetDataQuery(Object parameter, String query) throws SQLException {
        PreparedStatement result = connection.prepareStatement(query);
        preparedStatementHelper.getOperation(parameter.getClass()).getInOperation().accept(result, parameter);
        return result;
    }

    /*private long executeSingleGetIdByNameQuery(String name, String query) throws SQLException, NoSuchObjectException {
        try (PreparedStatement statement = prepareSingleGetIdByNameQuery(name, query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            throw new NoSuchObjectException();
        }
    }*/
}
