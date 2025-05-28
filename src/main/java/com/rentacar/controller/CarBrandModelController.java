package com.rentacar.controller;

import com.rentacar.entity.CarBrandModel;
import com.rentacar.service.CarBrandModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/models")
@PreAuthorize("hasRole('ADMIN')")
public class CarBrandModelController {
    private final CarBrandModelService carBrandModelService;

    @Autowired
    public CarBrandModelController(CarBrandModelService carBrandModelService) {
        this.carBrandModelService = carBrandModelService;
    }

    @PostMapping
    public ResponseEntity<CarBrandModel> createCarBrandModel(@RequestBody CarBrandModel carBrandModel) {
        return ResponseEntity.ok(carBrandModelService.createCarBrandModel(carBrandModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarBrandModel> updateCarBrandModel(@PathVariable Integer id, @RequestBody CarBrandModel carBrandModel) {
        return ResponseEntity.ok(carBrandModelService.updateCarBrandModel(id, carBrandModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarBrandModel(@PathVariable Integer id) {
        carBrandModelService.deleteCarBrandModel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarBrandModel> getCarBrandModelById(@PathVariable Integer id) {
        return carBrandModelService.getCarBrandModelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CarBrandModel>> getAllCarBrandModels() {
        return ResponseEntity.ok(carBrandModelService.getAllCarBrandModels());
    }
} 