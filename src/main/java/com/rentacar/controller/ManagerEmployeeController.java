package com.rentacar.controller;

import com.rentacar.dto.EmployeeDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.Employee;
import com.rentacar.entity.User;
import com.rentacar.entity.MoneyAccount;
import com.rentacar.service.BranchManagerService;
import com.rentacar.service.EmployeeService;
import com.rentacar.service.EmployeePaymentService;
import com.rentacar.service.UserService;
import com.rentacar.repository.MoneyAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; // For @Valid
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Controller
@RequestMapping("/manager/employees")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerEmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;
    private final BranchManagerService branchManagerService;
    private final EmployeePaymentService employeePaymentService;
    private final MoneyAccountRepository moneyAccountRepository;

    // Helper to get the manager's primary branch (first managed branch for now)
    private Optional<Branch> getManagerPrimaryBranch(UserDetails userDetails) {
        User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found: " + userDetails.getUsername()));
        List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(managerUser.getUserID());
        System.out.println("DEBUG: Manager " + userDetails.getUsername() + " (ID: " + managerUser.getUserID() + ") manages " + managedBranches.size() + " branches");
        if (!managedBranches.isEmpty()) {
            System.out.println("DEBUG: First managed branch: " + managedBranches.get(0).getBranchName() + " (ID: " + managedBranches.get(0).getBranchID() + ")");
        }
        return managedBranches.stream().findFirst();
    }

    @GetMapping
    public String listEmployees(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to see employees.");
            return "redirect:/manager/dashboard"; // Or an appropriate error page/message display
        }
        Integer branchId = primaryBranchOpt.get().getBranchID();
        List<EmployeeDTO> employees = employeeService.getEmployeesByBranch(branchId);
        System.out.println("DEBUG: Found " + employees.size() + " employees for branch ID: " + branchId);
        model.addAttribute("employees", employees);
        model.addAttribute("branchId", branchId); // For forms if needed
        model.addAttribute("branchName", primaryBranchOpt.get().getBranchName());
        return "manager/employees"; // templates/manager/employees.html
    }

    @GetMapping("/new")
    public String showCreateEmployeeForm(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to add an employee.");
            return "redirect:/manager/employees";
        }
        model.addAttribute("employeeDTO", EmployeeDTO.builder().branchID(primaryBranchOpt.get().getBranchID()).enabled(true).build());
        model.addAttribute("formTitle", "Add New Employee to " + primaryBranchOpt.get().getBranchName());
        model.addAttribute("branchId", primaryBranchOpt.get().getBranchID());
        return "manager/employee_form"; // templates/manager/employee_form.html
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
                               BindingResult result, @AuthenticationPrincipal UserDetails userDetails,
                               Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot save employee: No managed branch found.");
            return "redirect:/manager/employees";
        }
        Integer currentBranchId = primaryBranchOpt.get().getBranchID();
        employeeDTO.setBranchID(currentBranchId);

        if (result.hasErrors()) {
            model.addAttribute("formTitle", (employeeDTO.getEmployeeID() == null ? "Add New" : "Edit") + " Employee to " + primaryBranchOpt.get().getBranchName());
            model.addAttribute("branchId", currentBranchId);
            return "manager/employee_form";
        }

        try {
            if (employeeDTO.getEmployeeID() == null) { // Create new
                employeeService.createEmployee(employeeDTO, userDetails);
                redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully.");
            } else { // Update existing
                Optional<EmployeeDTO> existingEmpOpt = employeeService.getEmployeeById(employeeDTO.getEmployeeID());
                if (existingEmpOpt.isEmpty()) {
                    throw new EntityNotFoundException("Employee not found for update.");
                }
                EmployeeDTO existingEmpDTO = existingEmpOpt.get();
                
                // Validate branch access
                if(existingEmpDTO.getBranchID() == null || !existingEmpDTO.getBranchID().equals(currentBranchId)){
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update employee from another branch.");
                    return "redirect:/manager/employees";
                }

                // Validate user account
                if(existingEmpDTO.getUserID() == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Employee user account not found.");
                    return "redirect:/manager/employees";
                }

                employeeService.updateEmployee(employeeDTO.getEmployeeID(), employeeDTO, userDetails);
                redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully.");
            }
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("formTitle", (employeeDTO.getEmployeeID() == null ? "Add New" : "Edit") + " Employee to " + primaryBranchOpt.get().getBranchName());
            model.addAttribute("branchId", currentBranchId);
            return "manager/employee_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while saving the employee data.");
            model.addAttribute("formTitle", (employeeDTO.getEmployeeID() == null ? "Add New" : "Edit") + " Employee to " + primaryBranchOpt.get().getBranchName());
            model.addAttribute("branchId", currentBranchId);
            return "manager/employee_form";
        }
        return "redirect:/manager/employees";
    }

    @GetMapping("/edit/{employeeId}")
    public String showEditEmployeeForm(@PathVariable Integer employeeId, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No managed branch found.");
            return "redirect:/manager/employees";
        }
        Integer currentBranchId = primaryBranchOpt.get().getBranchID();

        try {
            EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
            
            if (employeeDTO.getBranchID() == null || !employeeDTO.getBranchID().equals(currentBranchId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only edit employees from your own branch.");
                return "redirect:/manager/employees";
            }

            // If user account is missing, we'll create it during update
            if (employeeDTO.getUserID() == null) {
                employeeDTO.setEnabled(true); // Set default values for new user account
            }

            model.addAttribute("employeeDTO", employeeDTO);
            model.addAttribute("formTitle", "Edit Employee in " + primaryBranchOpt.get().getBranchName());
            model.addAttribute("branchId", currentBranchId);
            return "manager/employee_form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/manager/employees";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while loading the employee data.");
            return "redirect:/manager/employees";
        }
    }

    @PostMapping("/set-enabled")
    public String setEmployeeEnabled(@RequestParam("employeeId") Integer employeeId, 
                                   @RequestParam("enabled") boolean enabled,
                                   @AuthenticationPrincipal UserDetails userDetails, 
                                   RedirectAttributes redirectAttributes) {
        Optional<Branch> primaryBranchOpt = getManagerPrimaryBranch(userDetails);
        if (primaryBranchOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No managed branch found.");
            return "redirect:/manager/employees";
        }
        Integer currentBranchId = primaryBranchOpt.get().getBranchID();
        
        try {
            EmployeeDTO existingEmpDTO = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found."));
            
            if(existingEmpDTO.getBranchID() == null || !existingEmpDTO.getBranchID().equals(currentBranchId)){
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to modify employee from another branch.");
                return "redirect:/manager/employees";
            }
            
            if(existingEmpDTO.getUserID() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Employee user account not found.");
                return "redirect:/manager/employees";
            }
            
            userService.setUserEnabled(existingEmpDTO.getUserID(), enabled);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Employee " + (enabled ? "activated" : "deactivated") + " successfully.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while updating employee status.");
        }
        return "redirect:/manager/employees";
    }

    // Maaş ödeme - Branch seçim sayfası
    @GetMapping("/pay-salary/{employeeId}")
    public String showBranchSelectionForSalary(@PathVariable Integer employeeId,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model, RedirectAttributes redirectAttributes) {
        try {
            // Employee'yi getir ve doğrula
            EmployeeDTO existingEmpDTO = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found."));
            
            // Manager'ın managed branch'lerini getir
            User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found: " + userDetails.getUsername()));
            List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(managerUser.getUserID());
            
            if (managedBranches.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No managed branches found.");
                return "redirect:/manager/employees";
            }
            
            // Her branch'in bakiyesini getir
            Map<Integer, Double> branchBalances = new HashMap<>();
            for (Branch branch : managedBranches) {
                try {
                    MoneyAccount account = moneyAccountRepository.findByBranch_BranchID(branch.getBranchID())
                        .orElse(null);
                    branchBalances.put(branch.getBranchID(), account != null ? account.getMoneyBalance() : 0.0);
                } catch (Exception e) {
                    branchBalances.put(branch.getBranchID(), 0.0);
                }
            }
            
            model.addAttribute("employee", existingEmpDTO);
            model.addAttribute("managedBranches", managedBranches);
            model.addAttribute("branchBalances", branchBalances);
            
            return "manager/select_branch_for_salary";
            
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        return "redirect:/manager/employees";
    }

    // Maaş ödeme onaylama
    @PostMapping("/pay-salary-confirm")
    public String confirmSalaryPayment(@RequestParam("employeeId") Integer employeeId,
                                     @RequestParam("branchId") Integer branchId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Employee'yi getir
            EmployeeDTO existingEmpDTO = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found."));
            
            // Maaş ödemesini yap
            employeePaymentService.paySalary(employeeId, branchId, userDetails);
            
            // Branch ismini al
            User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found"));
            List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(managerUser.getUserID());
            String branchName = managedBranches.stream()
                .filter(branch -> branch.getBranchID().equals(branchId))
                .map(Branch::getBranchName)
                .findFirst()
                .orElse("Unknown Branch");
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Salary payment successful for " + existingEmpDTO.getFirstName() + " " + existingEmpDTO.getLastName() + 
                ". Amount: " + String.format("%.2f ₺", existingEmpDTO.getSalary()) + 
                " from " + branchName);
                
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Insufficient funds: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred during salary payment.");
        }
        return "redirect:/manager/employees";
    }
} 