package com.rentacar.controller;

import com.rentacar.dto.ManagerDashboardStatsDTO;
import com.rentacar.dto.UserDTO; // To get current user from principal
import com.rentacar.entity.User; // ManagerDashboardService expects User entity
import com.rentacar.service.ManagerDashboardService;
import com.rentacar.service.UserService; // To fetch full User entity from username
import com.rentacar.mapper.UserMapper; // To convert UserDTO to User (if needed, but service takes User)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager") // Base path for manager section
@PreAuthorize("hasRole('MANAGER')")
public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;
    private final UserService userService; // To get the full User entity
    // No need for UserMapper here if userService.findEntityByUsername is created

    @Autowired
    public ManagerDashboardController(ManagerDashboardService managerDashboardService, UserService userService) {
        this.managerDashboardService = managerDashboardService;
        this.userService = userService;
    }

    @GetMapping({ "", "/", "/dashboard", "/index" }) // Common dashboard paths
    public String managerDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login?error=sessionExpired"; // Or handle appropriately
        }
        String username = userDetails.getUsername();
        // ManagerDashboardService expects a User entity. UserService usually returns UserDTO.
        // We need a way to get the User entity from username.
        User managerUserEntity = userService.findEntityByUsername(username)
            .orElseThrow(() -> new RuntimeException("Manager user not found: " + username));

        ManagerDashboardStatsDTO stats = managerDashboardService.getDashboardStats(managerUserEntity);
        
        model.addAttribute("stats", stats);
        model.addAttribute("managerUsername", username); // For display
        return "manager/index"; // Path to the Thymeleaf template
    }
} 