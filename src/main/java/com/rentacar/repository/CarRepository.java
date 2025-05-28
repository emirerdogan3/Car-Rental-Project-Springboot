package com.rentacar.repository;

import com.rentacar.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    boolean existsByPlateNumber(String plateNumber);
    boolean existsByPlateNumberAndCarIDNot(String plateNumber, Integer carId);
    long countByStatus(String status);

    // New methods for dashboard stats
    long countByBranch_BranchID(Integer branchId);
    long countByBranch_BranchIDAndStatus(Integer branchId, String status);
} 