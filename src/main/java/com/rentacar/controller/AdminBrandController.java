package com.rentacar.controller;

import com.rentacar.dto.CarBrandDTO;
import com.rentacar.service.CarBrandService;
import com.rentacar.service.CarBrandModelService;
import com.rentacar.mapper.CarBrandMapper;
import com.rentacar.entity.CarBrand;
import com.rentacar.entity.CarBrandModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController { // Sınıf adı AdminBrandController olarak değiştirildi

    private final CarBrandService carBrandService;
    private final CarBrandMapper carBrandMapper;
    private final CarBrandModelService carBrandModelService;

    @Autowired
    public AdminBrandController(CarBrandService carBrandService, CarBrandMapper carBrandMapper, CarBrandModelService carBrandModelService) {
        this.carBrandService = carBrandService;
        this.carBrandMapper = carBrandMapper;
        this.carBrandModelService = carBrandModelService;
    }

    @GetMapping
    public String listBrands(Model model) {
        // CarBrandService'in getAllCarBrandDTOs() gibi bir metodu varsa o kullanılmalı.
        // Şimdilik DTO'ya çevirme işlemini bırakıyorum, CarBrandService güncellenmiş olabilir.
        List<CarBrandDTO> brands = carBrandService.getAllCarBrands().stream()
                                      .map(carBrandMapper::toDto)
                                      .collect(Collectors.toList());
        model.addAttribute("brands", brands);
        return "admin/brands";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("brandForm", new CarBrandDTO());
        return "admin/brand_form";
    }

    @PostMapping("/new")
    public String createBrand(@ModelAttribute("brandForm") CarBrandDTO brandDTO) {
        // CarBrandService'in DTO alan bir create metodu varsa o kullanılmalı.
        carBrandService.createCarBrand(carBrandMapper.toEntity(brandDTO));
        return "redirect:/admin/brands";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        // CarBrandService'in DTO dönen bir get metodu varsa o kullanılmalı.
        CarBrandDTO brandDTO = carBrandService.getCarBrandById(id)
                                .map(carBrandMapper::toDto)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brandForm", brandDTO);
        return "admin/brand_form";
    }

    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable Integer id, @ModelAttribute("brandForm") CarBrandDTO brandDTO) {
        // CarBrandService'in DTO alan bir update metodu varsa o kullanılmalı.
        CarBrand carBrandEntity = carBrandMapper.toEntity(brandDTO);
        // carBrandEntity.setBrandID(id); // Mapper bunu zaten yapıyor olabilir veya ID DTO'dan gelmeli.
        carBrandService.updateCarBrand(id, carBrandEntity);
        return "redirect:/admin/brands";
    }

    @PostMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Integer id) {
        try {
            carBrandService.deleteCarBrand(id);
        } catch (Exception e) {
            return "redirect:/admin/brands?error=deleteFailed";
        }
        return "redirect:/admin/brands";
    }

    @GetMapping("/{brandId}/models")
    public String viewBrandModels(@PathVariable Integer brandId, Model model) {
        // Get brand info for context
        CarBrandDTO brandDTO = carBrandService.getCarBrandById(brandId)
                .map(carBrandMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + brandId));
        
        // Get models for this brand
        List<CarBrandModel> models = carBrandModelService.getAllCarBrandModels().stream()
                .filter(m -> m.getBrand() != null && m.getBrand().getBrandID().equals(brandId))
                .collect(Collectors.toList());
        
        model.addAttribute("models", models);
        model.addAttribute("brandContext", brandDTO);
        return "admin/models";
    }
} 