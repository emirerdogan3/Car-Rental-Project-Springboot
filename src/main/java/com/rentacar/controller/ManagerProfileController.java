package com.rentacar.controller;

import com.rentacar.dto.ChangePasswordDTO;
import com.rentacar.dto.UserProfileDTO;
import com.rentacar.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/profile")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerProfileController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            System.out.println("DEBUG: Manager profile request for user: " + currentUser.getUsername());
            UserProfileDTO userProfile = userService.getCurrentUserProfile(currentUser);
            System.out.println("DEBUG: UserProfile loaded: " + (userProfile != null ? "SUCCESS" : "NULL"));
            if (userProfile != null) {
                System.out.println("DEBUG: Profile details - firstName: " + userProfile.getFirstName() + 
                                 ", roleName: " + userProfile.getRoleName());
            }
            model.addAttribute("userProfile", userProfile);
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
            return "manager/profile";
        } catch (Exception e) {
            System.out.println("DEBUG: Error in manager profile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "User information could not be loaded: " + e.getMessage());
            return "manager/profile";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDTO userProfileDTO,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
            return "manager/profile";
        }
        try {
            userService.updateUserProfile(userProfileDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/manager/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())){
            bindingResult.rejectValue("confirmNewPassword", "error.changePasswordDTO", "New passwords do not match.");
        }

        if (bindingResult.hasErrors()) {
            UserProfileDTO userProfile = userService.getCurrentUserProfile(currentUser);
            model.addAttribute("userProfile", userProfile);
            return "manager/profile";
        }

        try {
            userService.changePassword(changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword(), currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error changing password. Please try again.");
        }
        return "redirect:/manager/profile";
    }
} 