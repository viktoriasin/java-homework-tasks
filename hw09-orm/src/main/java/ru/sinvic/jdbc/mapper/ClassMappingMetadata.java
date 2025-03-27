package ru.sinvic.jdbc.mapper;

import java.util.List;

public record ClassMappingMetadata(FiledMappingMetadata idField, List<FiledMappingMetadata> fieldsWithoutId) {
}
