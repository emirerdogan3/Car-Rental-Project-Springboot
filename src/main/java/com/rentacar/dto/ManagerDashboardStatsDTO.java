package com.rentacar.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDashboardStatsDTO {
    private int totalManagedBranches;
    private long totalCarsInManagedBranches;
    private long availableCarsInManagedBranches;
    private long activeReservationsInManagedBranches; // Confirmed and ongoing
    private long pendingApprovalReservationsInManagedBranches; // Pending manager or customer action (e.g. payment)
    private long unreadFeedbackForManagedBranches;
    // Could also add: totalEmployeesInManagedBranches, totalRevenueLastMonthEtc.
} 