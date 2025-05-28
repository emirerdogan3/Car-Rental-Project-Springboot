package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.User;
import com.rentacar.service.*;
import com.rentacar.mapper.CarMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/cars")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerCarController {

    private final CarService carService;
    private final UserService userService;
    private final BranchManagerService branchManagerService;
    // Lookup Services for forms
    private final CarBrandService carBrandService;
    private final CarBrandModelService carBrandModelService;
    private final CarCategoryService carCategoryService;
    private final FuelTypeService fuelTypeService;
    private final ColorService colorService;
    // CarMapper might not be directly needed if service methods handle DTOs, but good to have if manual mapping is required.
    // private final CarMapper carMapper; 

    private List<Branch> getManagerBranches(UserDetails userDetails) {
        User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found: " + userDetails.getUsername()));
        return branchManagerService.findBranchesByManagerId(managerUser.getUserID());
    }

    private Optional<Branch> getManagerPrimaryBranch(UserDetails userDetails) {
        List<Branch> managedBranches = getManagerBranches(userDetails);
        return managedBranches.stream().findFirst();
    }

    private void populateCarFormDropdowns(Model model) {
        model.addAttribute("brands", carBrandService.getAllCarBrands());
        // For models, it might be better to load them via AJAX based on brand selection
        // For now, loading all. Or, if CarBrandModelDTO includes brandName, it can be filtered client-side.
        model.addAttribute("models", carBrandModelService.getAllCarBrandModels()); 
        model.addAttribute("categories", carCategoryService.getAllCarCategories());
        model.addAttribute("fuelTypes", fuelTypeService.getAllFuelTypes());
        model.addAttribute("colors", colorService.getAllColors());
        // Car statuses (Available, Rented, Maintenance) can be a hardcoded list or enum
        model.addAttribute("statuses", List.of("Available", "Rented", "Maintenance"));
    }

    @GetMapping
    public String listCars(@AuthenticationPrincipal UserDetails userDetails, Model model,
                           @PageableDefault(size = 10) Pageable pageable, RedirectAttributes redirectAttributes) {
        List<Branch> managedBranches = getManagerBranches(userDetails);
        if (managedBranches.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to see cars.");
            return "redirect:/manager/dashboard";
        }

        // Get ALL cars from ALL managed branches
        List<CarDTO> allCars = managedBranches.stream()
            .flatMap(branch -> {
                CarFilterDTO filter = CarFilterDTO.builder().build();
                return carService.findCarsByBranchAndFilters(branch.getBranchID(), filter, Pageable.unpaged()).getContent().stream();
            })
            .collect(java.util.stream.Collectors.toList());

        // Manual pagination for combined results
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCars.size());
        List<CarDTO> paginatedCars = allCars.subList(start, end);

        // Create manual Page implementation
        Page<CarDTO> carPage = new org.springframework.data.domain.PageImpl<>(
            paginatedCars, pageable, allCars.size());

        // Create branch names string for display
        String branchNames = managedBranches.stream()
            .map(Branch::getBranchName)
            .collect(java.util.stream.Collectors.joining(", "));
        
        model.addAttribute("carPage", carPage);
        model.addAttribute("branchName", branchNames + " (" + managedBranches.size() + " branches)");
        return "manager/cars"; // templates/manager/cars.html
    }

    @GetMapping("/new")
    public String showCreateCarForm(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to add a car.");
            return "redirect:/manager/cars";
        }
        CarDTO carDTO = new CarDTO();
        carDTO.setBranchID(primaryBranchOpt.get().getBranchID()); // Pre-set manager's branch
        carDTO.setStatus("Available"); // Default status

        model.addAttribute("carDTO", carDTO);
        model.addAttribute("formTitle", "Add New Car to " + primaryBranchOpt.get().getBranchName());
        populateCarFormDropdowns(model);
        return "manager/car_form"; // templates/manager/car_form.html
    }

    @PostMapping("/save")
    public String saveCar(@Valid @ModelAttribute("carDTO") CarDTO carDTO, BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot save car: No managed branch found.");
            return "redirect:/manager/cars";
        }
        Integer currentBranchId = primaryBranchOpt.get().getBranchID();
        carDTO.setBranchID(currentBranchId); // Ensure car is associated with manager's branch

        if (result.hasErrors()) {
            model.addAttribute("formTitle", (carDTO.getCarID() == null ? "Add New" : "Edit") + " Car in " + primaryBranchOpt.get().getBranchName());
            populateCarFormDropdowns(model);
            return "manager/car_form";
        }

        try {
            if (carDTO.getCarID() == null) { // Create
                carService.createCar(carDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Car created successfully.");
            } else { // Update
                CarDTO existingCar = carService.getCarDtoById(carDTO.getCarID())
                    .orElseThrow(() -> new EntityNotFoundException("Car not found for update."));
                if(!existingCar.getBranchID().equals(currentBranchId)){
                     redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update car from another branch.");
                    return "redirect:/manager/cars";
                }
                carService.updateCar(carDTO.getCarID(), carDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Car updated successfully.");
            }
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            // For create, redirect to new form, for edit, redirect to edit form
            if (carDTO.getCarID() == null) return "redirect:/manager/cars/new";
            return "redirect:/manager/cars/edit/" + carDTO.getCarID();
        }
        return "redirect:/manager/cars";
    }

    @GetMapping("/edit/{carId}")
    public String showEditCarForm(@PathVariable Integer carId, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No managed branch found.");
            return "redirect:/manager/cars";
        }
        CarDTO carDTO = carService.getCarDtoById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + carId));

        if (!carDTO.getBranchID().equals(primaryBranchOpt.get().getBranchID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit cars from your own branch.");
            return "redirect:/manager/cars";
        }
        model.addAttribute("carDTO", carDTO);
        model.addAttribute("formTitle", "Edit Car in " + primaryBranchOpt.get().getBranchName());
        populateCarFormDropdowns(model);
        return "manager/car_form";
    }

    @PostMapping("/delete/{carId}")
    public String deleteCar(@PathVariable Integer carId, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No managed branch found.");
            return "redirect:/manager/cars";
        }
        try {
            CarDTO carToDelete = carService.getCarDtoById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found for deletion."));
            if (!carToDelete.getBranchID().equals(primaryBranchOpt.get().getBranchID())){
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to delete car from another branch.");
                return "redirect:/manager/cars";
            }
            carService.deleteCar(carId);
            redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            // Catch other exceptions e.g. DataIntegrityViolation if car has active reservations
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete car. It might have active reservations or other dependencies.");
        }
        return "redirect:/manager/cars";
    }
} 