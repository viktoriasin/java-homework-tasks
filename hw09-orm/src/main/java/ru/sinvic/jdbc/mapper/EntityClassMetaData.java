package ru.sinvic.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.util.List;

/** "Разбирает" объект на составные части */
public interface EntityClassMetaData<T> {
    String getName();

    Constructor<T> getConstructor();

    // Поле Id должно определять по наличию аннотации Id
    // Аннотацию @Id надо сделать самостоятельно
    FiledMappingMetadata getIdField();

    List<FiledMappingMetadata> getAllFields();

    List<FiledMappingMetadata> getFieldsWithoutId();

    public ClassMappingMetadata getClassMappingMetadata();
}
