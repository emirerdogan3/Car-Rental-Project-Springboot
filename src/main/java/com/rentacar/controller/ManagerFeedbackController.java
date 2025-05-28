package com.rentacar.controller;

import com.rentacar.dto.FeedbackDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.User;
import com.rentacar.service.BranchManagerService;
import com.rentacar.service.FeedbackService;
import com.rentacar.service.UserService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manager/feedbacks")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerFeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;
    private final BranchManagerService branchManagerService;

    @GetMapping
    public String listFeedbacks(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(name = "branchId", required = false) Integer selectedBranchId,
                                Model model,
                                @PageableDefault(size = 10, sort = "createdDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
                                RedirectAttributes redirectAttributes) {

        // Get manager's managed branches
        User managerUser = userService.findEntityByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Manager user not found: " + userDetails.getUsername()));
        List<Branch> managedBranches = branchManagerService.findBranchesByManagerId(managerUser.getUserID());
        
        if (managedBranches.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not managing any branch to see feedbacks.");
            return "redirect:/manager/dashboard";
        }

        // Determine which branch to show feedbacks for
        Integer branchId;
        String branchName;
        
        if (selectedBranchId != null) {
            // Verify manager has access to selected branch
            boolean hasAccess = managedBranches.stream()
                    .anyMatch(branch -> branch.getBranchID().equals(selectedBranchId));
            
            if (hasAccess) {
                branchId = selectedBranchId;
                branchName = managedBranches.stream()
                        .filter(branch -> branch.getBranchID().equals(selectedBranchId))
                        .map(Branch::getBranchName)
                        .findFirst()
                        .orElse("Unknown Branch");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "You don't have access to selected branch.");
                return "redirect:/manager/feedbacks";
            }
        } else {
            // Default to first managed branch
            Branch defaultBranch = managedBranches.get(0);
            branchId = defaultBranch.getBranchID();
            branchName = defaultBranch.getBranchName();
        }

        Page<FeedbackDTO> feedbackPage = feedbackService.getFeedbacksByBranch(branchId, pageable);
        System.out.println("DEBUG: Found " + feedbackPage.getTotalElements() + " feedbacks for branch ID: " + branchId);

        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("branchName", branchName);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("managedBranches", managedBranches);
        
        return "manager/feedbacks"; // Path to Thymeleaf template: templates/manager/feedbacks.html
    }
} 