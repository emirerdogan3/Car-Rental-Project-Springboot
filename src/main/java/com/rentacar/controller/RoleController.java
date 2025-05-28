package com.rentacar.controller;

import com.rentacar.entity.Role;
import com.rentacar.service.RoleService;
import com.rentacar.dto.RoleDTO;
import com.rentacar.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        logger.info("Tüm roller listeleniyor");
        return roleService.getAllRoles().stream().map(roleMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id)
                .map(roleMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RoleDTO createRole(@Valid @RequestBody RoleDTO roleDTO) {
        logger.info("Yeni rol oluşturuluyor: {}", roleDTO.getRoleName());
        Role role = roleMapper.toEntity(roleDTO);
        return roleMapper.toDto(roleService.createRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        logger.warn("Rol siliniyor: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
} 