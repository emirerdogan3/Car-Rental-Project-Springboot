package com.rentacar.service.impl;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.dto.UserDTO;
import com.rentacar.dto.UserProfileDTO;
import com.rentacar.entity.Customer;
import com.rentacar.entity.Employee;
import com.rentacar.entity.Manager;
import com.rentacar.entity.Role;
import com.rentacar.entity.User;
import com.rentacar.mapper.CustomerMapper;
import com.rentacar.mapper.RoleMapper;
import com.rentacar.mapper.UserMapper;
import com.rentacar.repository.CustomerRepository;
import com.rentacar.repository.EmployeeRepository;
import com.rentacar.repository.ManagerRepository;
import com.rentacar.repository.RoleRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.FeedbackRepository;
import com.rentacar.service.BranchManagerService;
import com.rentacar.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BranchManagerService branchManagerService;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final ReservationRepository reservationRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)){
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        customerRepository.findByUser_UserID(id).ifPresent(customerRepository::delete);
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserDTO> findDtoByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    @Override
    public Optional<User> findEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDTO> findDtosByRoleName(String roleName) {
        List<User> users = userRepository.findByRole_RoleName(roleName);
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean checkPassword(User user, String rawPassword) {
        if (user == null || user.getPasswordHash() == null || rawPassword == null) return false;
        return user.getPasswordHash().equals(rawPassword);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword, UserDetails currentUser) {
        if (currentUser == null || currentUser.getUsername() == null) {
            throw new IllegalArgumentException("Current user details are required.");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("New password cannot be empty.");
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));

        if (!user.getPasswordHash().equals(oldPassword)) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        user.setPasswordHash(newPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserRole(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Role newRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + roleId));
        user.setRole(newRole);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO setUserEnabled(Integer userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        user.setEnabled(enabled);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void assignManagerToBranch(Integer branchId, Integer userId) {
        if (branchId == null || userId == null) {
            throw new IllegalArgumentException("Branch ID and User ID cannot be null for assignment.");
        }
        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found with ID: " + userId));
        if (!"MANAGER".equalsIgnoreCase(manager.getRole().getRoleName())) {
            throw new IllegalArgumentException("User with ID: " + userId + " is not a MANAGER.");
        }
        branchManagerService.assignManagerToBranch(branchId, userId);
    }

    @Override
    @Transactional
    public void removeManagerFromBranch(Integer branchId, Integer userId) {
        if (branchId == null || userId == null) {
            throw new IllegalArgumentException("Branch ID and User ID cannot be null for removal.");
        }
        branchManagerService.removeManagerFromBranch(branchId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        
        UserProfileDTO.UserProfileDTOBuilder profileBuilder = UserProfileDTO.builder()
                .userID(user.getUserID())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled());
        
        if (user.getRole() != null) {
            profileBuilder.roleName(user.getRole().getRoleName());
        }

        if ("CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {
            Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                    .orElse(null);
            if (customer != null) {
                profileBuilder.customerID(customer.getCustomerID())
                              .firstName(customer.getFirstName())
                              .lastName(customer.getLastName())
                              .licenseNumber(customer.getLicenseNumber())
                              .address(customer.getAddress());
                              
                // Calculate rental statistics
                List<com.rentacar.entity.Reservation> reservations = reservationRepository.findByCustomer_CustomerIDAndStatusIn(
                    customer.getCustomerID(), 
                    List.of("Pending", "Confirmed", "Rented", "Completed", "Tamamlandi", "Cancelled")
                );
                
                // Total rentals
                Long totalRentals = (long) reservations.size();
                
                // Completed rentals
                Long completedRentals = reservations.stream()
                    .filter(r -> "Completed".equalsIgnoreCase(r.getStatus()) || "Tamamlandi".equalsIgnoreCase(r.getStatus()))
                    .count();
                    
                // Total spent
                Double totalSpent = reservations.stream()
                    .filter(r -> r.getTotalPrice() != null)
                    .mapToDouble(com.rentacar.entity.Reservation::getTotalPrice)
                    .sum();
                    
                // Average rating (from completed reservations' feedbacks)
                List<Integer> reservationIds = reservations.stream()
                    .filter(r -> "Completed".equalsIgnoreCase(r.getStatus()) || "Tamamlandi".equalsIgnoreCase(r.getStatus()))
                    .map(com.rentacar.entity.Reservation::getReservationID)
                    .collect(java.util.stream.Collectors.toList());
                    
                // Get all feedbacks for this customer's completed reservations
                List<com.rentacar.entity.Feedback> allFeedbacks = feedbackRepository.findAll();
                List<com.rentacar.entity.Feedback> customerFeedbacks = allFeedbacks.stream()
                    .filter(f -> f.getReservation() != null && reservationIds.contains(f.getReservation().getReservationID()))
                    .collect(java.util.stream.Collectors.toList());
                    
                Double averageRating = customerFeedbacks.stream()
                    .filter(f -> f.getRating() != null)
                    .mapToInt(com.rentacar.entity.Feedback::getRating)
                    .average()
                    .orElse(0.0);
                    
                profileBuilder.totalRentals(totalRentals)
                             .completedRentals(completedRentals)
                             .totalSpent(totalSpent)
                             .averageRating(averageRating);
            }
        } else if ("MANAGER".equalsIgnoreCase(user.getRole().getRoleName())) {
            Manager manager = managerRepository.findByUser_UserID(user.getUserID())
                    .orElse(null);
            if (manager != null) {
                profileBuilder.firstName(manager.getFirstName())
                             .lastName(manager.getLastName());
            }
        }
        return profileBuilder.build();
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileDTO userProfileDTO, UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));

        if (StringUtils.hasText(userProfileDTO.getEmail())) user.setEmail(userProfileDTO.getEmail());
        if (StringUtils.hasText(userProfileDTO.getPhone())) user.setPhone(userProfileDTO.getPhone());
        
        userRepository.save(user);

        if ("CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {
            Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setUser(user);
                    return newCustomer;
                });
            
            if (StringUtils.hasText(userProfileDTO.getFirstName())) customer.setFirstName(userProfileDTO.getFirstName());
            if (StringUtils.hasText(userProfileDTO.getLastName())) customer.setLastName(userProfileDTO.getLastName());
            if (StringUtils.hasText(userProfileDTO.getLicenseNumber())) customer.setLicenseNumber(userProfileDTO.getLicenseNumber());
            if (StringUtils.hasText(userProfileDTO.getAddress())) customer.setAddress(userProfileDTO.getAddress());
            customerRepository.save(customer);
        } else if ("MANAGER".equalsIgnoreCase(user.getRole().getRoleName())) {
            Manager manager = managerRepository.findByUser_UserID(user.getUserID())
                .orElseGet(() -> {
                    Manager newManager = new Manager();
                    newManager.setUser(user);
                    return newManager;
                });
            
            if (StringUtils.hasText(userProfileDTO.getFirstName())) manager.setFirstName(userProfileDTO.getFirstName());
            if (StringUtils.hasText(userProfileDTO.getLastName())) manager.setLastName(userProfileDTO.getLastName());
            managerRepository.save(manager);
        }
        return getCurrentUserProfile(currentUser); 
    }

    @Override
    @Transactional
    public UserDTO createUserByAdmin(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPasswordHash(userDTO.getPassword()); // Plain text password
        user.setEnabled(userDTO.isEnabled());

        if (userDTO.getRole() == null || userDTO.getRole().getRoleName() == null) {
            throw new IllegalArgumentException("Role information must be provided for user creation.");
        }
        Role role = roleRepository.findByRoleName(userDTO.getRole().getRoleName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + userDTO.getRole().getRoleName()));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        
        // Create appropriate profile based on role
        String roleName = role.getRoleName().toUpperCase();
        switch (roleName) {
            case "MANAGER":
                createManagerProfile(savedUser);
                break;
            case "CUSTOMER":
                createCustomerProfile(savedUser);
                break;
            case "EMPLOYEE":
                createEmployeeProfile(savedUser);
                break;
            // ADMIN doesn't need a separate profile table
        }
        
        return userMapper.toDto(savedUser);
    }
    
    private void createManagerProfile(User user) {
        Manager manager = new Manager();
        manager.setUser(user);
        // Set default values or extract from user if needed
        manager.setFirstName(""); // Default empty, can be updated later
        manager.setLastName("");  // Default empty, can be updated later
        managerRepository.save(manager);
    }
    
    private void createCustomerProfile(User user) {
        Customer customer = new Customer();
        customer.setUser(user);
        // Set default values
        customer.setFirstName("");
        customer.setLastName("");
        customer.setLicenseNumber("");
        customer.setAddress("");
        customerRepository.save(customer);
    }
    
    private void createEmployeeProfile(User user) {
        Employee employee = new Employee();
        employee.setUser(user);
        // Set default values
        employee.setFirstName("");
        employee.setLastName("");
        employee.setPositionTitle("");
        employee.setSalary(0.0);
        employeeRepository.save(employee);
    }
} 