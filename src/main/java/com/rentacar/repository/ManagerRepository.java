package com.rentacar.repository;

import com.rentacar.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    Optional<Manager> findByUser_UserID(Integer userId);
} 