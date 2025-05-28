package com.rentacar.controller;

import com.rentacar.dto.FeedbackDTO;
import com.rentacar.dto.ReservationDTO;
import com.rentacar.service.FeedbackService;
import com.rentacar.service.ReservationService;
import com.rentacar.service.UserService;
import com.rentacar.dto.UserDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping("/customer/feedback")
    public String showCustomerFeedbackForm(@RequestParam(name = "reservationId", required = false) Integer reservationId,
                                          Model model, 
                                          @AuthenticationPrincipal UserDetails currentUser) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        
        // Get user info
        UserDTO userDto = userService.findDtoByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUser.getUsername()));
        
        if (reservationId != null) {
            feedbackDTO.setReservationID(reservationId);
        }
        
        // Get completed reservations for feedback
        List<ReservationDTO> completedReservations = reservationService.getCompletedReservationsWithoutFeedback(currentUser);
        model.addAttribute("completedReservations", completedReservations);
        
        // Get user's previous feedbacks
        List<FeedbackDTO> userFeedbacks = feedbackService.getAllFeedbacks().stream()
                .filter(feedback -> feedback.getCustomer() != null && 
                                  feedback.getCustomer().getUser() != null &&
                                  feedback.getCustomer().getUser().getUserID().equals(userDto.getUserID()))
                .map(feedback -> {
                    FeedbackDTO dto = new FeedbackDTO();
                    dto.setFeedbackID(feedback.getFeedbackID());
                    dto.setReservationID(feedback.getReservation() != null ? feedback.getReservation().getReservationID() : null);
                    dto.setRating(feedback.getRating());
                    dto.setComment(feedback.getComment());
                    dto.setCreatedDate(feedback.getCreatedDate());
                    return dto;
                })
                .collect(Collectors.toList());
        model.addAttribute("userFeedbacks", userFeedbacks);
        
        model.addAttribute("feedbackDTO", feedbackDTO);
        
        if (completedReservations.isEmpty()) {
            model.addAttribute("infoMessage", "You have no completed reservations available for feedback.");
        }
        
        return "customer/feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@Valid @ModelAttribute("feedbackDTO") FeedbackDTO feedbackDTO,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // Reload data for form
            List<ReservationDTO> completedReservations = reservationService.getCompletedReservationsWithoutFeedback(currentUser);
            model.addAttribute("completedReservations", completedReservations);
            
            UserDTO userDto = userService.findDtoByUsername(currentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + currentUser.getUsername()));
            List<FeedbackDTO> userFeedbacks = feedbackService.getAllFeedbacks().stream()
                    .filter(feedback -> feedback.getCustomer() != null && 
                                      feedback.getCustomer().getUser() != null &&
                                      feedback.getCustomer().getUser().getUserID().equals(userDto.getUserID()))
                    .map(feedback -> {
                        FeedbackDTO dto = new FeedbackDTO();
                        dto.setFeedbackID(feedback.getFeedbackID());
                        dto.setReservationID(feedback.getReservation() != null ? feedback.getReservation().getReservationID() : null);
                        dto.setRating(feedback.getRating());
                        dto.setComment(feedback.getComment());
                        dto.setCreatedDate(feedback.getCreatedDate());
                        return dto;
                    })
                    .collect(Collectors.toList());
            model.addAttribute("userFeedbacks", userFeedbacks);
            
            return "customer/feedback";
        }

        try {
            feedbackService.createFeedback(feedbackDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback! Your review has been submitted successfully.");
            return "redirect:/customer/feedback";
        } catch (IllegalStateException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not submit feedback: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while submitting your feedback.");
        }
        
        return "redirect:/customer/feedback";
    }

    // Legacy endpoints for backward compatibility
    @GetMapping("/feedback/form")
    public String showFeedbackForm(@RequestParam(name = "reservationId", required = false) Integer reservationId,
                                   Model model, 
                                   @AuthenticationPrincipal UserDetails currentUser) {
        return "redirect:/customer/feedback" + (reservationId != null ? "?reservationId=" + reservationId : "");
    }

    @PostMapping("/feedback/submit")
    public String submitFeedbackLegacy(@Valid @ModelAttribute("feedbackDTO") FeedbackDTO feedbackDTO,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal UserDetails currentUser,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        return submitFeedback(feedbackDTO, bindingResult, currentUser, redirectAttributes, model);
    }
    
    @PostMapping("/feedback/delete/{feedbackId}")
    public String deleteFeedback(@PathVariable Integer feedbackId,
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedback(feedbackId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully. You can now leave new feedback for this reservation.");
            return "redirect:/customer/feedback";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete feedback: " + e.getMessage());
        }
        return "redirect:/customer/feedback";
    }
} 