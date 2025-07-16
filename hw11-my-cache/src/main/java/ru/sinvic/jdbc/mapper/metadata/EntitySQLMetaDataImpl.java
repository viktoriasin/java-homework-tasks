package ru.sinvic.jdbc.mapper.metadata;

import ru.sinvic.jdbc.mapper.EntityClassMetaData;
import ru.sinvic.jdbc.mapper.EntitySQLMetaData;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final ConcurrentHashMap<String, String> sqlRequestStorage = new ConcurrentHashMap<>();

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        String selectAll = "SELECT_ALL";
        sqlRequestStorage.computeIfAbsent(
            selectAll,
            k -> "SELECT " + String.join(", ", getAllColumnNames()) + " FROM " + entityClassMetaData.getName());
        return sqlRequestStorage.get(selectAll);
    }

    @Override
    public String getSelectByIdSql() {
        String selectById = "SELECT_BY_ID";
        sqlRequestStorage.computeIfAbsent(
            selectById,
            k -> "SELECT " + String.join(", ", getAllColumnNames())
                + " FROM "
                + entityClassMetaData.getName()
                + " WHERE "
                + entityClassMetaData.getIdField().jdbcColumnName()
                + " = " + " ? ");
        return sqlRequestStorage.get(selectById);
    }

    @Override
    public String getInsertSql() {
        String insert = "INSERT";
        sqlRequestStorage.computeIfAbsent(insert, k -> {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(entityClassMetaData.getName());
            List<String> allColumnNamesWithoutId = getAllColumnNamesWithoutId();
            sb.append("(").append(String.join(", ", allColumnNamesWithoutId)).append(")");
            sb.append(" VALUES ");
            sb.append("(")
                .append(String.join(
                    ", ", "?".repeat(allColumnNamesWithoutId.size()).split("")))
                .append(")");
            return sb.toString();
        });
        return sqlRequestStorage.get(insert);
    }

    @Override
    public String getUpdateSql() {
        String update = "UPDATE";
        sqlRequestStorage.computeIfAbsent(update, k -> {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ");
            sb.append(entityClassMetaData.getName());
            sb.append(" SET ");
            List<String> list = getAllColumnNamesWithoutId().stream()
                .map(name -> name + " = ?")
                .toList();
            sb.append(String.join(",", list));
            sb.append(" WHERE ");
            sb.append(entityClassMetaData.getIdField().jdbcColumnName()).append(" = ?");
            return sb.toString();
        });
        return sqlRequestStorage.get(update);
    }

    private List<String> getAllColumnNamesWithoutId() {
        return entityClassMetaData.getFieldsWithoutId().stream()
            .map(FieldMappingMetadata::jdbcColumnName)
            .toList();
    }

    private List<String> getAllColumnNames() {
        return entityClassMetaData.getAllFields().stream()
            .map(FieldMappingMetadata::jdbcColumnName)
            .toList();
    }
}
