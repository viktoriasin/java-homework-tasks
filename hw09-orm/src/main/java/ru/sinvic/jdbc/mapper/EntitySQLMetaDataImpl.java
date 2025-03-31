package ru.sinvic.jdbc.mapper;

import java.util.List;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT * " +
            " FROM " +
            entityClassMetaData.getName();
    }

    @Override
    public String getSelectByIdSql() {
        return "SELECT " +
            String.join(", ", getAllColumnNamesWithoutId()) +
            " FROM " +
            entityClassMetaData.getName() +
            " WHERE " +
            entityClassMetaData.getIdField() + " = " + " ? ";
    }

    @Override
    public String getInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(entityClassMetaData.getName());
        List<String> allFieldsNames = getAllColumnNamesWithoutId();
        sb.append("(").append(String.join(", ", allFieldsNames)).append(")");
        sb.append(" VALUES ");
        sb.append("(").append(String.join(", ", "?".repeat(allFieldsNames.size()))).append(")");
        return sb.toString();
    }

    @Override
    public String getUpdateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(entityClassMetaData.getName());
        sb.append(" SET ");
        List<String> list = getAllColumnNamesWithoutId().stream().map(name -> name + " = ?").toList();
        sb.append(String.join(",", list));
        sb.append(" WHERE ");
        sb.append(entityClassMetaData.getIdField()).append(" = ?");
        return sb.toString();
    }

    private List<String> getAllColumnNamesWithoutId() {
        return entityClassMetaData.getFieldsWithoutId().stream().map(FieldMappingMetadata::jdbcColumnName).toList();
    }
}
