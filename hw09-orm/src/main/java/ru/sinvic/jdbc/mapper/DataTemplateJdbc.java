package ru.sinvic.jdbc.mapper;

import ru.sinvic.core.repository.DataTemplate;
import ru.sinvic.core.repository.DataTemplateException;
import ru.sinvic.core.repository.executor.DbExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return constructNewInstanceFromResultSet(rs);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return (List<T>) dbExecutor
            .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                var objectsList = new ArrayList<>();
                try {
                    while (rs.next()) {
                        objectsList.add(constructNewInstanceFromResultSet(rs));
                    }
                    return objectsList;
                } catch (SQLException e) {
                    throw new DataTemplateException(e);
                }
            })
            .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(),  getParametersList(client));  // TODO: сделать так, чтобы значения соответствовали списку в SQL!!!
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            dbExecutor.executeStatement(
                connection, entitySQLMetaData.getUpdateSql(), getParametersList(client)); // TODO: сделать так, чтобы значения соответствовали списку в SQL!!!
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T constructNewInstanceFromResultSet(ResultSet rs) {
        T genericType = getInstanceOfGenericType();
        setFieldsOfGenericType(rs, genericType);
        return genericType;
    }

    private void setFieldsOfGenericType(ResultSet rs, T o) {
        List<Field> fieldsWithoutId = entityClassMetaData.getAllFields().stream().map(FieldMappingMetadata::field).toList();
        for (Field field : fieldsWithoutId) {
            setField(field, o, rs);
        }
    }

    private T getInstanceOfGenericType() {
        Constructor<?> declaredConstructor = entityClassMetaData.getConstructor();
        T o;
        try {
            o = (T) declaredConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    private void setField(Field field, T object, ResultSet rs) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        try {
            if (type == int.class) {
                field.set(object, rs.getInt(fieldName));
            } else if (type == long.class) {
                field.set(object, rs.getLong(fieldName));
            } else if (type == byte.class) {
                field.set(object, rs.getByte(fieldName));
            } else if (type == short.class) {
                field.set(object, rs.getShort(fieldName));
            } else if (type == double.class) {
                field.set(object, rs.getDouble(fieldName));
            } else if (type == boolean.class) {
                field.set(object, rs.getBoolean(fieldName));
            } else {
                field.set(object, rs.getObject(fieldName));
            }
        } catch (IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Method> getGetterMethods(Class<?> clazz) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            List<Method> getterMethods = new ArrayList<>();
            for (Method method : methods) {
                if (method.getName().startsWith("get")){
                    getterMethods.add(method);
                }
            }
            return getterMethods;
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    private List<Object> getParametersList(T object) throws IllegalAccessException, InvocationTargetException {
        String idFieldName = entityClassMetaData.getIdField().entityPropertyName();
        List<Object> valuesList = new ArrayList<>();
        for (Method method : getGetterMethods(object.getClass())) {
            if (!method.getName().toLowerCase().endsWith(idFieldName.toLowerCase())) {
                valuesList.add(method.invoke(object));
            }
        }
        return Collections.unmodifiableList(valuesList);
    }
}
