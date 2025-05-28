package com.rentacar.service;

import com.rentacar.dto.UserDTO;
import com.rentacar.dto.UserProfileDTO;
import com.rentacar.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Integer id);
    UserDTO createUserByAdmin(UserDTO userDTO);
    void deleteUser(Integer id);
    Optional<UserDTO> findDtoByUsername(String username);
    Optional<User> findEntityByUsername(String username);
    List<UserDTO> findDtosByRoleName(String roleName);
    boolean checkPassword(User user, String rawPassword);
    void changePassword(String oldPassword, String newPassword, UserDetails currentUser);
    UserDTO updateUserRole(Integer userId, Integer roleId);
    UserDTO setUserEnabled(Integer userId, boolean enabled);
    void assignManagerToBranch(Integer branchId, Integer userId);
    void removeManagerFromBranch(Integer branchId, Integer userId);
    UserProfileDTO getCurrentUserProfile(UserDetails currentUser);
    UserProfileDTO updateUserProfile(UserProfileDTO userProfileDTO, UserDetails currentUser);
} 