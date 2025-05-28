package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.mapper.CarMapper;
import com.rentacar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;
    private final CarMapper carMapper;

    @Autowired
    public CarController(CarService carService, CarMapper carMapper) {
        this.carService = carService;
        this.carMapper = carMapper;
    }

    @GetMapping("/branch/{branchId}")
    public List<CarDTO> getCarsByBranch(@PathVariable Integer branchId) {
        CarFilterDTO filter = new CarFilterDTO();
        return carService.findAvailableCarsByBranchAndFilters(branchId, filter, Pageable.unpaged()).getContent();
    }

    @PostMapping
    public CarDTO createCar(@RequestBody CarDTO carDTO) {
        return carService.createCar(carDTO);
    }

    @PutMapping("/{id}")
    public CarDTO updateCar(@PathVariable Integer id, @RequestBody CarDTO carDTO) {
        return carService.updateCar(id, carDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Integer id) {
        Optional<CarDTO> carDtoOpt = carService.getCarDtoById(id);
        return carDtoOpt.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
} 