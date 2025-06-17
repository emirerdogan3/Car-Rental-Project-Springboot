package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.mapper.CarMapper;
import com.rentacar.service.CarService;
import com.rentacar.service.CarBrandService;
import com.rentacar.service.CarBrandModelService;
import com.rentacar.service.CarCategoryService;
import com.rentacar.service.ColorService;
import com.rentacar.service.FuelTypeService;
import com.rentacar.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.rentacar.exception.DuplicateResourceException;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rentacar.dto.CarBrandModelDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody; // Bu önemli!

import java.util.Collections;
import java.util.List;

import com.rentacar.exception.ResourceInUseException;


@Controller
@RequestMapping("/admin/cars")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarController {
    private final CarService carService;
    private final CarMapper carMapper;
    private final CarBrandService carBrandService;
    private final CarBrandModelService carBrandModelService;
    private final CarCategoryService carCategoryService;
    private final ColorService colorService;
    private final FuelTypeService fuelTypeService;
    private final BranchService branchService;

    @Autowired
    public AdminCarController(CarService carService, CarMapper carMapper, CarBrandService carBrandService, 
                             CarBrandModelService carBrandModelService, CarCategoryService carCategoryService,
                             ColorService colorService, FuelTypeService fuelTypeService, BranchService branchService) {
        this.carService = carService;
        this.carMapper = carMapper;
        this.carBrandService = carBrandService;
        this.carBrandModelService = carBrandModelService;
        this.carCategoryService = carCategoryService;
        this.colorService = colorService;
        this.fuelTypeService = fuelTypeService;
        this.branchService = branchService;
    }




    @GetMapping
    public String listCars(Model model) {
        var cars = carService.findCarsByFilters(new CarFilterDTO(), Pageable.unpaged()).getContent();
        model.addAttribute("cars", cars);
        return "admin/cars";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("carForm", new CarDTO());
        //populateCarFormDropdowns(model);
        populateCarFormModel(model); // Helper metodu çağır
        return "admin/car_form";
    }

    // Kod tekrarını önlemek için form için gerekli verileri yükleyen bir helper metot oluşturalım.
    private void populateCarFormModel(Model model) {
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        model.addAttribute("models", carBrandModelService.getAllCarBrandModelDTOs());
        model.addAttribute("categories", carCategoryService.getAllCarCategories());
        model.addAttribute("fuelTypes", fuelTypeService.getAllFuelTypes());
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("branches", branchService.getAllBranches());
    }

    /*
    @PostMapping("/new")
    public String createCar(@ModelAttribute("carForm") CarDTO carDTO) {
        carService.createCar(carDTO);
        return "redirect:/admin/cars";
    }*/

    @PostMapping("/new")
    public String createCar(@ModelAttribute("carForm") CarDTO carDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            carService.createCar(carDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Car added successfully.");
            return "redirect:/admin/cars";
        } catch (DuplicateResourceException e) {
            // Hata yakalanırsa, kullanıcıyı forma geri gönder
            model.addAttribute("errorMessage", e.getMessage());
            populateCarFormModel(model); // Formun dropdown'larını tekrar doldur
            return "admin/car_form"; // Yönlendirme DEĞİL, formu tekrar render et
        }
    }

    private void populateCarFormDropdowns(Model model) {
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        model.addAttribute("models", carBrandModelService.getAllCarBrandModels());
        model.addAttribute("categories", carCategoryService.getAllCarCategories());
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("fuelTypes", fuelTypeService.getAllFuelTypes());
        model.addAttribute("branches", branchService.getAllBranches());
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CarDTO carDto = carService.getCarDtoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car Id:" + id));
        model.addAttribute("carForm", carDto);
        //populateCarFormDropdowns(model);
        populateCarFormModel(model); // Helper metodu çağır
        return "admin/car_form";
    }

    /*
    @PostMapping("/edit/{id}")
    public String updateCar(@PathVariable Integer id, @ModelAttribute("carForm") CarDTO carDTO) {
        carService.updateCar(id, carDTO);
        return "redirect:/admin/cars";
    }*/

    @PostMapping("/edit/{id}")
    public String updateCar(@PathVariable Integer id, @ModelAttribute("carForm") CarDTO carDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            carService.updateCar(id, carDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Car updated successfully.");
            return "redirect:/admin/cars";
        } catch (DuplicateResourceException e) {
            // Hata yakalanırsa, kullanıcıyı forma geri gönder
            model.addAttribute("errorMessage", e.getMessage());
            populateCarFormModel(model); // Formun dropdown'larını tekrar doldur
            return "admin/car_form"; // Yönlendirme DEĞİL, formu tekrar render et
        }
    }



    /*
    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return "redirect:/admin/cars";
    }*/
    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            carService.deleteCar(id);
            // Başarılı olursa gösterilecek mesaj
            redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully.");
        } catch (ResourceInUseException e) {
            // Bizim fırlattığımız özel hata yakalanırsa
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Beklenmedik başka bir hata olursa
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while trying to delete the car.");
        }
        return "redirect:/admin/cars";
    }
} 