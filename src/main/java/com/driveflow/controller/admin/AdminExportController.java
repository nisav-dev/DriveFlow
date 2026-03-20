package com.driveflow.controller.admin;

import com.driveflow.repository.BookingRepository;
import com.driveflow.repository.VehicleRepository;
import com.driveflow.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin/export")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','AGENT')")
public class AdminExportController {

    private final ExcelExportService excelExportService;
    private final BookingRepository  bookingRepository;
    private final VehicleRepository  vehicleRepository;

    private static final MediaType EXCEL =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @GetMapping("/bookings")
    public ResponseEntity<byte[]> exportBookings() {
        byte[] data = excelExportService.exportBookings(bookingRepository.findAll());
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"driveflow-bookings-" + ts + ".xlsx\"")
                .contentType(EXCEL)
                .body(data);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<byte[]> exportVehicles() {
        byte[] data = excelExportService.exportVehicles(vehicleRepository.findAll());
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"driveflow-vehicles-" + ts + ".xlsx\"")
                .contentType(EXCEL)
                .body(data);
    }
}
