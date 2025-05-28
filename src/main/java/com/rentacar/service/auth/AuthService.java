package com.rentacar.service.auth;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.dto.UserDTO;

public interface AuthService {
    UserDTO register(UserDTO userDTO, CustomerDTO customerDTO);
    String login(String username, String password);
} 