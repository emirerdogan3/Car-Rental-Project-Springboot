package com.rentacar.controller;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.User;
import com.rentacar.service.BranchManagerService;
import com.rentacar.service.ReservationService;
import com.rentacar.service.UserService;
import com.rentacar.mapper.ReservationMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/reservations")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final BranchManagerService branchManagerService;
    private final ReservationMapper reservationMapper;

    private List<Branch> getManagerBranches(UserDetails userDetails) {
        User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found: " + userDetails.getUsername()));
        return branchManagerService.findBranchesByManagerId(managerUser.getUserID());
    }

    @GetMapping
    public String listReservations(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute("filter") ReservationFilterDTO filter,
                                   Model model,
                                   @PageableDefault(size = 10) Pageable pageable,
                                   RedirectAttributes redirectAttributes) {

        List<Branch> managedBranches = getManagerBranches(userDetails);
        if (managedBranches.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to see reservations.");
            return "redirect:/manager/dashboard";
        }

        // Get ALL reservations from ALL managed branches
        List<ReservationDTO> allReservations = managedBranches.stream()
            .flatMap(branch -> {
                ReservationFilterDTO branchFilter = ReservationFilterDTO.builder()
                    .branchId(branch.getBranchID())
                    .status(filter != null ? filter.getStatus() : null)
                    .startDateFrom(filter != null ? filter.getStartDateFrom() : null)
                    .startDateTo(filter != null ? filter.getStartDateTo() : null)
                    .endDateFrom(filter != null ? filter.getEndDateFrom() : null)
                    .endDateTo(filter != null ? filter.getEndDateTo() : null)
                    .build();
                
                return reservationService.getReservationsByBranchAndFilters(
                    branch.getBranchID(), branchFilter, Pageable.unpaged()).getContent().stream();
            })
            .collect(Collectors.toList());

        // Manual pagination for combined results
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allReservations.size());
        List<ReservationDTO> paginatedReservations = allReservations.subList(start, end);

        // Create manual Page implementation
        Page<ReservationDTO> reservationPage = new org.springframework.data.domain.PageImpl<>(
            paginatedReservations, pageable, allReservations.size());

        // Create branch names string for display
        String branchNames = managedBranches.stream()
            .map(Branch::getBranchName)
            .collect(Collectors.joining(", "));

        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("branchName", branchNames + " (" + managedBranches.size() + " branches)");
        model.addAttribute("statuses", List.of("Confirmed", "Cancelled", "Completed"));
        return "manager/reservations";
    }

    @GetMapping("/{reservationId}")
    public String viewReservationDetails(@PathVariable Integer reservationId,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        List<Branch> managedBranches = getManagerBranches(userDetails);
        if (managedBranches.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch.");
            return "redirect:/manager";
        }
        
        List<Integer> managedBranchIds = managedBranches.stream()
            .map(Branch::getBranchID)
            .collect(Collectors.toList());
        
        try {
            // Get reservation and verify it belongs to one of manager's branches
            ReservationDTO reservation = reservationService.getAllReservations().stream()
                .filter(r -> r.getReservationID().equals(reservationId))
                .map(reservationMapper::toDto)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
            
            // Check if reservation belongs to any of manager's branches
            if (!managedBranchIds.contains(reservation.getBranchID())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only view reservations from your managed branches.");
                return "redirect:/manager/reservations";
            }
            
            model.addAttribute("reservation", reservation);
            return "manager/reservation_detail";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reservation not found.");
            return "redirect:/manager/reservations";
        }
    }
} 