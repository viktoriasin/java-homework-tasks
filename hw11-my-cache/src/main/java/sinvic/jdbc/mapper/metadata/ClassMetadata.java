package sinvic.jdbc.mapper.metadata;

import java.util.List;

public record ClassMetadata(FieldMappingMetadata idField, List<FieldMappingMetadata> fieldsWithoutId) {}
