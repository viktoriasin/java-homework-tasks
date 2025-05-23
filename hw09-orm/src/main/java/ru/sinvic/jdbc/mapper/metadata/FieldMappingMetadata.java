package ru.sinvic.jdbc.mapper.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public record FieldMappingMetadata(
        String entityPropertyName, String jdbcColumnName, Field field, Method getter, Method setter) {}
