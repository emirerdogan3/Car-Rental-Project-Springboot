package com.rentacar.controller;

import com.rentacar.dto.CountryDTO;
import com.rentacar.entity.Country;
import com.rentacar.mapper.CountryMapper;
import com.rentacar.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/countries")
@PreAuthorize("hasRole('ADMIN')")
public class CountryController {
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Autowired
    public CountryController(CountryService countryService, CountryMapper countryMapper) {
        this.countryService = countryService;
        this.countryMapper = countryMapper;
    }

    @PostMapping
    public CountryDTO createCountry(@RequestBody CountryDTO countryDTO) {
        Country country = countryMapper.toEntity(countryDTO);
        return countryMapper.toDto(countryService.createCountry(country));
    }

    @PutMapping("/{id}")
    public CountryDTO updateCountry(@PathVariable Integer id, @RequestBody CountryDTO countryDTO) {
        Country country = countryMapper.toEntity(countryDTO);
        return countryMapper.toDto(countryService.updateCountry(id, country));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryDTO> getCountryById(@PathVariable Integer id) {
        Optional<Country> country = countryService.getCountryById(id);
        return country.map(value -> ResponseEntity.ok(countryMapper.toDto(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CountryDTO> getAllCountries() {
        return countryService.getAllCountries().stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }
} 