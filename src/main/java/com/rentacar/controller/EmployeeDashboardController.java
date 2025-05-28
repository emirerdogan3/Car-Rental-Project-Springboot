package com.rentacar.controller;

import com.rentacar.dto.UserProfileDTO;
import com.rentacar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeDashboardController {

    private final UserService userService;

    @GetMapping
    public String employeeDashboard(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserProfileDTO userProfile = userService.getCurrentUserProfile(currentUser);
            model.addAttribute("userProfile", userProfile);
            return "employee/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Dashboard could not be loaded: " + e.getMessage());
            return "employee/dashboard";
        }
    }

    @GetMapping("/profile")
    public String employeeProfile(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserProfileDTO userProfile = userService.getCurrentUserProfile(currentUser);
            model.addAttribute("userProfile", userProfile);
            return "employee/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Profile information could not be loaded: " + e.getMessage());
            return "employee/profile";
        }
    }
} 