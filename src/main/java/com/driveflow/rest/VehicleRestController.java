package com.driveflow.rest;

import com.driveflow.entity.Vehicle;
import com.driveflow.enums.VehicleStatus;
import com.driveflow.rest.dto.ApiResponse;
import com.driveflow.rest.dto.VehicleDto;
import com.driveflow.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "רכבים", description = "ניהול ציי רכבים — חיפוש, קבלת פרטים ועדכון סטטוס")
public class VehicleRestController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "כל הרכבים", description = "מחזיר את רשימת כל הרכבים במערכת")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getAllVehicles() {
        List<VehicleDto> vehicles = vehicleService.findAll()
                .stream().map(VehicleDto::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(vehicles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "רכב לפי מזהה", description = "מחזיר פרטים מלאים של רכב ספציפי")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "נמצא"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "לא נמצא")
    })
    public ResponseEntity<ApiResponse<VehicleDto>> getVehicle(
            @Parameter(description = "מזהה הרכב", example = "1") @PathVariable Long id) {
        return vehicleService.findById(id)
                .map(v -> ResponseEntity.ok(ApiResponse.ok(VehicleDto.from(v))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    @Operation(
            summary = "רכבים זמינים",
            description = "מחזיר רכבים זמינים לפי סניף, תאריכים וקטגוריה אופציונלית"
    )
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getAvailableVehicles(
            @Parameter(description = "מזהה סניף איסוף", required = true, example = "1")
            @RequestParam Long branchId,

            @Parameter(description = "תאריך ושעת איסוף (ISO)", required = true, example = "2026-06-10T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDate,

            @Parameter(description = "תאריך ושעת החזרה (ISO)", required = true, example = "2026-06-15T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate,

            @Parameter(description = "מזהה קטגוריה (אופציונלי)")
            @RequestParam(required = false) Long categoryId) {

        try {
            List<VehicleDto> vehicles = vehicleService
                    .findAvailableVehicles(branchId, pickupDate, returnDate, categoryId)
                    .stream().map(VehicleDto::from).toList();
            return ResponseEntity.ok(ApiResponse.ok(vehicles));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "חיפוש חופשי", description = "Autocomplete — חיפוש רכבים לפי טקסט (יצרן, דגם)")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> search(
            @Parameter(description = "מחרוזת חיפוש", example = "Toyota") @RequestParam String q) {
        List<VehicleDto> results = vehicleService.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE
                        && (v.getMake().toLowerCase().contains(q.toLowerCase())
                            || v.getModel().toLowerCase().contains(q.toLowerCase())
                            || v.getDisplayName().toLowerCase().contains(q.toLowerCase())))
                .limit(10)
                .map(VehicleDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "רכבים לפי סטטוס", description = "מחזיר רשימת רכבים בסטטוס מסוים")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getByStatus(
            @Parameter(description = "סטטוס רכב", example = "AVAILABLE") @PathVariable VehicleStatus status) {
        List<VehicleDto> vehicles = vehicleService.findAll().stream()
                .filter(v -> v.getStatus() == status)
                .map(VehicleDto::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(vehicles));
    }
}
