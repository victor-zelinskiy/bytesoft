package com.edu.nc.bytesoft.dao.impl;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.TransactionManager;
import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.BaseEntity;
import com.edu.nc.bytesoft.model.NamedEntity;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObjectDaoReflect<T extends BaseEntity> implements ObjectDao<T> {

    private static final Log LOG = Log.get(ObjectDaoReflect.class);

    private static final String QUERY_GET_OBJECT_BY_ID = "SELECT ATYPE.CODE, " +
            "ATTS.VALUE, ATTS.DATE_VALUE, OBJR.REFERENCE\n" +
            "FROM ATTRTYPE ATYPE\n" +
            "LEFT JOIN ATTRIBUTES ATTS ON (ATTS.ATTR_ID = ATYPE.ATTR_ID AND ATTS.OBJECT_ID = ?)\n" +
            "LEFT JOIN OBJREFERENCE OBJR ON (OBJR.ATTR_ID = ATYPE.ATTR_ID AND OBJR.OBJECT_ID = ?)\n" +
            "WHERE ATYPE.OBJECT_TYPE_ID IN \n" +
            "  (\n" +
            "    SELECT OBT.OBJECT_TYPE_ID\n" +
            "    FROM OBJTYPE OBT\n" +
            "    START WITH OBT.OBJECT_TYPE_ID = \n" +
            "      (\n" +
            "        SELECT OBJECT_TYPE_ID\n" +
            "        FROM OBJECTS\n" +
            "        WHERE OBJECT_ID = ?\n" +
            "      )\n" +
            "    CONNECT BY PRIOR OBT.PARENT_ID = OBT.OBJECT_TYPE_ID\n" +
            "  )\n" +
            "AND (ATTS.VALUE IS NOT NULL \n" +
            "OR ATTS.DATE_VALUE IS NOT NULL\n" +
            "OR OBJR.REFERENCE IS NOT NULL)";

    private static final String QUERY_GET_ATTRIBUTES_BY_TYPE = "SELECT CODE\n" +
            "FROM ATTRTYPE\n" +
            "WHERE OBJECT_TYPE_ID IN \n" +
            "                    (\n" +
            "                      SELECT OBT.OBJECT_TYPE_ID\n" +
            "                      FROM OBJTYPE OBT\n" +
            "                      START WITH OBT.OBJECT_TYPE_ID = \n" +
            "                      \n" +
            "                        (\n" +
            "                          SELECT OBJECT_TYPE_ID\n" +
            "                          FROM OBJTYPE\n" +
            "                          WHERE CODE = ?\n" +
            "                          AND ROWNUM = 1\n" +
            "                        )\n" +
            "                      CONNECT BY PRIOR OBT.PARENT_ID = OBT.OBJECT_TYPE_ID\n" +
            "                    )";

    private static final String QUERY_INSERT_OBJECT = "INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, NAME) SELECT OBJ_SEQ.NEXTVAL, OBJT.OBJECT_TYPE_ID, ? FROM OBJTYPE OBJT WHERE OBJT.CODE = ? AND ROWNUM = 1";

    private static final String QUERY_GET_OBJ_SEQ_CURRVAL = "SELECT OBJ_SEQ.CURRVAL FROM DUAL";

    private Connection connection;

    private Class<T> objectClass;

    private boolean isLazy;

    private final EntityConverter entityConverter = new EntityConverter();

    private final EnumConverter enumConverter = new EnumConverter();

    private final BeanUtilsBean utilsBean = new BeanUtilsBean();


    public ObjectDaoReflect(Class<T> objectClass, boolean isLazy) {
        this.isLazy = isLazy;
        this.objectClass = objectClass;
    }

    public ObjectDaoReflect(Class<T> objectClass, TransactionManager transactionManager, boolean isLazy) {
        this(objectClass, isLazy);
        transactionManager.registerClient(this);
    }

    public ObjectDaoReflect(Class<T> objectClass, Connection connection, boolean isLazy) {
        this(objectClass, isLazy);
        this.connection = connection;
    }

    public ObjectDaoReflect(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectDaoReflect(Class<T> objectClass, TransactionManager transactionManager) {
        this(objectClass, transactionManager, false);
    }

    public ObjectDaoReflect(Class<T> objectClass, Connection connection) {
        this(objectClass, connection, false);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    @Override
    public T getObjectById(long id) throws SQLException, NoSuchObjectException {
        T result = extractObject(id);
        if (result == null) throw new NoSuchObjectException();
        result.setId(id);
        result.setLazy(false);
        return result;
    }

    @Override
    public boolean updateObject(T object) throws NoSuchObjectException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteObject(T object) throws NoSuchObjectException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long addObject(T object) throws SQLException {
        String newObjectName = (object instanceof NamedEntity) ? ((NamedEntity)object).getName() : "";
        //long newObjectId = insertObject(newObjectName, getObjTypeName(object.getClass()));
        System.out.println(Arrays.toString(getAttributeNames(getObjTypeName(object.getClass()))));
        return 0;
        //return newObjectId;
    }



//    private String generateInsertAttributesQuery(T object) throws SQLException {
//        for (String attributeName : getAttributeNames(getObjTypeName(object.getClass()))) {
//
//
//        }
//
//        for (Field field : getDeclaredFields(object.getClass())) {
//            if (field.isAnnotationPresent(AttributeName.class)) {
//                AttributeName attribute = field.getAnnotation(AttributeName.class);
//                if (Arrays.asList(attribute.value()).contains(attributeName)) {
//                    if (BaseEntity.class.isAssignableFrom(field.getType())) {
//                        if (utilsBean.getConvertUtils().lookup(field.getType()) == null) {
//                            utilsBean.getConvertUtils().register(entityConverter, field.getType());
//                        }
//                    } else if (List.class.isAssignableFrom(field.getType())) {
//                        field.setAccessible(true);
//                        utilsBean.getConvertUtils().register(new ListConverter((List) field.get(object), getFieldGenericType(field)), field.getType());
//                    } else if (field.getType().isEnum()) {
//                        utilsBean.getConvertUtils().register(enumConverter, field.getType());
//                    }
//                    utilsBean.setProperty(object, field.getName(), value);
//                    break;
//                }
//            }
//        }
//
//    }



    private String[] getAttributeNames(String objTypeName) throws SQLException {
        List<String> resultList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(QUERY_GET_ATTRIBUTES_BY_TYPE)) {
            statement.setString(1, objTypeName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    resultList.add(resultSet.getString(1));
                }
            }
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    private Object firstNotNullValue(Object... values) {
        for (Object value : values) if (value != null && !value.equals("null")) return value;
        return null;
    }

    private String getObjTypeName(Class<?> aClass) {
        if (aClass.isAnnotationPresent(ObjTypeName.class)) {
            ObjTypeName objType = aClass.getAnnotation(ObjTypeName.class);
            return objType.value();
        } else throw new IllegalArgumentException();
    }

    private Field[] getDeclaredFields(Class<?> aClass) {
        Class<?> current = aClass;
        List<Field> result = new ArrayList<>();
        while (current.getSuperclass() != null) {
            Collections.addAll(result, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return result.toArray(new Field[result.size()]);
    }

    private PreparedStatement prepareGetByIdStatement(Connection connection, long id) throws SQLException {
        PreparedStatement result = connection.prepareStatement(QUERY_GET_OBJECT_BY_ID);
        for (int i = 1; i <= 3; i++) {
            result.setLong(i, id);
        }
        return result;
    }

    private long insertObject(String objName, String objTypeCode) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_INSERT_OBJECT)) {
            statement.setString(1, objName);
            statement.setString(2, objTypeCode);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.executeQuery(QUERY_GET_OBJ_SEQ_CURRVAL)) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
            throw new SQLException();
        }
    }

    private T extractObject(long id) throws SQLException {
        try (PreparedStatement statement = prepareGetByIdStatement(connection, id);
             ResultSet resultSet = statement.executeQuery()) {
            T result = objectClass.newInstance();
            while (resultSet.next()) {
                String attributeName = resultSet.getString("CODE");
                Object value = firstNotNullValue(resultSet.getString("VALUE"), resultSet.getDate("DATE_VALUE"), resultSet.getBigDecimal("REFERENCE"));


                putValueToField(result, value, attributeName);

            }
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Cannot create instance: " + e);
            return null;
        }
    }


    private String extractString(long id) throws SQLException {
        try (PreparedStatement statement = prepareGetByIdStatement(connection, id);
             ResultSet resultSet = statement.executeQuery()) {
            String result;
            while (resultSet.next()) {
                String code = resultSet.getString("CODE");
                if (code.substring(code.lastIndexOf("_") + 1).equalsIgnoreCase("value")) {
                    result = resultSet.getString("VALUE");
                    return result;
                }
            }
            return null;
        }
    }

    private Class<?> getFieldGenericType(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }


    private Long castToId(Object value) {
        Long result = null;
        if ((value instanceof BigDecimal)) {
            result = ((BigDecimal) value).longValue();
        } else if (value instanceof String) {
            result = Long.valueOf((String) value);
        }
        return result;
    }


    private void putValueToField(Object object, Object value, String attributeName) {
        try {
            for (Field field : getDeclaredFields(object.getClass())) {
                if (field.isAnnotationPresent(AttributeName.class)) {
                    AttributeName attribute = field.getAnnotation(AttributeName.class);
                    if (Arrays.asList(attribute.value()).contains(attributeName)) {
                        if (BaseEntity.class.isAssignableFrom(field.getType())) {
                            if (utilsBean.getConvertUtils().lookup(field.getType()) == null) {
                                utilsBean.getConvertUtils().register(entityConverter, field.getType());
                            }
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            utilsBean.getConvertUtils().register(new ListConverter((List) field.get(object), getFieldGenericType(field)), field.getType());
                        } else if (field.getType().isEnum()) {
                            utilsBean.getConvertUtils().register(enumConverter, field.getType());
                        }
                        utilsBean.setProperty(object, field.getName(), value);
                        break;
                    }
                }

            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Cannot set property to object: " + e);
        }
    }


    private class EntityConverter implements Converter {
        @Override
        @SuppressWarnings("unchecked")
        public <V> V convert(Class<V> type, Object value) {

            V result = null;
            try {
                Long objectId = castToId(value);

                if (BaseEntity.class.isAssignableFrom(type)) {
                    if (isLazy) {
                        BaseEntity object = (BaseEntity) type.newInstance();
                        object.setId(castToId(value));
                        object.setLazy(true);
                        result = (V) object;
                    } else {
                        result = (V) new ObjectDaoReflect(type, connection).getObjectById(objectId);
                    }
                }
            } catch (SQLException | NoSuchObjectException | NumberFormatException | InstantiationException | IllegalAccessException e) {
                LOG.error("Cannot initilize field " + e);
            }


            return result;
        }
    }

    private class ListConverter implements Converter {

        private List list;
        private Class<?> genericTypeOfList;

        public ListConverter(List list, Class<?> genericTypeOfList) {
            this.list = list;
            this.genericTypeOfList = genericTypeOfList;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> V convert(Class<V> type, Object value) {
            if (list == null) {
                list = new ArrayList<>();
            }

            V result = (V) list;

            try {
                Object toList = null;
                if (genericTypeOfList.isEnum()) {
                    toList = enumConverter.convert(genericTypeOfList, value);
                } else if (BaseEntity.class.isAssignableFrom(genericTypeOfList)) {
                    toList = entityConverter.convert(genericTypeOfList, value);
                } else if (genericTypeOfList.equals(String.class)) {
                    toList = extractString(castToId(value));
                }

                if (toList != null) {
                    list.add(toList);
                }

            } catch (SQLException e) {
                LOG.error("Cannot initilize field " + e);
            }
            return result;
        }
    }

    private class EnumConverter implements Converter {
        @Override
        @SuppressWarnings("unchecked")
        public <V> V convert(Class<V> type, Object value) {

            V result = null;

            try {
                Long objectId = castToId(value);

                if (type.isEnum()) {
                    Method method = type.getDeclaredMethod("getById", Long.class);
                    result = (V) method.invoke(null, objectId);
                }
            } catch (NumberFormatException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                LOG.error("Cannot initilize field " + e);
            }

            return result;
        }
    }

}
