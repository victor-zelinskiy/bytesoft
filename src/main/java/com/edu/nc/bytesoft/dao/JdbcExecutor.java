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


    @SuppressWarnings("unchecked")
    public <R> R execute(String query, Class<R> resultType, Object... parameters) throws SQLException, NoSuchObjectException {
        try (PreparedStatement statement = prepareSingleGetDataQuery(query, parameters);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return (R) preparedStatementHelper.getOperation(resultType).getOutOperation().apply(resultSet);
            }
            throw new NoSuchObjectException();
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized PreparedStatement prepareSingleGetDataQuery(String query, Object... parameters) throws SQLException {
        PreparedStatement result = connection.prepareStatement(query);
        preparedStatementHelper.resetIndex();
        for (Object parameter : parameters) {
            preparedStatementHelper.getOperation(parameter.getClass()).getInOperation().accept(result, parameter);
        }
        return result;
    }

}
