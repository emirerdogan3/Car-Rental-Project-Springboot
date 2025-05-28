package com.rentacar.controller;

import com.rentacar.dto.RoleDTO;
import com.rentacar.dto.UserDTO;
import com.rentacar.service.RoleService;
import com.rentacar.service.UserService;
import com.rentacar.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;
    private final RoleMapper roleMapper; // For converting Role entities to RoleDTOs for the view

    @Autowired
    public AdminUserController(UserService userService, RoleService roleService, 
                             RoleMapper roleMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers(); // Assuming this now returns DTOs with role name and enabled status
        List<RoleDTO> roles = roleService.getAllRoles().stream()
                                .map(roleMapper::toDto)
                                .collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("allRoles", roles); // For role change dropdowns
        if (!model.containsAttribute("userForm")) { // For optional add new user form
            model.addAttribute("userForm", new UserDTO());
        }
        return "admin/users";
    }

    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam("userId") Integer userId, 
                                 @RequestParam("roleId") Integer roleId, 
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(userId, roleId);
            redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/set-enabled")
    public String setUserEnabled(@RequestParam("userId") Integer userId, 
                               @RequestParam("enabled") boolean enabled,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.setUserEnabled(userId, enabled);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Optional: Add New User by Admin
    @PostMapping("/add")
    public String addUser(@ModelAttribute("userForm") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            // Ensure role is set if roleId is part of UserDTO or handled by service
            if (userDTO.getRole() == null || userDTO.getRole().getRoleID() == null) {
                throw new IllegalArgumentException("Role must be selected for new user.");
            }
            
            // Get role name from roleID
            String roleName = roleService.getAllRoles().stream()
                    .filter(role -> role.getRoleID().equals(userDTO.getRole().getRoleID()))
                    .map(role -> role.getRoleName())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role selected."));
            
            userDTO.getRole().setRoleName(roleName);
            userService.createUserByAdmin(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
} 