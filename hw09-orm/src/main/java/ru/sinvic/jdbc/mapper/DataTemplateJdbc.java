package ru.sinvic.jdbc.mapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.sinvic.core.repository.DataTemplate;
import ru.sinvic.core.repository.DataTemplateException;
import ru.sinvic.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final InstanceHelper<T> instanceHelper;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, InstanceHelper<T> instanceHelper) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.instanceHelper = instanceHelper;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return instanceHelper.constructNewInstanceFromResultSet(rs);
                }
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
            return null;
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return (List<T>) dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    var objectsList = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            objectsList.add(instanceHelper.constructNewInstanceFromResultSet(rs));
                        }
                        return objectsList;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            return dbExecutor.executeStatement(
                    connection,
                    entitySQLMetaData.getInsertSql(),
                    instanceHelper.getValuesOfInstanceFieldsWithoutId(object));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        try {
            dbExecutor.executeStatement(
                    connection,
                    entitySQLMetaData.getUpdateSql(),
                    instanceHelper.getValuesOfInstanceFieldsWithId(object));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}
