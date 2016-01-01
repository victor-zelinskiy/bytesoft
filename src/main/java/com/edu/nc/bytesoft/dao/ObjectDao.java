package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;

import java.sql.SQLException;

public interface ObjectDao<T> extends SettableConnection {
    T getById(long id) throws SQLException, NoSuchObjectException;
    boolean delete(T object) throws NoSuchObjectException;
    T save (T object) throws SQLException, NoSuchObjectException;
}
