package com.rentacar.controller;

import com.rentacar.dto.CountyDTO;
import com.rentacar.dto.CityDTO;
import com.rentacar.service.CountyService;
import com.rentacar.service.CityService;
import com.rentacar.mapper.CountyMapper;
import com.rentacar.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/counties")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCountyController {

    private final CountyService countyService;
    private final CountyMapper countyMapper;
    private final CityService cityService;
    private final CityMapper cityMapper;

    @Autowired
    public AdminCountyController(CountyService countyService, CountyMapper countyMapper,
                                 CityService cityService, CityMapper cityMapper) {
        this.countyService = countyService;
        this.countyMapper = countyMapper;
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    @GetMapping
    public String listCounties(Model model) {
        List<CountyDTO> counties = countyService.getAllCounties().stream()
                                    .map(countyMapper::toDto) // Assuming service returns entities
                                    .collect(Collectors.toList());
        model.addAttribute("counties", counties);
        return "admin/counties";
    }

    private void addCitiesToModel(Model model) {
        List<CityDTO> cities = cityService.getAllCities().stream()
                                    .map(cityMapper::toDto) // Assuming service returns entities
                                    .collect(Collectors.toList());
        model.addAttribute("cities", cities);
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("countyForm", new CountyDTO());
        addCitiesToModel(model);
        return "admin/county_form";
    }

    @PostMapping("/new")
    public String createCounty(@ModelAttribute("countyForm") CountyDTO countyDTO) {
        countyService.createCounty(countyMapper.toEntity(countyDTO)); // Assuming service expects entity
        return "redirect:/admin/counties";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CountyDTO countyDTO = countyService.getCountyById(id)
                                .map(countyMapper::toDto) // Assuming service returns entity
                                .orElseThrow(() -> new IllegalArgumentException("Invalid county Id:" + id));
        model.addAttribute("countyForm", countyDTO);
        addCitiesToModel(model);
        return "admin/county_form";
    }

    @PostMapping("/edit/{id}")
    public String updateCounty(@PathVariable Integer id, @ModelAttribute("countyForm") CountyDTO countyDTO) {
        countyService.updateCounty(id, countyMapper.toEntity(countyDTO)); // Assuming service expects entity
        return "redirect:/admin/counties";
    }

    @PostMapping("/delete/{id}")
    public String deleteCounty(@PathVariable Integer id) {
        countyService.deleteCounty(id);
        return "redirect:/admin/counties";
    }
} 