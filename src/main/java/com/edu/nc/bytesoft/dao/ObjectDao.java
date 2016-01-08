package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.util.SqlExecutor;

import java.sql.SQLException;

public interface ObjectDao<T> extends SettableConnection {
    T getById(long id) throws SQLException, NoSuchObjectException;
    boolean delete(long id) throws NoSuchObjectException, SQLException;
    T save (T object) throws SQLException, NoSuchObjectException;
    SqlExecutor getSqlExecutor();
}
