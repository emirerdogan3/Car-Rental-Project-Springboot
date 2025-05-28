package com.rentacar.service;

import com.rentacar.entity.EmployeePayment;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface EmployeePaymentService {
    
    // Manager employee'ye belirli bir branch'ten maaş öder
    EmployeePayment paySalary(Integer employeeId, Integer branchId, UserDetails managerUser);
    
    // Employee'nin maaş ödemelerini getir
    List<EmployeePayment> getEmployeePayments(Integer employeeId);
    
    // Branch'in maaş ödemelerini getir
    List<EmployeePayment> getBranchPayments(Integer branchId);
    
    // Manager'ın yaptığı ödemeleri getir
    List<EmployeePayment> getManagerPayments(UserDetails managerUser);
} 