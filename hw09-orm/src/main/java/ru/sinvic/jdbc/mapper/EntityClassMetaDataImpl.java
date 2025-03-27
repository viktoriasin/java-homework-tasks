package ru.sinvic.jdbc.mapper;

import ru.sinvic.jdbc.annotations.Column;
import ru.sinvic.jdbc.annotations.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final ClassMappingMetadata classMappingMetadata;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        classMappingMetadata = getClassMetadata();
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
    public FiledMappingMetadata getIdField() {
        return classMappingMetadata.idField();
    }

    @Override
    public List<FiledMappingMetadata> getAllFields() {
        Stream<FiledMappingMetadata> fieldNamesWithoutID = classMappingMetadata.fieldsWithoutId().stream();
        FiledMappingMetadata idName = classMappingMetadata.idField();
        return Stream.concat(fieldNamesWithoutID, Stream.of(idName)).toList();
    }

    @Override
    public List<FiledMappingMetadata> getFieldsWithoutId() {
        return classMappingMetadata.fieldsWithoutId().stream().toList();
    }

    @Override
    public ClassMappingMetadata getClassMappingMetadata() {
        return classMappingMetadata;
    }

    private ClassMappingMetadata getClassMetadata() {
        FiledMappingMetadata idField = null;
        List<FiledMappingMetadata> fieldsWithoutId = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FiledMappingMetadata fieldMappingMetadata = getFieldMappingMetadata(field);
            if (field.isAnnotationPresent(Id.class)) {
                idField = fieldMappingMetadata;
            } else {
                fieldsWithoutId.add(fieldMappingMetadata);
            }
        }
        return new ClassMappingMetadata(idField, fieldsWithoutId);
    }

    private FiledMappingMetadata getFieldMappingMetadata(Field field) {
        return new FiledMappingMetadata(field.getName(), getFieldJDBCColumnName(field), field);
    }

    private String getFieldJDBCColumnName(Field field) {
        String dbName = null;
        if (field.isAnnotationPresent(Column.class)) {
            dbName = field.getAnnotation(Column.class).name();
        }
        return dbName == null ? field.getName() : dbName;
    }
}
