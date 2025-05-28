package com.rentacar.controller;

import com.rentacar.dto.RoleDTO;
import com.rentacar.service.RoleService;
import com.rentacar.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public AdminRoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    public String listRoles(Model model) {
        List<RoleDTO> roles = roleService.getAllRoles().stream()
                                .map(roleMapper::toDto)
                                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        return "admin/roles";
    }
    
    // CRUD operations for roles (add, edit, delete) can be added here if needed later.
    // For now, only listing is implemented as per the 'optional' nature.
} 