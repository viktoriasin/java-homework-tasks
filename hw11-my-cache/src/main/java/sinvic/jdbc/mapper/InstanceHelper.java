package sinvic.jdbc.mapper;

import ru.sinvic.jdbc.mapper.EntityClassMetaData;
import ru.sinvic.jdbc.mapper.metadata.FieldMappingMetadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("java:S112")
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

    @SuppressWarnings("unchecked")
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
        String fieldName = fieldMappingMetadata.jdbcColumnName();
        Method setterMethod = fieldMappingMetadata.setter();
        try {
            setterMethod.invoke(object, rs.getObject(fieldName));
        } catch (IllegalAccessException | SQLException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(T object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
