package com.driveflow.rest;

import com.driveflow.repository.BranchRepository;
import com.driveflow.rest.dto.ApiResponse;
import com.driveflow.rest.dto.BranchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "סניפים", description = "מידע על סניפי DriveFlow — כולל קואורדינטות GPS למפה")
public class BranchRestController {

    private final BranchRepository branchRepository;

    @GetMapping
    @Operation(summary = "כל הסניפים הפעילים", description = "משמש את מפת Leaflet.js בדף הבית")
    public ResponseEntity<ApiResponse<List<BranchDto>>> getAllBranches() {
        List<BranchDto> branches = branchRepository.findByActiveTrue()
                .stream().map(BranchDto::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(branches));
    }

    @GetMapping("/{id}")
    @Operation(summary = "סניף לפי מזהה")
    public ResponseEntity<ApiResponse<BranchDto>> getBranch(
            @Parameter(description = "מזהה סניף", example = "1") @PathVariable Long id) {
        return branchRepository.findById(id)
                .map(b -> ResponseEntity.ok(ApiResponse.ok(BranchDto.from(b))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/map")
    @Operation(
            summary = "נתוני מפה",
            description = "מחזיר רק id, name, city, latitude, longitude לשימוש ב-Leaflet.js"
    )
    public ResponseEntity<ApiResponse<List<BranchDto>>> getMapData() {
        List<BranchDto> mapPoints = branchRepository.findByActiveTrue().stream()
                .filter(b -> b.getLatitude() != null && b.getLongitude() != null)
                .map(BranchDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(mapPoints));
    }
}
