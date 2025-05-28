package com.rentacar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import com.rentacar.service.UserService;
import com.rentacar.service.ReservationService;
import com.rentacar.service.FeedbackService;
import com.rentacar.dto.UserDTO;
import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.mapper.ReservationMapper;
import com.rentacar.mapper.FeedbackMapper;
import com.rentacar.mapper.CountryMapper;
import com.rentacar.mapper.CityMapper;
import com.rentacar.mapper.CountyMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import com.rentacar.service.BranchService;
import com.rentacar.service.CountryService;
import com.rentacar.service.CityService;
import com.rentacar.service.CountyService;
import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.filter.BranchFilterDTO;
import com.rentacar.dto.CountryDTO;
import com.rentacar.dto.CityDTO;
import com.rentacar.dto.CountyDTO;
import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.service.CarService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.rentacar.dto.FeedbackDTO;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CustomerController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final FeedbackService feedbackService;
    private final ReservationMapper reservationMapper;
    private final FeedbackMapper feedbackMapper;
    private final BranchService branchService;
    private final CountryService countryService;
    private final CountryMapper countryMapper;
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final CountyService countyService;
    private final CountyMapper countyMapper;
    private final CarService carService;

    @Autowired
    public CustomerController(UserService userService, ReservationService reservationService, FeedbackService feedbackService, ReservationMapper reservationMapper, FeedbackMapper feedbackMapper, BranchService branchService, CountryService countryService, CountryMapper countryMapper, CityService cityService, CityMapper cityMapper, CountyService countyService, CountyMapper countyMapper, CarService carService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.feedbackService = feedbackService;
        this.reservationMapper = reservationMapper;
        this.feedbackMapper = feedbackMapper;
        this.branchService = branchService;
        this.countryService = countryService;
        this.countryMapper = countryMapper;
        this.cityService = cityService;
        this.cityMapper = cityMapper;
        this.countyService = countyService;
        this.countyMapper = countyMapper;
        this.carService = carService;
    }

    @GetMapping("/customer")
    public String customerDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        UserDTO userDto = userService.findDtoByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        ReservationFilterDTO filter = ReservationFilterDTO.builder().build();
        List<ReservationDTO> reservations = reservationService.getReservationsByCurrentUser(filter, Pageable.unpaged(), userDetails).getContent();

        long activeCount = reservations.stream().filter(r -> "Aktif".equalsIgnoreCase(r.getStatus()) || "Confirmed".equalsIgnoreCase(r.getStatus()) || "Rented".equalsIgnoreCase(r.getStatus())).count();
        long pendingPaymentCount = reservations.stream().filter(r -> "Beklemede".equalsIgnoreCase(r.getStatus()) || "Pending Approval".equalsIgnoreCase(r.getStatus()) || "Pending".equalsIgnoreCase(r.getStatus())).count();
        long completedCount = reservations.stream().filter(r -> "Tamamlandı".equalsIgnoreCase(r.getStatus()) || "Completed".equalsIgnoreCase(r.getStatus())).count();
        
        long feedbackCount = feedbackService.getAllFeedbacks().stream()
                             .map(feedbackMapper::toDto)
                             .filter(fDto -> fDto.getCustomerID() != null && fDto.getCustomerID().equals(userDto.getUserID()))
                             .count();

        model.addAttribute("activeCount", activeCount);
        model.addAttribute("pendingPaymentCount", pendingPaymentCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("feedbackCount", feedbackCount);
        return "customer/index";
    }

    // @GetMapping("/customer/branches")
    @GetMapping("/customer/branches-legacy")
    public String listBranchesLegacyRedirect() {
        return "redirect:/customer/branches";
    }

    @GetMapping("/customer/branches/{branchId}/cars")
    public String listCarsByBranch(@PathVariable Integer branchId, Model model) {
        List<CarDTO> cars = carService.findAvailableCarsByBranchAndFilters(branchId, new CarFilterDTO(), Pageable.unpaged()).getContent();
        model.addAttribute("cars", cars);
        model.addAttribute("branchId", branchId);
        return "customer/cars";
    }
} 