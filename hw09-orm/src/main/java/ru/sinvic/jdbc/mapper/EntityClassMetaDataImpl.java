package ru.sinvic.jdbc.mapper;

import ru.sinvic.jdbc.annotations.Column;
import ru.sinvic.jdbc.annotations.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        Map<String, Method> methodMap = Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toMap(method -> uncapitilize(method.getName()), method -> method));
        for (Field field : clazz.getDeclaredFields()) {
            FieldMappingMetadata fieldMappingMetadata = getFieldMappingMetadata(field, methodMap.get(field.getName()));
            if (field.isAnnotationPresent(Id.class)) {
                idField = fieldMappingMetadata;
            } else {
                fieldsWithoutId.add(fieldMappingMetadata);
            }
        }
        return new ClassMetadata(idField, fieldsWithoutId);
    }

    private static String uncapitilize(String methodName) {
        String substring = methodName.substring(3);
        return substring.substring(0, 1).toLowerCase() + substring.substring(1);
    }

    private FieldMappingMetadata getFieldMappingMetadata(Field field, Method getterMethod) {
        return new FieldMappingMetadata(field.getName(), getFieldJDBCColumnName(field), field, getterMethod);
    }

    private String getFieldJDBCColumnName(Field field) {
        String dbName = null;
        if (field.isAnnotationPresent(Column.class)) {
            dbName = field.getAnnotation(Column.class).name();
        }
        return dbName == null ? field.getName() : dbName;
    }
}
