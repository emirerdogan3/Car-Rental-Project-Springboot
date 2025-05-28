package com.rentacar.controller;

import com.rentacar.dto.CountryDTO;
import com.rentacar.mapper.CountryMapper;
import com.rentacar.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/countries")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCountryController {
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Autowired
    public AdminCountryController(CountryService countryService, CountryMapper countryMapper) {
        this.countryService = countryService;
        this.countryMapper = countryMapper;
    }

    @GetMapping
    public String listCountries(Model model) {
        var countries = countryService.getAllCountries();
        model.addAttribute("countries", countries);
        return "admin/countries";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("countryForm", new CountryDTO());
        return "admin/country_form";
    }

    @PostMapping("/new")
    public String createCountry(@ModelAttribute("countryForm") CountryDTO countryDTO) {
        countryService.createCountry(countryMapper.toEntity(countryDTO));
        return "redirect:/admin/countries";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        var country = countryService.getCountryById(id).orElse(null);
        model.addAttribute("countryForm", countryMapper.toDto(country));
        return "admin/country_form";
    }

    @PostMapping("/edit/{id}")
    public String updateCountry(@PathVariable Integer id, @ModelAttribute("countryForm") CountryDTO countryDTO) {
        countryService.updateCountry(id, countryMapper.toEntity(countryDTO));
        return "redirect:/admin/countries";
    }

    @PostMapping("/delete/{id}")
    public String deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
        return "redirect:/admin/countries";
    }
} 