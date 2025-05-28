package com.rentacar.service.impl;

import com.rentacar.dto.ManagerDashboardStatsDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.User;
import com.rentacar.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerDashboardServiceImpl implements ManagerDashboardService {

    private final BranchManagerService branchManagerService;
    private final CarService carService;
    private final ReservationService reservationService;
    private final FeedbackService feedbackService;
    // private final EmployeeService employeeService; // If adding employee stats

    @Override
    @Transactional(readOnly = true) // Good practice for read-only operations
    public ManagerDashboardStatsDTO getDashboardStats(User managerUser) {
        if (managerUser == null || managerUser.getUserID() == null) {
            // Or throw an exception, or return empty stats
            return ManagerDashboardStatsDTO.builder().build(); 
        }

        List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(managerUser.getUserID());

        if (managedBranches.isEmpty()) {
            return ManagerDashboardStatsDTO.builder().totalManagedBranches(0).build();
        }

        List<Integer> managedBranchIds = managedBranches.stream().map(Branch::getBranchID).collect(Collectors.toList());

        long totalCars = 0;
        long availableCars = 0;
        // Assuming CarService has methods to count by branch IDs
        // For simplicity, iterating here. Efficient services would take List<Integer> branchIds
        for (Integer branchId : managedBranchIds) {
            totalCars += carService.countCarsByBranch(branchId);         // Needs to be created in CarService
            availableCars += carService.countAvailableCarsByBranch(branchId); // Needs to be created in CarService
        }
        
        long activeReservations = 0;
        long pendingReservations = 0;
        // Assuming ReservationService can filter by branch IDs and status
        // This part might be complex depending on Reservation status definitions
        for (Integer branchId : managedBranchIds) {
            activeReservations += reservationService.countActiveReservationsByBranch(branchId);       // Needs to be created
            pendingReservations += reservationService.countPendingApprovalReservationsByBranch(branchId); // Needs to be created
        }

        long unreadFeedback = 0;
        // Assuming FeedbackService can filter by branch IDs and read status (if exists)
        // For now, let's count all feedback for managed branches.
        // A more specific "unread" would require a `isRead` flag on Feedback entity.
        for (Integer branchId : managedBranchIds) {
            unreadFeedback += feedbackService.countFeedbackByBranch(branchId); // Needs to be created
        }

        return ManagerDashboardStatsDTO.builder()
                .totalManagedBranches(managedBranches.size())
                .totalCarsInManagedBranches(totalCars)
                .availableCarsInManagedBranches(availableCars)
                .activeReservationsInManagedBranches(activeReservations)
                .pendingApprovalReservationsInManagedBranches(pendingReservations)
                .unreadFeedbackForManagedBranches(unreadFeedback)
                .build();
    }
} 