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
import ru.sinvic.core.repository.DataTemplateException;
import ru.sinvic.jdbc.mapper.metadata.FieldMappingMetadata;

public class InstanceHelper<T> {
    private final EntityClassMetaData<T> entityClassMetaData;

    public InstanceHelper(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public T constructNewInstanceFromResultSet(ResultSet rs) {
        T genericType = getInstanceOfGenericType();
        setFieldsOfGenericType(rs, genericType);
        return genericType;
    }

    public void setFieldsOfGenericType(ResultSet rs, T o) {
        List<FieldMappingMetadata> fields =
                entityClassMetaData.getAllFields().stream().toList();
        for (FieldMappingMetadata field : fields) {
            setField(field, o, rs);
        }
    }

    public T getInstanceOfGenericType() {
        Constructor<?> declaredConstructor = entityClassMetaData.getConstructor();
        T o;
        try {
            o = (T) declaredConstructor.newInstance();
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
        return o;
    }

    public List<Object> getValuesOfInstanceFieldsWithoutId(T object) {
        String idFieldName = entityClassMetaData.getIdField().entityPropertyName();
        List<Object> valuesList = new ArrayList<>();
        for (Method method : entityClassMetaData.getFieldsWithoutId().stream()
                .map(FieldMappingMetadata::getter)
                .toList()) {
            if (!method.getName().toLowerCase().endsWith(idFieldName.toLowerCase())) {
                valuesList.add(getFieldValue(object, method));
            }
        }
        return Collections.unmodifiableList(valuesList);
    }

    public List<Object> getValuesOfInstanceFieldsWithId(T object) {
        List<Object> result = new ArrayList<>(getValuesOfInstanceFieldsWithoutId(object));
        result.add(getFieldValue(object, entityClassMetaData.getIdField().getter()));
        return result;
    }

    private void setField(FieldMappingMetadata fieldMappingMetadata, T object, ResultSet rs) {
        Field entityField = fieldMappingMetadata.field();
        Class<?> type = entityField.getType();
        String fieldName = fieldMappingMetadata.jdbcColumnName();
        Method setterMethod = fieldMappingMetadata.setter();
        try {
            if (type == int.class) {
                setterMethod.invoke(object, rs.getInt(fieldName));
            } else if (type == long.class) {
                setterMethod.invoke(object, rs.getLong(fieldName));
            } else if (type == byte.class) {
                setterMethod.invoke(object, rs.getByte(fieldName));
            } else if (type == short.class) {
                setterMethod.invoke(object, rs.getShort(fieldName));
            } else if (type == double.class) {
                setterMethod.invoke(object, rs.getDouble(fieldName));
            } else if (type == boolean.class) {
                setterMethod.invoke(object, rs.getBoolean(fieldName));
            } else {
                setterMethod.invoke(object, rs.getObject(fieldName));
            }
        } catch (IllegalAccessException | SQLException | InvocationTargetException e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getFieldValue(T object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(e);
        }
    }
}
