package ru.sinvic.jdbc.mapper;

import java.lang.reflect.Field;

public record FieldMappingMetadata(String entityPropertyName, String jdbcColumnName, Field field) {
}
