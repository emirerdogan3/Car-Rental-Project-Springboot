package com.rentacar.service.impl;

import com.rentacar.entity.Branch;
import com.rentacar.entity.BranchManager;
import com.rentacar.entity.BranchManagerId;
import com.rentacar.entity.User;
import com.rentacar.repository.BranchManagerRepository;
import com.rentacar.repository.BranchRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.service.BranchManagerService;
import com.rentacar.repository.ManagerRepository;
import com.rentacar.entity.Manager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchManagerServiceImpl implements BranchManagerService {

    private final BranchManagerRepository branchManagerRepository;
    private final BranchRepository branchRepository; // Branch varlığını kontrol için
    private final UserRepository userRepository; // User (manager) varlığını ve rolünü kontrol için
    private final ManagerRepository managerRepository; // Added for fetching Manager by User
    // private final RoleRepository roleRepository; // Manager rolünü kontrol için

    @Override
    @Transactional
    public void assignManagerToBranch(Integer branchId, Integer userId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + branchId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User (potential manager) not found with id: " + userId));

        // Fetch the Manager entity associated with the User
        Manager manager = managerRepository.findByUser_UserID(userId)
            .orElseThrow(() -> new EntityNotFoundException("Manager profile not found for user id: " + userId));

        // Assuming manager role check is handled by UserService or calling context
        // Or: if(!"MANAGER".equals(managerUser.getRole().getRoleName())) throw new ...

        // For @IdClass, JPA uses the @Id fields on the entity to form the composite key.
        // We should check if the combination already exists using the fields.
        // BranchManagerId checkId = new BranchManagerId(branchId, userId);
        // if (branchManagerRepository.existsById(checkId)) { // This still works and is fine

        // Let's check by individual fields to be more explicit with @IdClass on entity fields
        boolean alreadyAssigned = branchManagerRepository.findByBranch_BranchID(branchId).stream()
            .anyMatch(bm -> bm.getManager().getUser().getUserID().equals(userId));

        if (alreadyAssigned) {
            System.out.println("Manager (" + userId + ") is already assigned to branch (" + branchId + ").");
            return;
        }

        BranchManager branchManager = BranchManager.builder()
                // No .id(branchManagerId) for @IdClass when PK fields are directly on entity
                .branch(branch)
                .manager(manager)
                .build();
        branchManagerRepository.save(branchManager);
    }

    @Override
    @Transactional
    public void removeManagerFromBranch(Integer branchId, Integer userId) {
        // We need the ManagerID for the BranchManagerId, not the UserID directly.
        // Assuming the userId passed is actually the User's ID that acts as a manager.
        Manager manager = managerRepository.findByUser_UserID(userId)
            .orElseThrow(() -> new EntityNotFoundException("Manager profile not found for user id: " + userId + " when trying to remove from branch."));

        BranchManagerId branchManagerId = new BranchManagerId(branchId, manager.getManagerID());
        if (!branchManagerRepository.existsById(branchManagerId)) {
            throw new EntityNotFoundException("Manager with user id " + userId + " (ManagerID: " + manager.getManagerID() + ") is not assigned to branch " + branchId);
        }
        branchManagerRepository.deleteById(branchManagerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> findBranchesByManagerId(Integer managerId) {
        // managerId here is assumed to be UserID, so we query through Manager to User
        return branchManagerRepository.findByManager_User_UserID(managerId).stream()
                .map(BranchManager::getBranch)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findManagersByBranchId(Integer branchId) {
        return branchManagerRepository.findByBranch_BranchID(branchId).stream()
                .map(bm -> bm.getManager().getUser())
                .collect(Collectors.toList());
    }
} 