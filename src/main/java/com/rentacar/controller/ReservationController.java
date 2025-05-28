package com.rentacar.controller;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.request.ReservationRequestDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.entity.Reservation;
import com.rentacar.mapper.ReservationMapper;
import com.rentacar.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @PostMapping
    public ReservationDTO createReservation(@RequestBody ReservationRequestDTO requestDTO, @AuthenticationPrincipal UserDetails currentUser) {
        return reservationService.createReservationAndProcessPayment(requestDTO, currentUser);
    }

    @GetMapping("/branch/{branchId}")
    public List<ReservationDTO> getReservationsByBranch(@PathVariable Integer branchId, 
                                                        @ModelAttribute ReservationFilterDTO filter,
                                                        Pageable pageable) {
        return reservationService.getReservationsByBranchAndFilters(branchId, filter, pageable).getContent();
    }
} 