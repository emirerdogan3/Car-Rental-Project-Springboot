package com.rentacar.service;

import com.rentacar.dto.ManagerDashboardStatsDTO;
import com.rentacar.entity.User; // Or just userId

public interface ManagerDashboardService {
    ManagerDashboardStatsDTO getDashboardStats(User managerUser);
    // Alternative: ManagerDashboardStatsDTO getDashboardStats(Integer managerUserId);
} 