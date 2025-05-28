package com.rentacar.service.impl;

import com.rentacar.entity.Manager;
import com.rentacar.repository.ManagerRepository;
import com.rentacar.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public Manager createManager(Manager manager) {
        return managerRepository.save(manager);
    }

    @Override
    public Manager updateManager(Integer id, Manager manager) {
        manager.setManagerID(id);
        return managerRepository.save(manager);
    }

    @Override
    public void deleteManager(Integer id) {
        managerRepository.deleteById(id);
    }

    @Override
    public Optional<Manager> getManagerById(Integer id) {
        return managerRepository.findById(id);
    }

    @Override
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }
} 