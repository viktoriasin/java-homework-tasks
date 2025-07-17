package ru.sinvic.jdbc.mapper.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.sinvic.core.repository.DataTemplateException;
import ru.sinvic.jdbc.annotations.Column;
import ru.sinvic.jdbc.annotations.Id;
import ru.sinvic.jdbc.mapper.EntityClassMetaData;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final ClassMetadata classMetadata;

    private String name;
    private Constructor<T> constructor;
    private FieldMappingMetadata idField;
    private List<FieldMappingMetadata> allFields;
    private List<FieldMappingMetadata> allFieldsWithoutID;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        classMetadata = getClassMetadata();
        parseMetaData();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public FieldMappingMetadata getIdField() {
        return idField;
    }

    @Override
    public List<FieldMappingMetadata> getAllFields() {
        return allFields;
    }

    @Override
    public List<FieldMappingMetadata> getFieldsWithoutId() {
        return allFieldsWithoutID;
    }

    private void parseMetaData() {
        name = parseName();
        constructor = parseConstructor();
        idField = parseIdField();
        allFields = parseAllFields();
        allFieldsWithoutID = parseAllFieldsWithoutId();
    }

    private List<FieldMappingMetadata> parseAllFieldsWithoutId() {
        return classMetadata.fieldsWithoutId().stream().toList();
    }

    private List<FieldMappingMetadata> parseAllFields() {
        Stream<FieldMappingMetadata> fieldNamesWithoutID = classMetadata.fieldsWithoutId().stream();
        FieldMappingMetadata idName = classMetadata.idField();
        return Stream.concat(fieldNamesWithoutID, Stream.of(idName)).toList();
    }

    private FieldMappingMetadata parseIdField() {
        return classMetadata.idField();
    }

    private Constructor<T> parseConstructor() {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DataTemplateException(e);
        }
    }

    private String parseName() {
        return clazz.getSimpleName().toLowerCase();
    }

    private ClassMetadata getClassMetadata() {
        FieldMappingMetadata idEntityField = null;
        List<FieldMappingMetadata> fieldsWithoutId = new ArrayList<>();
        Map<String, Method> getterMethodMap = getMethods("get");
        Map<String, Method> setterMethodMap = getMethods("set");
        for (Field field : clazz.getDeclaredFields()) {
            FieldMappingMetadata fieldMappingMetadata = getFieldMappingMetadata(
                    field, getterMethodMap.get(field.getName()), setterMethodMap.get(field.getName()));
            if (field.isAnnotationPresent(Id.class)) {
                idEntityField = fieldMappingMetadata;
            } else {
                fieldsWithoutId.add(fieldMappingMetadata);
            }
        }
        return new ClassMetadata(idEntityField, fieldsWithoutId);
    }

    private Map<String, Method> getMethods(String firstLettersInMethodName) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().startsWith(firstLettersInMethodName))
                .collect(Collectors.toMap(method -> uncapitilize(method.getName()), method -> method));
    }

    private String uncapitilize(String methodName) {
        String substring = methodName.substring(3);
        return substring.substring(0, 1).toLowerCase() + substring.substring(1);
    }

    private FieldMappingMetadata getFieldMappingMetadata(Field field, Method getterMethod, Method setterMethod) {
        return new FieldMappingMetadata(
                field.getName(), getFieldJDBCColumnName(field), field, getterMethod, setterMethod);
    }

    private String getFieldJDBCColumnName(Field field) {
        String dbName = null;
        if (field.isAnnotationPresent(Column.class)) {
            dbName = field.getAnnotation(Column.class).name();
        }
        return dbName == null ? field.getName() : dbName;
    }
}
