package com.rentacar.controller;

import com.rentacar.entity.County;
import com.rentacar.service.CountyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/counties")
@PreAuthorize("hasRole('ADMIN')")
public class CountyController {
    private final CountyService countyService;

    @Autowired
    public CountyController(CountyService countyService) {
        this.countyService = countyService;
    }

    @PostMapping
    public ResponseEntity<County> createCounty(@RequestBody County county) {
        return ResponseEntity.ok(countyService.createCounty(county));
    }

    @PutMapping("/{id}")
    public ResponseEntity<County> updateCounty(@PathVariable Integer id, @RequestBody County county) {
        return ResponseEntity.ok(countyService.updateCounty(id, county));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCounty(@PathVariable Integer id) {
        countyService.deleteCounty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<County> getCountyById(@PathVariable Integer id) {
        return countyService.getCountyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<County>> getAllCounties() {
        return ResponseEntity.ok(countyService.getAllCounties());
    }
} 