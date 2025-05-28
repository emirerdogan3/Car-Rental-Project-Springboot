package com.rentacar.service.auth;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.dto.UserDTO;
import com.rentacar.entity.Role;
import com.rentacar.entity.User;
import com.rentacar.mapper.UserMapper;
import com.rentacar.repository.RoleRepository;
import com.rentacar.security.JwtUtil;
import com.rentacar.service.CustomerService;
import com.rentacar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final CustomerService customerService;

    @Override
    @Transactional
    public UserDTO register(UserDTO userDTO, CustomerDTO customerDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        User user = userMapper.toEntity(userDTO);
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new EntityNotFoundException("CUSTOMER role not found. Registration cannot proceed."));
        user.setRole(customerRole);

        User savedUser = userRepository.save(user);

        customerService.createCustomer(customerDTO, savedUser);

        return userMapper.toDto(savedUser);
    }

    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (user.getPasswordHash() == null || !user.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid password for user: " + username);
        }

        Role role = user.getRole();
        if (role == null) {
            throw new IllegalStateException("User has no role assigned: " + username);
        }
        return jwtUtil.generateToken(user.getUsername(), role.getRoleName());
    }
} 