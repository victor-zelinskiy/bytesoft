package com.edu.nc.bytesoft.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PreparedStatementHelper {
    private Map<Class, OperationHolder> operations = new HashMap<>();

    public PreparedStatementHelper() {
        operations.put(String.class, new OperationHolder<>((preparedStatement, o) -> preparedStatement.setString(1, (String) o), resultSet -> resultSet.getString(1)));
        operations.put(Long.class, new OperationHolder<>((preparedStatement, o) -> preparedStatement.setLong(1, (Long) o), resultSet -> resultSet.getLong(1)));
    }


    public OperationHolder getOperation(Class type) {
        return operations.get(type);
    }




    public class OperationHolder<T> {
        private BiConsumer<PreparedStatement, T> inOperation;
        private Function<ResultSet, T> outOperation;

        public OperationHolder(CheckedBiConsumer<PreparedStatement, T> inOperation, CheckedFunction<ResultSet, T> outOperation) {
            this.inOperation = inOperation;
            this.outOperation = outOperation;
        }

        public BiConsumer<PreparedStatement, T> getInOperation() {
            return inOperation;
        }

        public Function<ResultSet, T> getOutOperation() {
            return outOperation;
        }
    }

    @FunctionalInterface
    private interface CheckedFunction<T, R> extends Function<T, R> {
        @Override
        default R apply(T elem) {
            try {
                return checkedApply(elem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        R checkedApply(T t) throws Exception;
    }

    @FunctionalInterface
    private interface CheckedBiConsumer<T, U> extends BiConsumer<T, U>{
        @Override
        default void accept(T t, U u) {
            try {
                checkedAccept(t, u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        void checkedAccept(T t, U u) throws Exception;
    }


}
