package ru.sinvic.crm.service;

import java.util.List;
import java.util.Optional;
import ru.sinvic.crm.model.Manager;

public interface DBServiceManager {

    Manager saveManager(Manager client);

    Optional<Manager> getManager(long no);

    List<Manager> findAll();
}
