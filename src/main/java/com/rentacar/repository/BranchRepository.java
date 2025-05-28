package com.rentacar.repository;

import com.rentacar.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BranchRepository extends JpaRepository<Branch, Integer>, JpaSpecificationExecutor<Branch> {
    boolean existsByBranchName(String branchName);
    boolean existsByBranchNameAndBranchIDNot(String branchName, Integer branchId);
} 