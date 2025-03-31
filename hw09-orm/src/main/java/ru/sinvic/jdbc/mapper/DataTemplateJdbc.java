package ru.sinvic.jdbc.mapper;

import ru.sinvic.core.repository.DataTemplate;
import ru.sinvic.core.repository.DataTemplateException;
import ru.sinvic.core.repository.executor.DbExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final InstanceUtils<T> instanceUtils;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData,
                            InstanceUtils<T> instanceUtils) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.instanceUtils = instanceUtils;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return instanceUtils.constructNewInstanceFromResultSet(rs);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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
                        objectsList.add(instanceUtils.constructNewInstanceFromResultSet(rs));
                    }
                    return objectsList;
                } catch (SQLException e) {
                    throw new DataTemplateException(e);
                }
            })
            .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(),  instanceUtils.getValuesOfInstanceFieldsWithoutId(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            dbExecutor.executeStatement(
                connection, entitySQLMetaData.getUpdateSql(), instanceUtils.getValuesOfInstanceFieldsWithId(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}
