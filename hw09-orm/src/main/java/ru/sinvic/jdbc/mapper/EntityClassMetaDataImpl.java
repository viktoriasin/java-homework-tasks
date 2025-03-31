package ru.sinvic.jdbc.mapper;

import ru.sinvic.jdbc.annotations.Column;
import ru.sinvic.jdbc.annotations.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final ClassMetadata classMetadata;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        classMetadata = getClassMetadata();
    }

    @Override
    public String getName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FieldMappingMetadata getIdField() {
        return classMetadata.idField();
    }

    @Override
    public List<FieldMappingMetadata> getAllFields() {
        Stream<FieldMappingMetadata> fieldNamesWithoutID = classMetadata.fieldsWithoutId().stream();
        FieldMappingMetadata idName = classMetadata.idField();
        return Stream.concat(fieldNamesWithoutID, Stream.of(idName)).toList();
    }

    @Override
    public List<FieldMappingMetadata> getFieldsWithoutId() {
        return classMetadata.fieldsWithoutId().stream().toList();
    }

    @Override
    public List<Method> getGetterMethods() {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            List<Method> getterMethods = new ArrayList<>();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    getterMethods.add(method);
                }
            }
            return getterMethods;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ClassMetadata getClassMetadata() {
        FieldMappingMetadata idField = null;
        List<FieldMappingMetadata> fieldsWithoutId = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldMappingMetadata fieldMappingMetadata = getFieldMappingMetadata(field);
            if (field.isAnnotationPresent(Id.class)) {
                idField = fieldMappingMetadata;
            } else {
                fieldsWithoutId.add(fieldMappingMetadata);
            }
        }
        return new ClassMetadata(idField, fieldsWithoutId);
    }

    private FieldMappingMetadata getFieldMappingMetadata(Field field) {
        return new FieldMappingMetadata(field.getName(), getFieldJDBCColumnName(field), field);
    }

    private String getFieldJDBCColumnName(Field field) {
        String dbName = null;
        if (field.isAnnotationPresent(Column.class)) {
            dbName = field.getAnnotation(Column.class).name();
        }
        return dbName == null ? field.getName() : dbName;
    }
}
