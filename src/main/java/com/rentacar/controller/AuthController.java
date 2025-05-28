package com.rentacar.controller;

import com.rentacar.dto.UserDTO;
import com.rentacar.dto.CustomerDTO;
import com.rentacar.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication, @RequestParam(required = false) String employeeAccessDenied) {
        if ("true".equals(employeeAccessDenied)) {
            return "login";
        }
        
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                } else if (role.equals("ROLE_MANAGER")) {
                    return "redirect:/manager";
                } else if (role.equals("ROLE_CUSTOMER")) {
                    return "redirect:/customer";
                } else if (role.equals("ROLE_EMPLOYEE")) {
                    return "redirect:/employee";
                }
            }
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("customerDTO", new CustomerDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute("userDTO") UserDTO userDTO, 
                              @ModelAttribute("customerDTO") CustomerDTO customerDTO, 
                              Model model) {
        
        try {
            authService.register(userDTO, customerDTO);
            return "redirect:/login?registerSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("customerDTO", customerDTO);
            return "register";
        } catch (Exception e) {
            model.addAttribute("registrationError", "Registration failed. Please try again.");
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("customerDTO", customerDTO);
            return "register";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        return "profile";
    }
} 