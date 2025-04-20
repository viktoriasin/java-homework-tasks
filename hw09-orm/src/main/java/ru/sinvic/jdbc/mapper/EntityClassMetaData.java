package ru.sinvic.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.util.List;
import ru.sinvic.jdbc.mapper.metadata.FieldMappingMetadata;

/** "Разбирает" объект на составные части */
public interface EntityClassMetaData<T> {
    String getName();

    Constructor<T> getConstructor();

    // Поле Id должно определять по наличию аннотации Id
    // Аннотацию @Id надо сделать самостоятельно
    FieldMappingMetadata getIdField();

    List<FieldMappingMetadata> getAllFields();

    List<FieldMappingMetadata> getFieldsWithoutId();
}
