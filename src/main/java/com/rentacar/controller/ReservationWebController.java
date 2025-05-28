package com.rentacar.controller;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.dto.request.ReservationRequestDTO;
import com.rentacar.mapper.ReservationMapper;
import com.rentacar.service.ReservationService;
import com.rentacar.service.CarService;
import com.rentacar.service.BranchService;
import com.rentacar.dto.BranchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import com.rentacar.service.UserService;
import com.rentacar.dto.UserDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class ReservationWebController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final CarService carService;
    private final BranchService branchService;
    private final UserService userService;

    @Autowired
    public ReservationWebController(ReservationService reservationService, ReservationMapper reservationMapper, CarService carService, BranchService branchService, UserService userService) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
        this.carService = carService;
        this.branchService = branchService;
        this.userService = userService;
    }

    @GetMapping("/reservation/new")
    public String showReservationForm(@RequestParam(value = "carId", required = false) Integer carId, Model model) {
        if (carId == null) {
            return "redirect:/customer/branches"; // If no car selected, redirect to branch selection
        }
        
        // Get the selected car details
        CarDTO selectedCar = carService.getCarDtoById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));
        
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setCarID(carId);
        
        List<CarDTO> cars = carService.findCarsByFilters(new CarFilterDTO(), Pageable.unpaged()).getContent();
        List<BranchDTO> branches = branchService.getAllBranches();
        
        // Create ReservationRequestDTO for form binding
        ReservationRequestDTO reservationRequest = new ReservationRequestDTO();
        reservationRequest.setCarId(carId);
        reservationRequest.setBranchId(selectedCar.getBranchID());
        
        model.addAttribute("car", selectedCar); // Add selected car details
        model.addAttribute("cars", cars);
        model.addAttribute("branches", branches);
        model.addAttribute("reservationDTO", reservationDTO);
        model.addAttribute("reservationRequest", reservationRequest); // For form binding
        return "customer/reservation_form";
    }

    @PostMapping("/reservation/new")
    public String createReservation(@ModelAttribute ReservationDTO reservationDTO, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("errorMessage", "Reservation creation via this page is currently disabled. Please use the new reservation form.");
        List<CarDTO> cars = carService.findCarsByFilters(new CarFilterDTO(), Pageable.unpaged()).getContent();
        List<BranchDTO> branches = branchService.getAllBranches();
        model.addAttribute("cars", cars);
        model.addAttribute("branches", branches);
        model.addAttribute("reservationDTO", reservationDTO);
        return "customer/reservation_form";
    }

    @GetMapping("/reservations")
    public String listUserReservations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ReservationFilterDTO filter = ReservationFilterDTO.builder().build();
        List<ReservationDTO> reservations = reservationService.getReservationsByCurrentUser(filter, Pageable.unpaged(), userDetails).getContent();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @GetMapping("/reservations/{id}")
    public String reservationDetails(@PathVariable Integer id, Model model) {
        ReservationDTO reservation = reservationService.getAllReservations().stream()
            .filter(r -> r.getReservationID().equals(id))
            .map(reservationMapper::toDto)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + id));
        model.addAttribute("reservation", reservation);
        return "reservation_detail";
    }
} 