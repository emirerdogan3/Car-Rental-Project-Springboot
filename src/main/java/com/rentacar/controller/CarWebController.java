package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.mapper.CarMapper;
import com.rentacar.service.CarService;
import com.rentacar.service.CarBrandService;
import com.rentacar.service.CarBrandModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// @Controller - Temporarily disabled to resolve mapping conflict with CustomerCarController
public class CarWebController {
    private final CarService carService;
    private final CarMapper carMapper;
    private final CarBrandService carBrandService;
    private final CarBrandModelService carBrandModelService;

    @Autowired
    public CarWebController(CarService carService, CarMapper carMapper, CarBrandService carBrandService, CarBrandModelService carBrandModelService) {
        this.carService = carService;
        this.carMapper = carMapper;
        this.carBrandService = carBrandService;
        this.carBrandModelService = carBrandModelService;
    }

    @GetMapping("/cars")
    public String listCars(Model model) {
        // Şimdilik tüm branch'lerdeki araçları göstermek için örnek bir metot
        // Gelişmiş filtreleme için ileride branch, şehir, marka vs. eklenebilir
        // CarService.findCarsByFilters returns Page<CarDTO>
        List<CarDTO> carDTOs = carService.findCarsByFilters(new CarFilterDTO(), Pageable.unpaged()).getContent();
        model.addAttribute("cars", carDTOs);
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        model.addAttribute("models", carBrandModelService.getAllCarBrandModels());
        return "cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Integer id, Model model) {
        CarDTO carDTO = carService.getCarDtoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car Id:" + id));
        model.addAttribute("car", carDTO);
        return "car_detail";
    }
} 