package ru.sinvic.jdbc.mapper;

import java.lang.reflect.Field;

public record FiledMappingMetadata(String entityPropertyName, String jdbcColumnName, Field field) {
}
