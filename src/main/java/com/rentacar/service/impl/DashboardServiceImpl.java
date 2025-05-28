package com.rentacar.service.impl;

import com.rentacar.dto.AdminDashboardStatsDTO;
import com.rentacar.repository.BranchRepository;
import com.rentacar.repository.CarRepository;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    // Define constants for status/role names for clarity and maintainability
    private static final String CAR_STATUS_AVAILABLE = "Available";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final List<String> ACTIVE_RESERVATION_STATUSES = Arrays.asList("Pending", "Confirmed", "Rented");

    @Override
    public AdminDashboardStatsDTO getAdminDashboardStats() {
        long totalBranches = branchRepository.count();
        long totalCars = carRepository.count();
        long activeCars = carRepository.countByStatus(CAR_STATUS_AVAILABLE);

        long totalUsers = userRepository.count();
        long customerUsers = userRepository.countByRole_RoleName(ROLE_CUSTOMER);
        long managerUsers = userRepository.countByRole_RoleName(ROLE_MANAGER);
        long adminUsers = userRepository.countByRole_RoleName(ROLE_ADMIN);

        long activeReservations = reservationRepository.countByStatusIn(ACTIVE_RESERVATION_STATUSES);

        return AdminDashboardStatsDTO.builder()
                .totalBranches(totalBranches)
                .totalCars(totalCars)
                .activeCars(activeCars)
                .totalUsers(totalUsers)
                .customerUsers(customerUsers)
                .managerUsers(managerUsers)
                .adminUsers(adminUsers)
                .activeReservations(activeReservations)
                .build();
    }
} 