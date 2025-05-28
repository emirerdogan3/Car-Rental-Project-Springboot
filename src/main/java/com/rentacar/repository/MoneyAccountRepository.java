package com.rentacar.repository;

import com.rentacar.entity.MoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyAccountRepository extends JpaRepository<MoneyAccount, Integer> {
    Optional<MoneyAccount> findByBranch_BranchID(Integer branchId);
} 