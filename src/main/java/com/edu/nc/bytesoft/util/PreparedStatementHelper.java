package com.edu.nc.bytesoft.util;

import org.apache.commons.lang3.ArrayUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PreparedStatementHelper {
    private Map<Class, OperationHolder> operations = new HashMap<>();
    private int index;

    public PreparedStatementHelper() {
        operations.put(String.class, new OperationHolder<>((preparedStatement, o) -> preparedStatement.setString(index++, (String) o), resultSet -> resultSet.getString(1)));
        operations.put(Long.class, new OperationHolder<>((preparedStatement, o) -> preparedStatement.setLong(index++, (Long) o), resultSet -> resultSet.getLong(1)));

        operations.put(long[].class, new OperationHolder<>(resultSet -> {
            List<Long> list = new ArrayList<>();
            do {
                list.add(resultSet.getLong(1));
            } while (resultSet.next());
            return ArrayUtils.toPrimitive(list.toArray(new Long[list.size()]));
        }));

        operations.put(String[].class, new OperationHolder<>(resultSet -> {
            List<String> list = new ArrayList<>();
            do {
                list.add(resultSet.getString(1));
            } while (resultSet.next());
            return list.toArray(new String[list.size()]);
        }));
    }


    public OperationHolder getOperation(Class type) {
        return operations.get(type);
    }


    public void resetIndex() {
        index = 1;
    }

    public class OperationHolder<T> {
        private BiConsumer<PreparedStatement, T> inOperation;
        private Function<ResultSet, T> outOperation;

        private OperationHolder(CheckedBiConsumer<PreparedStatement, T> inOperation, CheckedFunction<ResultSet, T> outOperation) {
            this.inOperation = inOperation;
            this.outOperation = outOperation;
        }

        private OperationHolder(CheckedFunction<ResultSet, T> outOperation) {
            this.outOperation = outOperation;
        }

        private OperationHolder(CheckedBiConsumer<PreparedStatement, T> inOperation) {
            this.inOperation = inOperation;
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
