package com.rentacar.repository;

import com.rentacar.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByBranch_BranchID(Integer branchId);
} 