package com.rentacar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDTO {
    private long totalBranches;
    private long totalCars;
    private long activeCars;
    private long totalUsers;
    private long customerUsers;
    private long managerUsers;
    private long adminUsers;
    private long activeReservations;
} 