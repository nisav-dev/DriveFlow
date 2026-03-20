package com.driveflow.controller.admin;

import com.driveflow.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
        return "admin/bookings/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.findById(id).orElseThrow());
        return "admin/bookings/detail";
    }

    @PostMapping("/{id}/pickup")
    public String pickup(@PathVariable Long id,
                          @RequestParam int pickupMileage,
                          RedirectAttributes redirectAttributes) {
        bookingService.markActive(id, pickupMileage);
        redirectAttributes.addFlashAttribute("success", "הרכב נמסר ללקוח.");
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/return")
    public String returnVehicle(@PathVariable Long id,
                                 @RequestParam int returnMileage,
                                 @RequestParam(defaultValue = "false") boolean hasDamage,
                                 RedirectAttributes redirectAttributes) {
        bookingService.complete(id, returnMileage, hasDamage);
        redirectAttributes.addFlashAttribute("success", "הרכב הוחזר. ההזמנה נסגרה.");
        return "redirect:/admin/bookings/" + id;
    }
}
