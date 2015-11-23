package com.edu.nc.bytesoft.dao;

import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.BaseEntity;

import java.sql.SQLException;

public interface ObjectDao<T extends BaseEntity> extends SettableConnection {
    T getObjectById(long id) throws SQLException, NoSuchObjectException;
    boolean updateObject(T object) throws NoSuchObjectException;
    boolean deleteObject(T object) throws NoSuchObjectException;
    boolean addObject(T object);
}
