package com.edu.nc.bytesoft.dao.impl;

import com.edu.nc.bytesoft.Log;
import com.edu.nc.bytesoft.dao.ObjectDao;
import com.edu.nc.bytesoft.dao.TransactionManager;
import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import com.edu.nc.bytesoft.dao.exception.NoSuchObjectException;
import com.edu.nc.bytesoft.dao.sql.Queries;
import com.edu.nc.bytesoft.model.BaseEntity;
import com.edu.nc.bytesoft.model.IdentifiableEnum;
import com.edu.nc.bytesoft.model.NamedEntity;
import com.edu.nc.bytesoft.util.SqlExecutor;
import javafx.util.Pair;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ObjectDaoReflect<T extends BaseEntity> implements ObjectDao<T> {

    private static final Log LOG = Log.get(ObjectDaoReflect.class);

    private Connection connection;

    private Class<T> objectClass;

    private boolean isLazy;

    private final EntityConverter entityConverter = new EntityConverter();

    private final EnumConverter enumConverter = new EnumConverter();

    private final BeanUtilsBean utilsBean = new BeanUtilsBean();

    private final SqlExecutor sqlExecutor = new SqlExecutor();


    public ObjectDaoReflect() {
    }

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
        sqlExecutor.setConnection(connection);
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

    @Autowired
    public void setConnection(Connection connection) {
        this.connection = connection;
        sqlExecutor.setConnection(connection);
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }

    public void setObjectClass(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public T getById(long id) throws SQLException, NoSuchObjectException {
        if ("N".equals(sqlExecutor.execute(Queries.QUERY_CHECK_IF_OBJECT_EXIST, String.class, id)))
            throw new NoSuchObjectException();
        T result = extractObject(id);
        if (result == null) throw new NoSuchObjectException();
        result.setId(id);
        result.setLazy(false);
        return result;
    }


    @Override
    public boolean delete(long id) throws NoSuchObjectException, SQLException {
        T oldObject = getById(id);
        try {
            for (PreparedStatement preparedUpdateStatement : prepareDeleteStatements(oldObject)) {
                try (PreparedStatement statement = preparedUpdateStatement) {
                    statement.executeUpdate();
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T save(T object) throws SQLException, NoSuchObjectException {
        if (object.isNew()) {
            object = insert(object);
        } else {
            T oldObject = getById(object.getId());
            update(object, oldObject);
        }
        return object;
    }

    @Override
    public SqlExecutor getSqlExecutor() {
        return sqlExecutor;
    }

    private <P extends BaseEntity> P update(P object, BaseEntity oldObject) throws SQLException, NoSuchObjectException {
        try {
            for (PreparedStatement preparedUpdateStatement : prepareUpdateStatements(object, oldObject)) {
                try (PreparedStatement statement = preparedUpdateStatement) {
                    statement.executeUpdate();
                }
            }
            return object;
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
        try (PreparedStatement statement = connection.prepareStatement(Queries.QUERY_GET_ATTRIBUTES_BY_TYPE)) {
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
        PreparedStatement result = connection.prepareStatement(Queries.QUERY_GET_OBJECT_BY_ID);
        for (int i = 1; i <= 3; i++) {
            result.setLong(i, id);
        }
        return result;
    }

    private PreparedStatement prepareGetAttribute(long attributeId, long objectId, String tableName) throws SQLException {
        PreparedStatement result = connection.prepareStatement(Queries.QUERY_GET_SINGLE_ATTRIBUTE.replace("tableName", tableName));
        result.setLong(1, attributeId);
        result.setLong(2, objectId);
        return result;
    }

    private List<PreparedStatement> prepareUpdateStatements(BaseEntity object, BaseEntity oldObject) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
        List<PreparedStatement> result = new ArrayList<>();
        SqlGenerator sqlGenerator = new SqlGenerator();
        List<String> queries = sqlGenerator.generateUpdateQueries(object, oldObject);
        List<Pair<Date, Date>> lastGeneratedDates = sqlGenerator.lastGeneratedDates;
        int index = 0;
        for (String query : queries) {
            PreparedStatement statement = connection.prepareStatement(query);
            if (query.indexOf('?') >= 0) {
                statement.setDate(1, new java.sql.Date(lastGeneratedDates.get(index).getKey().getTime()));
                statement.setDate(2, new java.sql.Date(lastGeneratedDates.get(index).getValue().getTime()));
                index++;
            }
            result.add(statement);
        }
        return result;
    }

    private List<PreparedStatement> prepareDeleteStatements(BaseEntity oldObject) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
        List<PreparedStatement> result = new ArrayList<>();
        SqlGenerator sqlGenerator = new SqlGenerator();
        List<String> queries = sqlGenerator.generateDeleteQueries(oldObject);
        List<Pair<Date, Date>> lastGeneratedDates = sqlGenerator.lastGeneratedDates;
        int index = 0;
        for (String query : queries) {
            PreparedStatement statement = connection.prepareStatement(query);
            if (query.indexOf('?') >= 0) {
                statement.setDate(1, new java.sql.Date(lastGeneratedDates.get(index++).getKey().getTime()));
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
        for (Pair<Date, Date> date : sqlGenerator.lastGeneratedDates) {
            java.sql.Date x = new java.sql.Date(date.getKey().getTime());
            result.setDate(index++, x);
        }
        return result;
    }

    private long insertObjectAndReturnId(String objName, String objTypeCode) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(Queries.QUERY_INSERT_OBJECT)) {
            statement.setString(1, objName);
            statement.setString(2, objTypeCode);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.executeQuery(Queries.QUERY_GET_OBJ_SEQ_CURRVAL)) {
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

    private boolean checkIfAttributeExist(long attributeId, long objectId, String tableName) throws SQLException {
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

        private List<Pair<Date, Date>> lastGeneratedDates = new ArrayList<>();

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


        private List<String> generateUpdateQueries(BaseEntity object, BaseEntity oldObject) throws SQLException, NoSuchObjectException, IllegalAccessException {
            List<String> updateQueries = new ArrayList<>();
            Map<String, Field> fieldMap = getAnnotationValueFieldMap(object.getClass());
            lastGeneratedDates.clear();
            for (String attributeCode : getAttributeNamesFromDB(getObjTypeName(object.getClass()))) {
                Field field = fieldMap.get(attributeCode);
                if (field != null) {
                    field.setAccessible(true);
                    if (checkIfFieldValueChanged(field, object, oldObject)) {
                        Object attributeValue = field.get(object);
                        if (attributeValue != null) {
                            long attributeId = sqlExecutor.execute(Queries.QUERY_GET_ATTRIBUTE_ID_BY_CODE, Long.class, attributeCode);
                            if (BaseEntity.class.isAssignableFrom(field.getType())) {
                                BaseEntity reffereneObject = (BaseEntity) attributeValue;
                                if (reffereneObject.isNew()) {
                                    reffereneObject = insert(reffereneObject);
                                }
                                String oldReference = String.valueOf(((BaseEntity) field.get(oldObject)).getId());
                                updateQueries.add(buildObjReferenceUpdate(String.valueOf(attributeId), String.valueOf(object.getId()), String.valueOf(reffereneObject.getId()), oldReference));
                            } else if (List.class.isAssignableFrom(field.getType())) {
                                updateQueries.addAll(buildUpdateList(String.valueOf(attributeId), String.valueOf(object.getId()), getFieldGenericType(field), (List<?>) field.get(object), (List<?>) field.get(oldObject)));
                            } else if (IdentifiableEnum.class.isAssignableFrom(field.getType())) {
                                String reference = String.valueOf(((IdentifiableEnum) field.get(object)).getId());
                                String oldReference = String.valueOf(((IdentifiableEnum) field.get(oldObject)).getId());
                                updateQueries.add(buildObjReferenceUpdate(String.valueOf(attributeId), String.valueOf(object.getId()), reference, oldReference));
                            } else if (field.getType().equals(Date.class)) {
                                Date oldDate = (Date) field.get(oldObject);
                                updateQueries.add(buildAttributeDateUpdate(String.valueOf(attributeId), object.getId().toString(), (Date) attributeValue, oldDate));
                            } else if (field.getType().equals(String.class) || Number.class.isAssignableFrom(field.getType())) {
                                String oldValue = String.valueOf(field.get(oldObject));
                                updateQueries.add(buildAttributeStringUpdate(String.valueOf(attributeId), String.valueOf(object.getId()), String.valueOf(attributeValue), oldValue));
                            }
                        }
                    }
                }
            }
            return updateQueries;
        }


        private List<String> generateDeleteQueries(BaseEntity oldObject) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchObjectException {
            List<String> deleteQueries = new ArrayList<>();
            Map<String, Field> fieldMap = getAnnotationValueFieldMap(oldObject.getClass());
            lastGeneratedDates.clear();
            for (String attributeCode : getAttributeNamesFromDB(getObjTypeName(oldObject.getClass()))) {
                Field field = fieldMap.get(attributeCode);
                if (field != null) {
                    field.setAccessible(true);
                    Object attributeValue = field.get(oldObject);
                    if (attributeValue != null) {
                        long attributeId = sqlExecutor.execute(Queries.QUERY_GET_ATTRIBUTE_ID_BY_CODE, Long.class, attributeCode);
                        if (BaseEntity.class.isAssignableFrom(field.getType())) {
                            BaseEntity reffereneObject = (BaseEntity) attributeValue;
                            deleteQueries.add(buildObjReferenceDelete(String.valueOf(attributeId), String.valueOf(oldObject.getId()), String.valueOf(reffereneObject.getId())));
                            if (!reffereneObject.isNew() && !isLazy) {
                                deleteQueries.addAll(generateDeleteQueries(reffereneObject));
                            }
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            deleteQueries.addAll(buildDeleteList(String.valueOf(attributeId), String.valueOf(oldObject.getId()), getFieldGenericType(field), (List<?>) field.get(oldObject)));
                        } else if (IdentifiableEnum.class.isAssignableFrom(field.getType())) {
                            String reference = String.valueOf(((IdentifiableEnum) field.get(oldObject)).getId());
                            deleteQueries.add(buildObjReferenceDelete(String.valueOf(attributeId), String.valueOf(oldObject.getId()), reference));
                        } else if (field.getType().equals(Date.class)) {
                            deleteQueries.add(buildAttributeDateDelete(String.valueOf(attributeId), oldObject.getId().toString(), (Date) attributeValue));
                        } else if (field.getType().equals(String.class) || Number.class.isAssignableFrom(field.getType())) {
                            deleteQueries.add(buildAttributeStringDelete(String.valueOf(attributeId), oldObject.getId().toString(), String.valueOf(attributeValue)));
                        }
                    }
                }
            }
            deleteQueries.add(buildObjectDelete(String.valueOf(oldObject.getId())));
            return deleteQueries;
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
                            insertQuery.append(buildAttributeInsert(attributeName, String.valueOf(object.getId()), String.valueOf(attributeValue), null));
                        }
                    }
                }
            }

            if (attributeNames.size() > 0) {
                insertQuery.append(buildInsertFooter(attributeNames.toArray(new String[attributeNames.size()])));
                return insertQuery.toString();
            } else throw new IllegalArgumentException();
        }


        private boolean checkIfFieldValueChanged(Field field, Object object, Object oldObject) throws IllegalAccessException {
            return !Objects.equals(field.get(object), field.get(oldObject));
        }

        private String buildAttributeInsert(String attributeId, String objectId, String value, Date dateValue) {
            String dateMarker = null;
            if (dateValue != null) {
                dateMarker = "?";
                lastGeneratedDates.add(new Pair<>(dateValue, null));
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

        private String buildObjReferenceDelete(String attributeId, String objectId, String reference) {
            return "DELETE OBJREFERENCE " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND REFERENCE = " + reference;
        }

        private String buildObjectDelete(String objectId) {
            return "DELETE OBJECTS " +
                    "WHERE OBJECT_ID = " + objectId;
        }

        private String buildObjReferenceUpdate(String attributeId, String objectId, String reference, String oldReference) {
            return "UPDATE OBJREFERENCE " +
                    "SET REFERENCE = " + reference + " " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND REFERENCE = " + oldReference;
        }


        private String buildAttributeDateUpdate(String attributeId, String objectId, Date value, Date oldValue) {
            lastGeneratedDates.add(new Pair<>(value, oldValue));
            return "UPDATE ATTRIBUTES " +
                    "DATE_VALUE = ? " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND DATE_VALUE = ?";
        }

        private String buildAttributeDateDelete(String attributeId, String objectId, Date value) {
            lastGeneratedDates.add(new Pair<>(value, null));
            return "DELETE ATTRIBUTES " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND DATE_VALUE = ?";
        }


        private String buildAttributeStringUpdate(String attributeId, String objectId, String value, String oldValue) {
            return "UPDATE ATTRIBUTES " +
                    "SET VALUE = " + "'" + value + "'" + " " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND VALUE = " + "'" + oldValue + "'";
        }

        private String buildAttributeStringDelete(String attributeId, String objectId, String value) {
            return "DELETE ATTRIBUTES " +
                    "WHERE ATTR_ID = " + attributeId + " AND OBJECT_ID = " + objectId + " AND VALUE = " + "'" + value + "'";
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

        private List<String> buildDeleteList(String attributeId, String objectId, Class<?> listGenericType, List<?> list) throws SQLException, NoSuchObjectException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            List<String> result = new LinkedList<>();
            if (IdentifiableEnum.class.isAssignableFrom(listGenericType)) {
                result.addAll(list.stream().map(toDelete -> buildObjReferenceDelete(attributeId, objectId, String.valueOf(((IdentifiableEnum) toDelete).getId()))).collect(Collectors.toList()));
                return result;
            } else if (listGenericType.equals(NamedEntity.class)) {
                for (Object toDelete : list) {
                    NamedEntity toDeleteEntity = (NamedEntity) toDelete;
                    result.add(buildObjReferenceDelete(attributeId, objectId, String.valueOf(toDeleteEntity.getId())));
                    result.add(buildObjectDelete(String.valueOf(toDeleteEntity.getId())));
                }
                return result;
            } else if (BaseEntity.class.isAssignableFrom(listGenericType)) {
                for (Object toDelete : list) {
                    BaseEntity toDeleteEntity = (BaseEntity) toDelete;
                    result.add(buildObjReferenceDelete(attributeId, objectId, String.valueOf(toDeleteEntity.getId())));
                    if (!toDeleteEntity.isNew() && !isLazy) {
                        result.addAll(generateDeleteQueries(toDeleteEntity));
                    }
                }
                return result;
            }
            return result;
        }

        private List<String> buildUpdateList(String attributeId, String objectId, Class<?> listGenericType, List<?> list, List<?> oldList) throws SQLException, NoSuchObjectException {
            List<String> result = new LinkedList<>();
            if (IdentifiableEnum.class.isAssignableFrom(listGenericType)) {
                List<?> toDeleteList = new ArrayList<>(oldList);
                toDeleteList.removeAll(list);
                result.addAll(toDeleteList.stream().map(toDelete -> buildObjReferenceDelete(attributeId, objectId, String.valueOf(((IdentifiableEnum) toDelete).getId()))).collect(Collectors.toList()));
                List<?> toAddList = new ArrayList<>(list);
                toAddList.removeAll(oldList);
                result.addAll(toAddList.stream().map(toAdd -> "INSERT" + buildObjReferenceInsert(attributeId, objectId, String.valueOf(((IdentifiableEnum) toAdd).getId()))).collect(Collectors.toList()));
                return result;
            } else if (listGenericType.equals(NamedEntity.class)) {

                List<?> toDeleteList = new ArrayList<>(oldList);
                toDeleteList.removeAll(list);
                for (Object toDelete : toDeleteList) {
                    NamedEntity toDeleteEntity = (NamedEntity) toDelete;
                    result.add(buildObjReferenceDelete(attributeId, objectId, String.valueOf(toDeleteEntity.getId())));
                    result.add(buildObjectDelete(String.valueOf(toDeleteEntity.getId())));
                }
                List<?> toAddList = new ArrayList<>(list);
                toAddList.removeAll(oldList);
                String listValueObjectTypeCode = sqlExecutor.execute(Queries.QUERY_GET_LIST_OBJECT_TYPE_CODE_BY_ATTR_ID, String.class, attributeId);
                for (Object toAdd : toAddList) {
                    NamedEntity toAddEntity = (NamedEntity) toAdd;
                    long listElemId = insertObjectAndReturnId(String.valueOf(toAddEntity.getName()), listValueObjectTypeCode);
                    toAddEntity.setId(listElemId);
                    result.add("INSERT" + buildAttributeInsert(String.valueOf(64), String.valueOf(listElemId), String.valueOf(toAddEntity.getName()), null));
                    result.add("INSERT" + buildObjReferenceInsert(String.valueOf(63), String.valueOf(listElemId), String.valueOf(sqlExecutor.execute(Queries.QUERY_GET_LIST_OBJECT_ID_BY_LIST_ITEM_OBJECT_TYPE_CODE, Long.class, listValueObjectTypeCode))));
                    result.add("INSERT" + buildObjReferenceInsert(attributeId, objectId, String.valueOf(listElemId)));
                }
                return result;
            } else if (BaseEntity.class.isAssignableFrom(listGenericType)) {
                List<?> toDeleteList = new ArrayList<>(oldList);
                toDeleteList.removeAll(list);
                result.addAll(toDeleteList.stream().map(toDelete -> buildObjReferenceDelete(attributeId, objectId, String.valueOf(((BaseEntity) toDelete).getId()))).collect(Collectors.toList()));
                List<?> toAddList = new ArrayList<>(list);
                toAddList.removeAll(oldList);
                for (Object value : toAddList) {
                    BaseEntity elem = (BaseEntity) value;
                    if (elem.isNew()) {
                        elem = insert(elem);
                    }
                    result.add("INSERT" + buildObjReferenceInsert(attributeId, objectId, elem.getId().toString()));
                }
                return result;
            }
            return result;
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
                    long attributeId = sqlExecutor.execute(Queries.QUERY_GET_ATTRIBUTE_ID_BY_CODE, Long.class, attributeCode);
                    String listValueObjectTypeCode = sqlExecutor.execute(Queries.QUERY_GET_LIST_OBJECT_TYPE_CODE_BY_ATTR_ID, String.class, attributeId);
                    long listElemId = insertObjectAndReturnId(String.valueOf(elem.getName()), listValueObjectTypeCode);
                    elem.setId(listElemId);
                    result.append(buildAttributeInsert(String.valueOf(64), String.valueOf(listElemId), String.valueOf(elem.getName()), null));
                    result.append(buildObjReferenceInsert(String.valueOf(63), String.valueOf(listElemId), String.valueOf(sqlExecutor.execute(Queries.QUERY_GET_LIST_OBJECT_ID_BY_LIST_ITEM_OBJECT_TYPE_CODE, Long.class, listValueObjectTypeCode))));
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
