package com.rentacar.service.impl;

import com.rentacar.dto.EmployeeDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.Employee;
import com.rentacar.entity.Role;
import com.rentacar.entity.User;
import com.rentacar.mapper.EmployeeMapper;
import com.rentacar.repository.BranchRepository;
import com.rentacar.repository.EmployeeRepository;
import com.rentacar.repository.RoleRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.service.EmployeeService;
import com.rentacar.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final EmployeeMapper employeeMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO, UserDetails currentUserDetails) {
        if (!StringUtils.hasText(employeeDTO.getUsername()) || !StringUtils.hasText(employeeDTO.getPassword())) {
            throw new IllegalArgumentException("Username and password must be provided for new employee.");
        }
        if (userRepository.existsByUsername(employeeDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + employeeDTO.getUsername());
        }
        if (StringUtils.hasText(employeeDTO.getEmail()) && userRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + employeeDTO.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(employeeDTO.getUsername());
        newUser.setEmail(employeeDTO.getEmail());
        newUser.setPhone(employeeDTO.getPhone());
        newUser.setPasswordHash(employeeDTO.getPassword());
        newUser.setEnabled(employeeDTO.getEnabled() != null ? employeeDTO.getEnabled() : true);

        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("Role 'EMPLOYEE' not found. Please ensure it exists."));
        newUser.setRole(employeeRole);
        User savedUser = userRepository.save(newUser);

        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee.setUser(savedUser);

        if (employeeDTO.getBranchID() == null) {
            throw new IllegalArgumentException("Branch ID must be provided for new employee.");
        }
        Branch branch = branchRepository.findById(employeeDTO.getBranchID())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + employeeDTO.getBranchID()));
        employee.setBranch(branch);
        
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO, UserDetails currentUserDetails) {
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
        
        User existingUser = existingEmployee.getUser();
        if (existingUser == null) {
            // Create new user account if missing
            existingUser = new User();
            existingUser.setUsername(employeeDTO.getUsername());
            existingUser.setEmail(employeeDTO.getEmail());
            existingUser.setPhone(employeeDTO.getPhone());
            existingUser.setEnabled(true);
            
            Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                    .orElseThrow(() -> new EntityNotFoundException("Role 'EMPLOYEE' not found"));
            existingUser.setRole(employeeRole);
            
            existingUser = userRepository.save(existingUser);
            existingEmployee.setUser(existingUser);
        }

        // Update user information if provided
        if (StringUtils.hasText(employeeDTO.getUsername())) {
            existingUser.setUsername(employeeDTO.getUsername());
        }
        if (StringUtils.hasText(employeeDTO.getEmail())) {
            existingUser.setEmail(employeeDTO.getEmail());
        }
        if (StringUtils.hasText(employeeDTO.getPhone())) {
            existingUser.setPhone(employeeDTO.getPhone());
        }
        if (StringUtils.hasText(employeeDTO.getPassword())) {
            existingUser.setPasswordHash(passwordEncoder.encode(employeeDTO.getPassword()));
        }
        if (employeeDTO.getEnabled() != null) {
            existingUser.setEnabled(employeeDTO.getEnabled());
        }

        // Update employee information
        if (StringUtils.hasText(employeeDTO.getFirstName())) {
            existingEmployee.setFirstName(employeeDTO.getFirstName());
        }
        if (StringUtils.hasText(employeeDTO.getLastName())) {
            existingEmployee.setLastName(employeeDTO.getLastName());
        }
        if (StringUtils.hasText(employeeDTO.getPositionTitle())) {
            existingEmployee.setPositionTitle(employeeDTO.getPositionTitle());
        }
        if (employeeDTO.getSalary() != null) {
            existingEmployee.setSalary(employeeDTO.getSalary());
        }

        // Validate and update branch if changed
        if (employeeDTO.getBranchID() != null) {
            if (existingEmployee.getBranch() == null || !employeeDTO.getBranchID().equals(existingEmployee.getBranch().getBranchID())) {
                Branch newBranch = branchRepository.findById(employeeDTO.getBranchID())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + employeeDTO.getBranchID()));
                existingEmployee.setBranch(newBranch);
            }
        }

        try {
            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            return employeeMapper.toDto(updatedEmployee);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update employee: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        
        // Delete the employee first
        employeeRepository.delete(employee);
        
        // Then delete the associated user if it exists
        if (employee.getUser() != null) {
            try {
                userRepository.delete(employee.getUser());
            } catch (Exception e) {
                // Log the error but don't throw it since the employee is already deleted
                System.err.println("Error deleting user account: " + e.getMessage());
            }
        }
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .map(this::ensureEnabledFieldNotNull);
    }

    @Override
    public List<EmployeeDTO> getEmployeesByBranch(Integer branchId) {
        if (branchId == null) {
            System.out.println("DEBUG: Branch ID is null, returning empty list");
            return List.of();
        }
        List<Employee> employees = employeeRepository.findByBranch_BranchID(branchId);
        System.out.println("DEBUG: Found " + employees.size() + " employees in database for branch ID: " + branchId);
        for (Employee emp : employees) {
            System.out.println("DEBUG: Employee - ID: " + emp.getEmployeeID() + ", Name: " + emp.getFirstName() + " " + emp.getLastName() + ", Branch: " + (emp.getBranch() != null ? emp.getBranch().getBranchID() : "null"));
        }
        return employees.stream()
                .map(employeeMapper::toDto)
                .map(this::ensureEnabledFieldNotNull) // Fix null enabled field
                .collect(Collectors.toList());
    }

    /**
     * Ensures the enabled field is never null in EmployeeDTO
     */
    private EmployeeDTO ensureEnabledFieldNotNull(EmployeeDTO dto) {
        if (dto.getEnabled() == null) {
            dto.setEnabled(true); // Default to true when null
        }
        return dto;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .map(this::ensureEnabledFieldNotNull)
                .collect(Collectors.toList());
    }
} 