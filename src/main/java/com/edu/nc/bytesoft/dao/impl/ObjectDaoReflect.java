package com.edu.nc.bytesoft.dao.impl;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.JdbcExecutor;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.TransactionManager;
import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.model.BaseEntity;
import com.edu.nc.bytesoft.model.IdentifiableEnum;
import com.edu.nc.bytesoft.model.NamedEntity;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

    private static final String QUERY_GET_SINGLE_ATTRIBUTE = "SELECT * FROM tableName WHERE ATTR_ID = ? AND OBJECT_ID = ?";


    private static final String QUERY_GET_LIST_OBJECT_TYPE_CODE_BY_ATTR_ID = "SELECT REF_TYPE.CODE\n" +
            "FROM ATTRTYPE TYPE JOIN OBJTYPE REF_TYPE ON (REF_TYPE.OBJECT_TYPE_ID = TYPE.OBJECT_TYPE_ID_REF)\n" +
            "WHERE TYPE.ATTR_ID = ?";

    private static final String QUERY_GET_LIST_OBJECT_ID_BY_LIST_ITEM_OBJECT_TYPE_CODE = "SELECT OBJR.REFERENCE\n" +
            "FROM OBJREFERENCE OBJR, OBJECTS OBJ, OBJTYPE OBJ_T\n" +
            "WHERE OBJ.OBJECT_ID = OBJR.OBJECT_ID\n" +
            "AND OBJ.OBJECT_TYPE_ID = OBJ_T.OBJECT_TYPE_ID\n" +
            "AND OBJ_T.CODE = ?\n" +
            "AND ROWNUM = 1";

    private static final String QUERY_GET_ATTRIBUTE_ID_BY_CODE = "SELECT ATTR_ID\n" +
            " FROM ATTRTYPE\n" +
            " WHERE CODE = ?\n" +
            " AND ROWNUM = 1";

    private Connection connection;

    private Class<T> objectClass;

    private boolean isLazy;

    private final EntityConverter entityConverter = new EntityConverter();

    private final EnumConverter enumConverter = new EnumConverter();

    private final BeanUtilsBean utilsBean = new BeanUtilsBean();

    private JdbcExecutor jdbcExecutor = new JdbcExecutor();


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
        jdbcExecutor.setConnection(connection);
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
        jdbcExecutor.setConnection(connection);
    }


    @Override
    public T getById(long id) throws SQLException, NoSuchObjectException {
        T result = extractObject(id);
        if (result == null) throw new NoSuchObjectException();
        result.setId(id);
        result.setLazy(false);
        return result;
    }


    @Override
    public boolean delete(T object) throws NoSuchObjectException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T save(T object) throws SQLException, NoSuchObjectException {
        if (object.isNew()) {
            object = insert(object);
        } else update(object);
        return object;
    }

    private <P extends BaseEntity> P update(P object) throws SQLException, NoSuchObjectException {
        try {
            List<PreparedStatement> preparedUpdateStatements = prepareUpdateAttributesStatement(object);
            for (PreparedStatement updateStatement : preparedUpdateStatements) {
//                try (updateStatement) {
//                    updateStatement.executeUpdate();
//                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SQLException(e);
        }
    }

    private <P extends BaseEntity> P insert(P object) throws SQLException, NoSuchObjectException {
        try {
            String newObjectName = (object instanceof NamedEntity) ? ((NamedEntity) object).getName() : "";
            long newObjectId = insertObjectAndReturnId(newObjectName, getObjTypeName(object.getClass()));
            object.setId(newObjectId);
            try (PreparedStatement insertAttributesstatement = prepareInsertAttributesStatement(object)) {
                insertAttributesstatement.executeUpdate();
                return object;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new SQLException(e);
        }
    }


    private String[] getAttributeNamesFromDB(String objTypeName) throws SQLException {
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

    private Field[] getAllDeclaredFields(Class<?> aClass) {
        Class<?> current = aClass;
        List<Field> result = new ArrayList<>();
        while (current.getSuperclass() != null) {
            Collections.addAll(result, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return result.toArray(new Field[result.size()]);
    }

    private PreparedStatement prepareGetByIdStatement(long id) throws SQLException {
        PreparedStatement result = connection.prepareStatement(QUERY_GET_OBJECT_BY_ID);
        for (int i = 1; i <= 3; i++) {
            result.setLong(i, id);
        }
        return result;
    }

    private PreparedStatement prepareGetAttribute(long attributeId, long objectId, String tableName) throws SQLException {
        PreparedStatement result = connection.prepareStatement(QUERY_GET_SINGLE_ATTRIBUTE.replace("tableName", tableName));
        result.setLong(1, attributeId);
        result.setLong(2, objectId);
        return result;
    }

    private List<PreparedStatement> prepareUpdateAttributesStatement(BaseEntity object) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
        List<PreparedStatement> result = new ArrayList<>();
        SqlGenerator sqlGenerator = new SqlGenerator();
        List<String> queries = sqlGenerator.generateUpdateAttributesQueries(object);
        List<Date> lastGeneratedDates = sqlGenerator.lastGeneratedDates;
        int index = 0;
        for (String query : queries) {
            PreparedStatement statement = connection.prepareStatement(query);
            if (query.indexOf('?') >= 0) {
                statement.setDate(1, new java.sql.Date(lastGeneratedDates.get(index++).getTime()));
            }
            result.add(statement);
        }
        return result;
    }

    private PreparedStatement prepareInsertAttributesStatement(BaseEntity object) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.generateInsertAttributesQuery(object);
        PreparedStatement result = connection.prepareStatement(sql);
        int index = 1;
        for (Date date : sqlGenerator.lastGeneratedDates) {
            java.sql.Date x = new java.sql.Date(date.getTime());
            result.setDate(index++, x);
        }
        return result;
    }

    private long insertObjectAndReturnId(String objName, String objTypeCode) throws SQLException {
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
        try (PreparedStatement statement = prepareGetByIdStatement(id);
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


    private String extractListStringValue(long id) throws SQLException {
        try (PreparedStatement statement = prepareGetByIdStatement(id);
             ResultSet resultSet = statement.executeQuery()) {
            String result;
            if (resultSet.next()) {
                String code = resultSet.getString("CODE");
                if (code.substring(code.lastIndexOf("_") + 1).equalsIgnoreCase("value")) {
                    result = resultSet.getString("VALUE");
                    return result;
                }
            }
            return null;
        }
    }

    public boolean checkIfAttributeExist(long attributeId, long objectId, String tableName) throws SQLException {
        try (PreparedStatement statement = prepareGetAttribute(attributeId, objectId, tableName);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
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
            for (Field field : getAllDeclaredFields(object.getClass())) {
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
                        result = (V) new ObjectDaoReflect(type, connection).getById(objectId);
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
        private Class<?> fieldGenericType;

        public ListConverter(List list, Class<?> fieldGenericType) {
            this.list = list;
            this.fieldGenericType = fieldGenericType;
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
                if (fieldGenericType.isEnum()) {
                    toList = enumConverter.convert(fieldGenericType, value);
                } else if (BaseEntity.class.isAssignableFrom(fieldGenericType)) {
                    toList = entityConverter.convert(fieldGenericType, value);
                } else if (fieldGenericType.equals(String.class)) {
                    toList = extractListStringValue(castToId(value));
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


    private class SqlGenerator {

        private List<Date> lastGeneratedDates = new ArrayList<>();

        private Map<String, Field> getAnnotationValueFieldMap(Class aClass) {
            Map<String, Field> result = new HashMap<>();
            Field[] fields = getAllDeclaredFields(aClass);
            for (Field field : fields) {
                if (field.isAnnotationPresent(AttributeName.class)) {
                    for (String value : field.getAnnotation(AttributeName.class).value()) {
                        result.put(value, field);
                    }
                }
            }
            return result;
        }

        private List<String> generateUpdateAttributesQueries(BaseEntity object) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
            List<String> updateQueries = new ArrayList<>();
            Map<String, Field> fieldMap = getAnnotationValueFieldMap(object.getClass());
            lastGeneratedDates.clear();
            for (String attributeCode : getAttributeNamesFromDB(getObjTypeName(object.getClass()))) {
                Field field = fieldMap.get(attributeCode);
                if (field != null) {
                    field.setAccessible(true);
                    Object attributeValue = field.get(object);
                    if (attributeValue != null) {
                        long attributeId = jdbcExecutor.execute(QUERY_GET_ATTRIBUTE_ID_BY_CODE, Long.class, attributeCode);
                        if (BaseEntity.class.isAssignableFrom(field.getType())) {
                            BaseEntity reffereneObject = (BaseEntity) attributeValue;
                            if (reffereneObject.isNew()) {
                                reffereneObject = insert(reffereneObject);
                            }
                            updateQueries.add(buildObjReferenceUpdate(String.valueOf(attributeId), object.getId().toString(), reffereneObject.getId().toString()));
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            updateQueries.add(buildUpdateList(String.valueOf(attributeId), object.getId().toString(), getFieldGenericType(field), (List<?>) field.get(object)));
                        } else if (IdentifiableEnum.class.isAssignableFrom(field.getType())) {
                            Method method = field.getType().getMethod("getId");
                            updateQueries.add(buildObjReferenceUpdate(String.valueOf(attributeId), object.getId().toString(), method.invoke(field.get(object)).toString()));
                        } else if (field.getType().equals(Date.class)) {
                            updateQueries.add(buildAttributeUpdate(String.valueOf(attributeId), object.getId().toString(), null, (Date) attributeValue));
                        } else if (field.getType().equals(String.class) || Number.class.isAssignableFrom(field.getType())) {
                            updateQueries.add(buildAttributeUpdate(String.valueOf(attributeId), object.getId().toString(), attributeValue.toString(), null));
                        }
                    }
                }
            }
            if (updateQueries.size() == 0) {
                throw new IllegalArgumentException();
            }
            return updateQueries;
        }

        private String generateInsertAttributesQuery(BaseEntity object) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
            Set<String> attributeNames = new HashSet<>();
            StringBuilder insertQuery = new StringBuilder("INSERT ALL");
            Map<String, Field> fieldMap = getAnnotationValueFieldMap(object.getClass());
            lastGeneratedDates.clear();
            for (String attributeName : getAttributeNamesFromDB(getObjTypeName(object.getClass()))) {
                Field field = fieldMap.get(attributeName);
                if (field != null) {
                    field.setAccessible(true);
                    Object attributeValue = field.get(object);
                    if (attributeValue != null) {
                        attributeNames.add(attributeName);
                        if (BaseEntity.class.isAssignableFrom(field.getType())) {
                            BaseEntity reffereneObject = (BaseEntity) attributeValue;
                            if (reffereneObject.isNew()) {
                                reffereneObject = insert(reffereneObject);
                            }
                            insertQuery.append(buildObjReferenceInsert(attributeName, object.getId().toString(), reffereneObject.getId().toString()));
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            insertQuery.append(buildInsertList(attributeName, object.getId().toString(), getFieldGenericType(field), (List<?>) field.get(object)));
                        } else if (IdentifiableEnum.class.isAssignableFrom(field.getType())) {
                            Method method = field.getType().getMethod("getId");
                            insertQuery.append(buildObjReferenceInsert(attributeName, object.getId().toString(), method.invoke(field.get(object)).toString()));
                        } else if (field.getType().equals(Date.class)) {
                            insertQuery.append(buildAttributeInsert(attributeName, object.getId().toString(), null, (Date) attributeValue));
                        } else if (field.getType().equals(String.class) || Number.class.isAssignableFrom(field.getType())) {
                            insertQuery.append(buildAttributeInsert(attributeName, object.getId().toString(), attributeValue.toString(), null));
                        }
                    }
                }
            }

            if (attributeNames.size() > 0) {
                insertQuery.append(buildInsertFooter(attributeNames.toArray(new String[attributeNames.size()])));
                return insertQuery.toString();
            } else throw new IllegalArgumentException();
        }

        private String buildAttributeInsert(String attributeId, String objectId, String value, Date dateValue) {
            String dateMarker = null;
            if (dateValue != null) {
                dateMarker = "?";
                lastGeneratedDates.add(dateValue);
            }
            return " INTO ATTRIBUTES(ATTR_ID, OBJECT_ID, VALUE, DATE_VALUE) VALUES (" + attributeId +
                    ", " +
                    objectId +
                    ", " +
                    "'" +
                    value +
                    "'" +
                    ", " +
                    dateMarker +
                    " )";
        }

        private String buildObjReferenceInsert(String attributeId, String objectId, String reference) {
            return " INTO OBJREFERENCE(ATTR_ID, OBJECT_ID, REFERENCE) VALUES (" + attributeId +
                    ", " +
                    objectId +
                    ", " +
                    reference +
                    ")";
        }

        private String buildObjReferenceUpdate(String attributeId, String objectId, String reference) {
            return "UPDATE OBJREFERENCE \n" +
                    "SET REFERENCE = " + reference + "\n" +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + ";";
        }

        private String buildObjectNameUpdate(String objectId, String name) {
            return "UPDATE OBJECTS \n" +
                    "SET NAME = " + name + "\n" +
                    "WHERE OBJECT_ID = " + objectId + ";";
        }

        private String buildAttributeUpdate(String attributeId, String objectId, String value, Date dateValue) {
            String dateMarker = null;
            if (dateValue != null) {
                dateMarker = "?";
                lastGeneratedDates.add(dateValue);
            }
            return "UPDATE ATTRIBUTES \n" +
                    "SET VALUE = " + value + "\n" +
                    "SET DATE_VALUE = " + dateMarker + "\n" +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + ";";
        }


        private String buildInsertFooter(String[] attributeNames) {
            StringBuilder result = new StringBuilder(" SELECT * FROM ( SELECT ATTR_ID, CODE FROM ATTRTYPE ) PIVOT ( MAX(ATTR_ID) FOR CODE IN (");
            String prefix = "";
            for (String attributeName : attributeNames) {
                result
                        .append(prefix)
                        .append("'")
                        .append(attributeName)
                        .append("' ")
                        .append(attributeName);
                prefix = ",";
            }
            result.append("))");
            return result.toString();
        }

        private String buildUpdateList(String attributeId, String objectId, Class<?> listGenericType, List<?> list) throws SQLException, NoSuchObjectException {
            StringBuilder result = new StringBuilder();
            if (IdentifiableEnum.class.isAssignableFrom(listGenericType)) {
                for (Object value : list) {
                    IdentifiableEnum elem = (IdentifiableEnum) value;
                    result.append(buildObjReferenceUpdate(attributeId, objectId, String.valueOf(elem.getId())));
                }
                return result.toString();
            } else if (listGenericType.equals(NamedEntity.class)) {
                for (Object value : list) {
                    NamedEntity elem = (NamedEntity) value;
                    long listElemId = elem.getId();
                    result.append(buildObjectNameUpdate(String.valueOf(listElemId), elem.getName()));
                    result.append(buildAttributeUpdate(String.valueOf(64), String.valueOf(listElemId), elem.getName(), null));
                }
                return result.toString();
            } else if (BaseEntity.class.isAssignableFrom(listGenericType)) {
                for (Object value : list) {
                    BaseEntity elem = (BaseEntity) value;
                    if (elem.isNew()) {
                        elem = insert(elem);
                    }
                    result.append(buildObjReferenceUpdate(attributeId, objectId, elem.getId().toString()));
                }
                return result.toString();
            }
            return "";
        }

        private String buildInsertList(String attributeCode, String objectId, Class<?> listGenericType, List<?> list) throws SQLException, NoSuchObjectException {
            StringBuilder result = new StringBuilder();
            if (IdentifiableEnum.class.isAssignableFrom(listGenericType)) {
                for (Object value : list) {
                    IdentifiableEnum elem = (IdentifiableEnum) value;
                    result.append(buildObjReferenceInsert(attributeCode, objectId, String.valueOf(elem.getId())));
                }
                return result.toString();
            } else if (listGenericType.equals(NamedEntity.class)) {
                for (Object value : list) {
                    NamedEntity elem = (NamedEntity) value;
                    long attributeId = jdbcExecutor.execute(QUERY_GET_ATTRIBUTE_ID_BY_CODE, Long.class, attributeCode);
                    String listValueObjectTypeCode = jdbcExecutor.execute(QUERY_GET_LIST_OBJECT_TYPE_CODE_BY_ATTR_ID, String.class, attributeId);
                    long listElemId = insertObjectAndReturnId(String.valueOf(elem.getName()), listValueObjectTypeCode);
                    elem.setId(listElemId);
                    result.append(buildAttributeInsert(String.valueOf(64), String.valueOf(listElemId), String.valueOf(elem.getName()), null));
                    result.append(buildObjReferenceInsert(String.valueOf(63), String.valueOf(listElemId), String.valueOf(jdbcExecutor.execute(QUERY_GET_LIST_OBJECT_ID_BY_LIST_ITEM_OBJECT_TYPE_CODE, Long.class, listValueObjectTypeCode))));
                    result.append(buildObjReferenceInsert(attributeCode, objectId, String.valueOf(listElemId)));
                }
                return result.toString();
            } else if (BaseEntity.class.isAssignableFrom(listGenericType)) {
                for (Object value : list) {
                    BaseEntity elem = (BaseEntity) value;
                    if (elem.isNew()) {
                        elem = insert(elem);
                    }
                    result.append(buildObjReferenceInsert(attributeCode, objectId, elem.getId().toString()));
                }
                return result.toString();
            }
            return "";
        }
    }
}
