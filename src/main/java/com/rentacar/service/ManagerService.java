package com.rentacar.service;

import com.rentacar.entity.Manager;
import java.util.List;
import java.util.Optional;

public interface ManagerService {
    Manager createManager(Manager manager);
    Manager updateManager(Integer id, Manager manager);
    void deleteManager(Integer id);
    Optional<Manager> getManagerById(Integer id);
    List<Manager> getAllManagers();
} 