package com.rentacar.service;

import com.rentacar.entity.Branch;
import com.rentacar.entity.User;
import java.util.List;

// Gerekirse DTO importları eklenecek

public interface BranchManagerService {
    void assignManagerToBranch(Integer branchId, Integer userId); // Manager (User) ID'si
    void removeManagerFromBranch(Integer branchId, Integer userId); // Manager (User) ID'si

    List<Branch> findBranchesByManagerId(Integer managerId);
    List<User> findManagersByBranchId(Integer branchId);

    // İleride eklenebilecek diğer metotlar:
    // List<UserDTO> getManagersByBranch(Integer branchId);
    // List<BranchDTO> getBranchesByManager(Integer userId);
    // boolean isManagerAssignedToBranch(Integer branchId, Integer userId);
} 