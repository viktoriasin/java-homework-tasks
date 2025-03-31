package ru.sinvic.jdbc.mapper;

import java.util.List;

public record ClassMetadata(FieldMappingMetadata idField, List<FieldMappingMetadata> fieldsWithoutId) {}
