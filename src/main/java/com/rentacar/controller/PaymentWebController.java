package com.rentacar.controller;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.service.ReservationService;
import com.rentacar.mapper.ReservationMapper;
import com.rentacar.service.UserService;
import com.rentacar.dto.UserDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class PaymentWebController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final UserService userService;

    @Autowired
    public PaymentWebController(ReservationService reservationService, ReservationMapper reservationMapper, UserService userService) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
        this.userService = userService;
    }

    @GetMapping("/payment")
    public String showPaymentForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ReservationFilterDTO filter = ReservationFilterDTO.builder()
            .status("Beklemede")
            .build();
        List<ReservationDTO> reservations = reservationService.getReservationsByCurrentUser(filter, Pageable.unpaged(), userDetails).getContent();

        model.addAttribute("reservations", reservations);
        return "payment_form";
    }

    @PostMapping("/payment")
    public String processPayment(@RequestParam("reservationId") Integer reservationId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("paymentError", "This payment screen is currently under review. Please make payments via the reservation form.");
        return "payment_form";
    }
} 