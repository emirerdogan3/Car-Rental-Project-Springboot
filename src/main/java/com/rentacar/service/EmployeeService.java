package com.rentacar.service;

import com.rentacar.dto.EmployeeDTO;
// import com.rentacar.entity.User; // No longer directly used if UserDetails is preferred
import org.springframework.security.core.userdetails.UserDetails; // Added
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO, UserDetails currentUserDetails);
    EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO, UserDetails currentUserDetails);
    void deleteEmployee(Integer id);
    Optional<EmployeeDTO> getEmployeeById(Integer id);
    List<EmployeeDTO> getEmployeesByBranch(Integer branchId);
    List<EmployeeDTO> getAllEmployees();
} 