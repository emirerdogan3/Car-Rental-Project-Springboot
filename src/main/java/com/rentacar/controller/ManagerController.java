package com.rentacar.controller;

import com.rentacar.dto.ManagerDTO;
import com.rentacar.entity.Manager;
import com.rentacar.mapper.ManagerMapper;
import com.rentacar.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/managers")
@PreAuthorize("hasRole('ADMIN')")
public class ManagerController {
    private final ManagerService managerService;
    private final ManagerMapper managerMapper;

    @Autowired
    public ManagerController(ManagerService managerService, ManagerMapper managerMapper) {
        this.managerService = managerService;
        this.managerMapper = managerMapper;
    }

    @PostMapping
    public ManagerDTO createManager(@RequestBody ManagerDTO managerDTO) {
        Manager manager = managerMapper.toEntity(managerDTO);
        return managerMapper.toDto(managerService.createManager(manager));
    }

    @PutMapping("/{id}")
    public ManagerDTO updateManager(@PathVariable Integer id, @RequestBody ManagerDTO managerDTO) {
        Manager manager = managerMapper.toEntity(managerDTO);
        return managerMapper.toDto(managerService.updateManager(id, manager));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Integer id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerDTO> getManagerById(@PathVariable Integer id) {
        Optional<Manager> manager = managerService.getManagerById(id);
        return manager.map(value -> ResponseEntity.ok(managerMapper.toDto(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ManagerDTO> getAllManagers() {
        return managerService.getAllManagers().stream()
                .map(managerMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/manager")
    public String managerDashboard(Model model) {
        // İleride yönetici özet bilgileri eklenebilir
        return "manager/index";
    }
} 