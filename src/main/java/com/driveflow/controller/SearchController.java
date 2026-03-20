package com.driveflow.controller;

import com.driveflow.repository.BranchRepository;
import com.driveflow.repository.VehicleCategoryRepository;
import com.driveflow.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final VehicleService vehicleService;
    private final BranchRepository branchRepository;
    private final VehicleCategoryRepository categoryRepository;

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) Long pickupBranchId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime pickupDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime returnDate,
            @RequestParam(required = false) Long categoryId,
            Model model) {

        var branches = branchRepository.findByActiveTrue();
        model.addAttribute("branches", branches);
        model.addAttribute("categories", categoryRepository.findAll());

        if (pickupDate == null || returnDate == null) {
            pickupDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            returnDate = pickupDate.plusDays(5);
        }
        if (pickupBranchId == null && !branches.isEmpty()) {
            pickupBranchId = branches.get(0).getId();
        }

        model.addAttribute("selectedBranchId", pickupBranchId);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("selectedCategoryId", categoryId);

        if (pickupDate != null && returnDate != null) {
            try {
                var vehicles = vehicleService.findAvailableVehicles(pickupBranchId, pickupDate, returnDate, categoryId);
                model.addAttribute("vehicles", vehicles);
                int numDays = (int) ChronoUnit.DAYS.between(pickupDate.toLocalDate(), returnDate.toLocalDate());
                model.addAttribute("numDays", Math.max(numDays, 1));
            } catch (IllegalArgumentException e) {
                model.addAttribute("searchError", "טווח התאריכים אינו תקין. יש לבחור תאריך החזרה אחרי האיסוף.");
            }
        }

        return "search/results";
    }
}
