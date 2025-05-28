package com.rentacar.controller;

import com.rentacar.entity.CarBrand;
import com.rentacar.service.CarBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class CarBrandController {
    private final CarBrandService carBrandService;

    @Autowired
    public CarBrandController(CarBrandService carBrandService) {
        this.carBrandService = carBrandService;
    }

    @PostMapping
    public ResponseEntity<CarBrand> createCarBrand(@RequestBody CarBrand carBrand) {
        return ResponseEntity.ok(carBrandService.createCarBrand(carBrand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarBrand> updateCarBrand(@PathVariable Integer id, @RequestBody CarBrand carBrand) {
        return ResponseEntity.ok(carBrandService.updateCarBrand(id, carBrand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarBrand(@PathVariable Integer id) {
        carBrandService.deleteCarBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarBrand> getCarBrandById(@PathVariable Integer id) {
        return carBrandService.getCarBrandById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CarBrand>> getAllCarBrands() {
        return ResponseEntity.ok(carBrandService.getAllCarBrands());
    }
} 