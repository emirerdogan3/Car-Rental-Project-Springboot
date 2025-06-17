package com.rentacar.repository;

import com.rentacar.entity.EmployeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Date;

public interface EmployeePaymentRepository extends JpaRepository<EmployeePayment, Integer> {
    
    // Employee'nin tüm maaş ödemelerini getir
    List<EmployeePayment> findByEmployee_EmployeeIDOrderByPaymentDateDesc(Integer employeeId);
    
    // Branch'in tüm maaş ödemelerini getir
    @Query("SELECT ep FROM EmployeePayment ep WHERE ep.employee.branch.branchID = :branchId ORDER BY ep.paymentDate DESC")
    List<EmployeePayment> findByBranchIDOrderByPaymentDateDesc(@Param("branchId") Integer branchId);
    
    // Manager'ın yaptığı maaş ödemelerini getir
    List<EmployeePayment> findByPaidBy_UserIDOrderByPaymentDateDesc(Integer managerId);
    
    // Belirli tarih aralığındaki ödemeleri getir
    @Query("SELECT ep FROM EmployeePayment ep WHERE ep.paymentDate BETWEEN :startDate AND :endDate ORDER BY ep.paymentDate DESC")
    List<EmployeePayment> findByPaymentDateBetweenOrderByPaymentDateDesc(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // YENİ EKLENEN METOT - Hatanın kaynağı bu!
    boolean existsByAccount_Branch_BranchID(Integer branchId);
} 