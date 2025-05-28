package com.rentacar.controller;

import com.rentacar.dto.AdminDashboardStatsDTO;
import com.rentacar.service.DashboardService;
import com.rentacar.service.ReservationSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.rentacar.service.UserService;
import com.rentacar.dto.UserProfileDTO;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final ReservationSchedulerService reservationSchedulerService;
    private final UserService userService;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        AdminDashboardStatsDTO stats = dashboardService.getAdminDashboardStats();
        model.addAttribute("dashboardStats", stats);
        // İleride admin özet bilgileri eklenebilir
        return "admin/index";
    }
    
    @GetMapping("/admin/profile")
    public String adminProfile(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserProfileDTO userProfile = userService.getCurrentUserProfile(currentUser);
            model.addAttribute("userProfile", userProfile);
            return "admin/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "User information could not be loaded: " + e.getMessage());
            return "admin/profile";
        }
    }
    
    /**
     * Manuel rezervasyon durumu güncelleme - Test için
     */
    @PostMapping("/admin/update-reservations")
    public String manualUpdateReservations(RedirectAttributes redirectAttributes) {
        try {
            reservationSchedulerService.updateExpiredReservations();
            redirectAttributes.addFlashAttribute("successMessage", 
                "Reservation status update completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating reservations: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 