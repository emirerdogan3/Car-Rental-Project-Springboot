package com.rentacar.controller;

import com.rentacar.dto.CarDTO;
import com.rentacar.service.CarService;
import com.rentacar.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import com.rentacar.dto.request.ReservationRequestDTO;
import com.rentacar.dto.ReservationDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/reservation") 
@PreAuthorize("hasRole('CUSTOMER')") // Sadece müşteriler rezervasyon yapabilsin
@RequiredArgsConstructor
public class ReservationFormController { 

    private final CarService carService;
    private final ReservationService reservationService;

    @GetMapping("/form")
    public String showReservationForm(@RequestParam("carId") Integer carId, Model model, RedirectAttributes redirectAttributes) {
        CarDTO carDTO = carService.getCarDtoById(carId).orElse(null);
        if (carDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected car not found.");
            return "redirect:/cars"; // Ya da anasayfaya
        }
        // Araç müsait değilse veya başka bir statüdeyse formu gösterme veya uyarı ver.
        // Bu kontrol carService.findAvailableCarsByBranchAndFilters içinde zaten yapılıyor olmalı
        // ama burada bir kez daha kontrol etmek iyi olabilir.
        // if (!"Available".equalsIgnoreCase(carDTO.getStatus())) {
        //     redirectAttributes.addFlashAttribute("errorMessage", "Selected car is not available for reservation.");
        //     // Şubenin araç listesine geri dönülebilir:
        //     return "redirect:/cars?branchId=" + (carDTO.getBranchID() != null ? carDTO.getBranchID() : ""); 
        // }

        model.addAttribute("car", carDTO);
        return "customer/reservation_form"; // templates/customer/reservation_form.html
    }

    @GetMapping("/api/check-availability")
    @ResponseBody
    public ResponseEntity<?> checkCarAvailability(@RequestParam("carId") Integer carId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        if (startDate.after(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("available", false, "message", "Start date cannot be after end date."));
        }
        boolean available = reservationService.isCarAvailable(carId, startDate, endDate);
        // Araç genel olarak 'Available' değilse de false dönülebilir.
        CarDTO car = carService.getCarDtoById(carId).orElse(null);
        if (car == null || !"Available".equalsIgnoreCase(car.getStatus())) {
             if(available) { // Eğer çakışan rezervasyon yoksa ama araç statüsü uygun değilse özel mesaj
                return ResponseEntity.ok(Map.of("available", false, "message", "Car is not in Available status."));
             } // eğer zaten available=false ise (çakışmadan dolayı) bu mesajı override etme
        }

        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/api/calculate-price")
    @ResponseBody
    public ResponseEntity<?> calculatePrice(@RequestParam("carId") Integer carId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        if (startDate.after(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date cannot be after end date."));
        }
        try {
            double totalPrice = reservationService.calculateTotalPrice(carId, startDate, endDate);
            return ResponseEntity.ok(Map.of("totalPrice", totalPrice));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // "Öde ve Kirala" butonu için POST mapping
    @PostMapping("/submit")
    public String submitReservation(@Valid ReservationRequestDTO requestDTO, 
                                    BindingResult bindingResult, // Validation sonuçları için
                                    @AuthenticationPrincipal UserDetails currentUser, 
                                    RedirectAttributes redirectAttributes, Model model) {
        
        // Formdan gelen DTO'da totalPrice var, ancak bu tekrar hesaplanıp doğrulanacak.
        // branchId de formdan gelmeli (hidden input olarak).
        // carId zaten formda olacak.

        if (bindingResult.hasErrors()) {
            // Hataları reservation_form.html'de göstermek için modeli tekrar doldur
            CarDTO carDTO = carService.getCarDtoById(requestDTO.getCarId()).orElse(null);
            if (carDTO == null) { // Should not happen if form was populated correctly
                 redirectAttributes.addFlashAttribute("errorMessage", "Car details could not be loaded for the form.");
                 return "redirect:/customer/branches"; // Veya genel bir hata sayfası
            }
            model.addAttribute("car", carDTO);
            model.addAttribute("reservationRequest", requestDTO); // Hatalı DTO'yu forma geri gönder
            // Gerekirse diğer model attribute'leri (örn: hata mesajları) eklenebilir.
            return "customer/reservation_form";
        }
        
        // Güvenlik: İstemci tarafından gönderilen fiyatı doğrula veya sunucuda yeniden hesapla.
        // Bu, ReservationServiceImpl içinde zaten yapılıyor.

        try {
            ReservationDTO createdReservation = reservationService.createReservationAndProcessPayment(requestDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation successful! Your reservation ID is: " + createdReservation.getReservationID());
            // Başarılı ödeme sonrası onay ekranına yönlendir
            return "redirect:/reservation/success?reservationId=" + createdReservation.getReservationID(); 
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating reservation: " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not process reservation: " + e.getMessage());
        } catch (Exception e) {
            // Genel bir hata mesajı
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            // Log error e.printStackTrace();
        }
        // Hata durumunda formu tekrar göster, carId ve branchId ile.
        // Ya da daha genel bir hata sayfasına yönlendir.
        return "redirect:/reservation/form?carId=" + requestDTO.getCarId();
    }

    @GetMapping("/success")
    public String reservationSuccess(@RequestParam("reservationId") Integer reservationId, Model model) {
        // Rezervasyon detaylarını alıp model'e ekleyebiliriz, ama şimdilik sadece ID yeterli.
        model.addAttribute("reservationId", reservationId);
        return "customer/reservation_success"; // templates/customer/reservation_success.html
    }
} 