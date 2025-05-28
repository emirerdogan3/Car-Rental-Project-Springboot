package com.rentacar.repository;

import com.rentacar.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUser_UserID(Integer userId);
    // Optional<Customer> findByUser_Username(String username); // Gerekirse eklenebilir
} 