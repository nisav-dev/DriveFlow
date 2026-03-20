package com.driveflow.controller;

import com.driveflow.entity.Booking;
import com.driveflow.entity.Customer;
import com.driveflow.repository.BranchRepository;
import com.driveflow.repository.ExtraRepository;
import com.driveflow.repository.VehicleRepository;
import com.driveflow.security.CustomUserDetails;
import com.driveflow.service.BookingService;
import com.driveflow.service.PdfService;
import com.driveflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final ExtraRepository extraRepository;
    private final PdfService pdfService;

    @GetMapping("/new")
    public String newBooking(@RequestParam Long vehicleId,
                              @RequestParam Long pickupBranchId,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime pickupDate,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime returnDate,
                              Model model) {
        model.addAttribute("vehicle", vehicleRepository.findById(vehicleId).orElseThrow());
        model.addAttribute("pickupBranch", branchRepository.findById(pickupBranchId).orElseThrow());
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("extras", extraRepository.findByActiveTrue());
        int numDays = (int) java.time.temporal.ChronoUnit.DAYS
                .between(pickupDate.toLocalDate(), returnDate.toLocalDate());
        model.addAttribute("numDays", Math.max(numDays, 1));
        return "booking/new";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long vehicleId,
                          @RequestParam Long pickupBranchId,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime pickupDate,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime returnDate,
                          @RequestParam(required = false) List<Long> extraIds,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            Customer customer = userService.findCustomerByUserId(userDetails.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            Booking booking = bookingService.createBooking(
                    customer.getId(), vehicleId, pickupBranchId, pickupBranchId,
                    pickupDate, returnDate, extraIds);

            // Simulate auto-confirm after "payment"
            bookingService.confirm(booking.getId());

            redirectAttributes.addFlashAttribute("success", "ההזמנה אושרה בהצלחה!");
            return "redirect:/booking/confirmation/" + booking.getBookingNumber();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "אירעה שגיאה ביצירת ההזמנה: " + e.getMessage());
            return "redirect:/search";
        }
    }

    @GetMapping("/confirmation/{bookingNumber}")
    public String confirmation(@PathVariable String bookingNumber, Model model) {
        Booking booking = bookingService.findByNumber(bookingNumber)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        return "booking/confirmation";
    }

    // ── PDF הורדת אישור הזמנה ───────────────────────────────────────────────

    @GetMapping("/confirmation/{bookingNumber}/pdf")
    public ResponseEntity<byte[]> downloadConfirmationPdf(@PathVariable String bookingNumber) {
        Booking booking = bookingService.findByNumber(bookingNumber)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingNumber));

        byte[] pdf = pdfService.generateBookingConfirmationPdf(booking);

        String filename = "driveflow-" + bookingNumber + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id,
                          @RequestParam(required = false) String reason,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        bookingService.cancel(id, reason != null ? reason : "בוטל על ידי הלקוח");
        redirectAttributes.addFlashAttribute("success", "ההזמנה בוטלה.");
        return "redirect:/my-account/bookings";
    }
}
