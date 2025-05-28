package com.rentacar.controller;

import com.rentacar.entity.CarBrandModel;
import com.rentacar.service.CarBrandModelService;
import com.rentacar.service.CarBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/models")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModelController {
    private final CarBrandModelService carBrandModelService;
    private final CarBrandService carBrandService;

    @Autowired
    public AdminModelController(CarBrandModelService carBrandModelService, CarBrandService carBrandService) {
        this.carBrandModelService = carBrandModelService;
        this.carBrandService = carBrandService;
    }

    @GetMapping
    public String listModels(Model model) {
        var models = carBrandModelService.getAllCarBrandModels();
        model.addAttribute("models", models);
        return "admin/models";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("modelForm", new CarBrandModel());
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        return "admin/model_form";
    }

    @PostMapping("/new")
    public String createModel(@ModelAttribute("modelForm") CarBrandModel modelEntity) {
        carBrandModelService.createCarBrandModel(modelEntity);
        return "redirect:/admin/models";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        var modelEntity = carBrandModelService.getCarBrandModelById(id).orElse(null);
        model.addAttribute("modelForm", modelEntity);
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        return "admin/model_form";
    }

    @PostMapping("/edit/{id}")
    public String updateModel(@PathVariable Integer id, @ModelAttribute("modelForm") CarBrandModel modelEntity) {
        carBrandModelService.updateCarBrandModel(id, modelEntity);
        return "redirect:/admin/models";
    }

    @PostMapping("/delete/{id}")
    public String deleteModel(@PathVariable Integer id) {
        carBrandModelService.deleteCarBrandModel(id);
        return "redirect:/admin/models";
    }
} 