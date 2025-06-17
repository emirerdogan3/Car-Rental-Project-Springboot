package com.rentacar.controller;

import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.UserDTO;
import com.rentacar.dto.CountryDTO;
import com.rentacar.dto.CityDTO;
import com.rentacar.dto.CountyDTO;
import com.rentacar.service.*; // For BranchService, UserService, CountryService, CityService, CountyService
import lombok.RequiredArgsConstructor; // For constructor injection
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.rentacar.exception.ResourceInUseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/branches")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor // Using Lombok for constructor injection
public class AdminBranchController {

    private final BranchService branchService;
    // private final BranchMapper branchMapper; // Not directly used in this modification, but usually present
    private final UserService userService; // For manager assignments, remains
    private final CountryService countryService; // Added
    private final CityService cityService;       // Added
    private final CountyService countyService;   // Added

    private void addLocationAttributes(Model model) {
        model.addAttribute("countries", countryService.getAllCountryDTOs());
        model.addAttribute("cities", cityService.getAllCityDTOs());
        model.addAttribute("counties", countyService.getAllCountyDTOs());
    }

    @GetMapping
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        return "admin/branches";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("branchForm", new BranchDTO());
        addLocationAttributes(model); // Populate location data for dropdowns
        return "admin/branch_form";
    }

    @PostMapping("/new")
    public String createBranch(@ModelAttribute("branchForm") BranchDTO branchDTO) {
        branchService.createBranch(branchDTO);
        return "redirect:/admin/branches";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        BranchDTO branchDto = branchService.getBranchDtoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch Id:" + id));
        model.addAttribute("branchForm", branchDto);
        addLocationAttributes(model); // Populate location data for dropdowns
        return "admin/branch_form";
    }

    @PostMapping("/edit/{id}")
    public String updateBranch(@PathVariable Integer id, @ModelAttribute("branchForm") BranchDTO branchDTO) {
        branchService.updateBranch(id, branchDTO);
        return "redirect:/admin/branches";
    }

    /*
    @PostMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Integer id) {
        branchService.deleteBranch(id);
        return "redirect:/admin/branches";
    }*/
    @PostMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            branchService.deleteBranch(id);
            redirectAttributes.addFlashAttribute("successMessage", "Branch deleted successfully.");
        } catch (ResourceInUseException e) {
            // Servisten gelen özel hatayı yakala ve mesajını arayüze gönder
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Diğer beklenmedik hatalar için genel bir mesaj göster
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the branch.");
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/{id}/managers")
    public String listManagersForBranch(@PathVariable Integer id, Model model) {
        BranchDTO branchDto = branchService.getBranchDtoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch Id: " + id));
        model.addAttribute("branch", branchDto);

        List<UserDTO> managerDTOs = Collections.emptyList();
        if (branchDto.getManagerNames() != null && !branchDto.getManagerNames().isEmpty()) {
            managerDTOs = branchDto.getManagerNames().stream()
                    .map((String username) -> userService.findDtoByUsername(username)
                                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        model.addAttribute("managers", managerDTOs);
        return "admin/branch_managers";
    }

    @GetMapping("/{id}/managers/new")
    public String showAddManagerForm(@PathVariable Integer id, Model model) {
        BranchDTO branchDto = branchService.getBranchDtoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch Id: " + id));
        model.addAttribute("branch", branchDto);
        model.addAttribute("managerForm", new UserDTO());
        List<UserDTO> availableManagers = userService.findDtosByRoleName("MANAGER").stream()
                .collect(Collectors.toList());
        model.addAttribute("allManagers", availableManagers);
        return "admin/manager_form";
    }

    @PostMapping("/{branchId}/managers/assign")
    public String assignManagerToBranch(@PathVariable Integer branchId, @RequestParam("userId") Integer userId) {
        userService.assignManagerToBranch(branchId, userId);
        return "redirect:/admin/branches/" + branchId + "/managers";
    }

    @PostMapping("/{branchId}/managers/remove/{userId}")
    public String removeManagerFromBranch(@PathVariable Integer branchId, @PathVariable Integer userId) {
        userService.removeManagerFromBranch(branchId, userId);
        return "redirect:/admin/branches/" + branchId + "/managers";
    }
} 