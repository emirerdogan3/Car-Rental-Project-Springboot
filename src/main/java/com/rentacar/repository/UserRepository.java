package com.rentacar.repository;

import com.rentacar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    long countByRole_RoleName(String roleName);
    List<User> findByRole_RoleName(String roleName);

    // Methods for EmployeeService checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndUserIDNot(String username, Integer userId);
    boolean existsByEmailAndUserIDNot(String email, Integer userId);
} 