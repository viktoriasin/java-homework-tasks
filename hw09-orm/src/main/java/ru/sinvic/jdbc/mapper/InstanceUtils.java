package ru.sinvic.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstanceUtils<T> {
    private final EntityClassMetaData<T> entityClassMetaData;

    public InstanceUtils(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public T constructNewInstanceFromResultSet(ResultSet rs) {
        T genericType = getInstanceOfGenericType();
        setFieldsOfGenericType(rs, genericType);
        return genericType;
    }

    public void setFieldsOfGenericType(ResultSet rs, T o) {
        List<Field> fieldsWithoutId = entityClassMetaData.getAllFields().stream().map(FieldMappingMetadata::field).toList();
        for (Field field : fieldsWithoutId) {
            setField(field, o, rs);
        }
    }

    public T getInstanceOfGenericType() {
        Constructor<?> declaredConstructor = entityClassMetaData.getConstructor();
        T o;
        try {
            o = (T) declaredConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    public List<Object> getValuesOfInstanceFields(T object) {
        String idFieldName = entityClassMetaData.getIdField().entityPropertyName();
        List<Object> valuesList = new ArrayList<>();
        for (Method method : entityClassMetaData.getGetterMethods()) {
            if (!method.getName().toLowerCase().endsWith(idFieldName.toLowerCase())) {
                try {
                    valuesList.add(method.invoke(object));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Collections.unmodifiableList(valuesList);
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
}
