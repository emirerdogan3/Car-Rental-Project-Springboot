package com.rentacar.service;

import com.rentacar.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Integer id);
    Role createRole(Role role);
    void deleteRole(Integer id);
} 