package com.rentacar.controller;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.service.ReservationService;
import com.rentacar.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/reservations")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerReservationHistoryController {

    private final ReservationService reservationService;
    private final BranchService branchService;

    @GetMapping
    public String listReservations(@ModelAttribute("filter") ReservationFilterDTO filter,
                                   Model model,
                                   @PageableDefault(size = 10, sort = "reservationID") Pageable pageable,
                                   @AuthenticationPrincipal UserDetails currentUser) {
        Page<ReservationDTO> reservationPage = reservationService.getReservationsByCurrentUser(filter, pageable, currentUser);
        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("filter", filter); // To keep filter values in the form
        model.addAttribute("branches", branchService.getAllBranches());
        return "customer/reservations"; // Thymeleaf template path
    }

    @PostMapping("/{reservationId}/cancel")
    public String cancelReservation(@PathVariable("reservationId") Integer reservationId,
                                    @AuthenticationPrincipal UserDetails currentUser,
                                    RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(reservationId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully.");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not cancel reservation: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while cancelling the reservation.");
            // Log error e.printStackTrace();
        }
        return "redirect:/customer/reservations";
    }
} 