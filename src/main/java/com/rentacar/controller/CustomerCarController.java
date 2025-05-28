package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/cars") // Genel araç işlemleri için baz URL
@RequiredArgsConstructor
public class CustomerCarController {

    private final CarService carService;
    private final BranchService branchService; // Şube adını almak için
    // Lookup Services for filter dropdowns
    private final CarBrandService carBrandService;
    private final CarBrandModelService carBrandModelService;
    private final CarCategoryService carCategoryService;

    private void populateCarFilterDropdowns(Model model) {
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        model.addAttribute("models", carBrandModelService.getAllCarBrandModelDTOs());
        model.addAttribute("categories", carCategoryService.getAllCarCategories());
    }

    @GetMapping
    public String listCarsForCustomer(@RequestParam(name = "branchId", required = true) Integer branchId,
                                      @RequestParam(name = "startDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.util.Date startDate,
                                      @RequestParam(name = "endDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.util.Date endDate,
                                      @ModelAttribute("filter") CarFilterDTO filter,
                                      Model model,
                                      @PageableDefault(size = 9) Pageable pageable) {
        
        Page<CarDTO> carPage;
        
        // If dates are provided, use the date-aware method
        if (startDate != null && endDate != null) {
            carPage = carService.findAvailableCarsByBranchAndFilters(branchId, filter, pageable, startDate, endDate);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } else {
            // If no dates provided, show all cars from the branch
            carPage = carService.findAvailableCarsByBranchAndFilters(branchId, filter, pageable);
        }
        
        String branchName = branchService.getBranchDtoById(branchId)
                                .map(BranchDTO::getBranchName)
                                .orElse("Selected Branch");

        model.addAttribute("carPage", carPage);
        model.addAttribute("cars", carPage.getContent()); // For template compatibility
        model.addAttribute("filter", filter);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branchName", branchName);
        populateCarFilterDropdowns(model); // Filtre dropdownları için verileri ekle

        return "customer/cars"; // templates/customer/cars.html
    }

    @GetMapping("/detail/{carId}")
    public String carDetail(@PathVariable("carId") Integer carId, Model model) {
        CarDTO carDTO = carService.getCarDtoById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId)); // Daha iyi bir exception handling
        model.addAttribute("car", carDTO);
        // Rezervasyon formu için araç ID'sini ve diğer bilgileri reservation_form.html'e taşıyabiliriz.
        return "customer/car_detail"; // templates/customer/car_detail.html
    }
    
    // AJAX için modellere göre marka getiren endpoint (Opsiyonel, eğer dropdown'lar dinamik olacaksa)
    @GetMapping("/api/models")
    @ResponseBody
    public List<com.rentacar.dto.CarBrandModelDTO> getModelsByBrand(@RequestParam("brandId") Integer brandId) {
        if (brandId == null) return java.util.Collections.emptyList();
        return carBrandModelService.getModelsByBrandId(brandId);
    }
} 