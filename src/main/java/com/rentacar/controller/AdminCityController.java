package com.rentacar.controller;

import com.rentacar.dto.CityDTO;
import com.rentacar.dto.CountryDTO;
import com.rentacar.entity.City;
import com.rentacar.mapper.CityMapper;
import com.rentacar.service.CityService;
import com.rentacar.service.CountryService;
import com.rentacar.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/cities")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCityController {
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Autowired
    public AdminCityController(CityService cityService, CityMapper cityMapper,
                               CountryService countryService, CountryMapper countryMapper) {
        this.cityService = cityService;
        this.cityMapper = cityMapper;
        this.countryService = countryService;
        this.countryMapper = countryMapper;
    }

    @GetMapping
    public String listCities(Model model) {
        List<CityDTO> cities = cityService.getAllCities().stream()
                                .map(cityMapper::toDto) // Assuming service returns entities
                                .collect(Collectors.toList());
        model.addAttribute("cities", cities);
        return "admin/cities";
    }

    private void addCountriesToModel(Model model) {
        List<CountryDTO> countries = countryService.getAllCountries().stream()
                                        .map(countryMapper::toDto) // Assuming service returns entities
                                        .collect(Collectors.toList());
        model.addAttribute("countries", countries);
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("cityForm", new CityDTO());
        addCountriesToModel(model);
        return "admin/city_form";
    }

    @PostMapping("/new")
    public String createCity(@ModelAttribute("cityForm") CityDTO cityDTO) {
        // The CityDTO should have countryID set from the form
        cityService.createCity(cityMapper.toEntity(cityDTO)); // Assuming service expects entity
        return "redirect:/admin/cities";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CityDTO cityDTO = cityService.getCityById(id)
                                .map(cityMapper::toDto) // Assuming service returns entity
                                .orElseThrow(() -> new IllegalArgumentException("Invalid city Id:" + id));
        model.addAttribute("cityForm", cityDTO);
        addCountriesToModel(model);
        return "admin/city_form";
    }

    @PostMapping("/edit/{id}")
    public String updateCity(@PathVariable Integer id, @ModelAttribute("cityForm") CityDTO cityDTO) {
        cityService.updateCity(id, cityMapper.toEntity(cityDTO)); // Assuming service expects entity
        return "redirect:/admin/cities";
    }

    @PostMapping("/delete/{id}")
    public String deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return "redirect:/admin/cities";
    }
} 