package com.driveflow.rest;

import com.driveflow.entity.Booking;
import com.driveflow.enums.BookingStatus;
import com.driveflow.repository.BookingRepository;
import com.driveflow.rest.dto.ApiResponse;
import com.driveflow.rest.dto.BookingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "הזמנות", description = "קבלת נתוני הזמנות — דורש הרשאת ADMIN/AGENT")
@SecurityRequirement(name = "cookieAuth")
public class BookingRestController {

    private final BookingRepository bookingRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    @Operation(summary = "כל ההזמנות", description = "מחזיר רשימת כל ההזמנות. דורש ADMIN או AGENT.")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getAllBookings(
            @Parameter(description = "סינון לפי סטטוס (אופציונלי)")
            @RequestParam(required = false) BookingStatus status) {

        List<Booking> bookings = (status != null)
                ? bookingRepository.findByStatus(status)
                : bookingRepository.findAll();

        List<BookingDto> dtos = bookings.stream().map(BookingDto::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(dtos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    @Operation(summary = "הזמנה לפי מזהה")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "נמצא"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "לא נמצא"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "אין הרשאה")
    })
    public ResponseEntity<ApiResponse<BookingDto>> getBooking(
            @Parameter(description = "מזהה הזמנה", example = "1") @PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(b -> ResponseEntity.ok(ApiResponse.ok(BookingDto.from(b))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{bookingNumber}")
    @Operation(summary = "הזמנה לפי מספר הזמנה", description = "מחזיר הזמנה לפי מספר DRIVEFLOW-YYYY-XXXXX")
    public ResponseEntity<ApiResponse<BookingDto>> getByBookingNumber(
            @Parameter(description = "מספר הזמנה", example = "DRIVEFLOW-2026-00001")
            @PathVariable String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber)
                .map(b -> ResponseEntity.ok(ApiResponse.ok(BookingDto.from(b))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    @Operation(summary = "סטטיסטיקות הזמנות", description = "ספירות לפי סטטוס")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        var stats = java.util.Map.of(
                "total",     bookingRepository.count(),
                "pending",   bookingRepository.countByStatus(BookingStatus.PENDING),
                "confirmed", bookingRepository.countByStatus(BookingStatus.CONFIRMED),
                "active",    bookingRepository.countByStatus(BookingStatus.ACTIVE),
                "completed", bookingRepository.countByStatus(BookingStatus.COMPLETED),
                "cancelled", bookingRepository.countByStatus(BookingStatus.CANCELLED)
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
