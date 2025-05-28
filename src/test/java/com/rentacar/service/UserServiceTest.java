package com.rentacar.service;

import com.rentacar.dto.UserDTO;
import com.rentacar.entity.User;
import com.rentacar.mapper.UserMapper;
import com.rentacar.repository.UserRepository;
import com.rentacar.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_found() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testuser");

        UserDTO userDTO = new UserDTO();
        userDTO.setUserID(1);
        userDTO.setUsername("testuser");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserID());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserById(2);
        assertFalse(result.isPresent());
    }
} 