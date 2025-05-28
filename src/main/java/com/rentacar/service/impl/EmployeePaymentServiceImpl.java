package com.rentacar.service.impl;

import com.rentacar.entity.*;
import com.rentacar.repository.*;
import com.rentacar.service.EmployeePaymentService;
import com.rentacar.service.BranchManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeePaymentServiceImpl implements EmployeePaymentService {
    
    private final EmployeePaymentRepository employeePaymentRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final MoneyAccountRepository moneyAccountRepository;
    private final BranchManagerService branchManagerService;

    @Override
    @Transactional
    public EmployeePayment paySalary(Integer employeeId, Integer branchId, UserDetails managerUser) {
        // Employee'yi getir
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
        
        // Manager user'ını getir
        User manager = userRepository.findByUsername(managerUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found: " + managerUser.getUsername()));
        
        // Manager'ın bu branch'e erişimi olduğunu kontrol et
        List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(manager.getUserID());
        boolean hasAccessToBranch = managedBranches.stream()
                .anyMatch(branch -> branch.getBranchID().equals(branchId));
        
        if (!hasAccessToBranch) {
            throw new IllegalArgumentException("Manager does not have access to branch ID: " + branchId);
        }
        
        // Employee'nin maaşını kontrol et
        Double salary = employee.getSalary();
        if (salary == null || salary <= 0) {
            throw new IllegalArgumentException("Employee salary is not set or invalid");
        }
        
        // Seçilen branch'in money account'ını getir
        MoneyAccount branchAccount = moneyAccountRepository.findByBranch_BranchID(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch money account not found for branch ID: " + branchId));
        
        // Yeterli bakiye kontrolü
        if (branchAccount.getMoneyBalance() < salary) {
            throw new IllegalStateException("Insufficient funds in branch account. Required: " + salary + 
                                          ", Available: " + branchAccount.getMoneyBalance());
        }
        
        // Branch account'tan maaşı düş
        branchAccount.setMoneyBalance(branchAccount.getMoneyBalance() - salary);
        moneyAccountRepository.save(branchAccount);
        
        // EmployeePayment kaydı oluştur
        EmployeePayment payment = EmployeePayment.builder()
                .employee(employee)
                .account(branchAccount)
                .amount(salary)
                .paymentDate(new Date())
                .description("Salary payment for " + employee.getFirstName() + " " + employee.getLastName() + 
                           " from " + branchAccount.getBranch().getBranchName())
                .paidBy(manager)
                .build();
        
        return employeePaymentRepository.save(payment);
    }

    @Override
    public List<EmployeePayment> getEmployeePayments(Integer employeeId) {
        return employeePaymentRepository.findByEmployee_EmployeeIDOrderByPaymentDateDesc(employeeId);
    }

    @Override
    public List<EmployeePayment> getBranchPayments(Integer branchId) {
        return employeePaymentRepository.findByBranchIDOrderByPaymentDateDesc(branchId);
    }

    @Override
    public List<EmployeePayment> getManagerPayments(UserDetails managerUser) {
        User manager = userRepository.findByUsername(managerUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found: " + managerUser.getUsername()));
        return employeePaymentRepository.findByPaidBy_UserIDOrderByPaymentDateDesc(manager.getUserID());
    }
} 