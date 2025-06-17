package com.rentacar.repository;

import com.rentacar.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Integer> {
    // YENİ EKLENEN METOT
    boolean existsByAccount_Branch_BranchID(Integer branchId);
} 