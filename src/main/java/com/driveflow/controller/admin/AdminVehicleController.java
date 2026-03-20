package com.driveflow.controller.admin;

import com.driveflow.entity.Vehicle;
import com.driveflow.enums.FuelType;
import com.driveflow.enums.TransmissionType;
import com.driveflow.enums.VehicleStatus;
import com.driveflow.repository.BranchRepository;
import com.driveflow.repository.VehicleCategoryRepository;
import com.driveflow.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;
    private final VehicleCategoryRepository categoryRepository;
    private final BranchRepository branchRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vehicles", vehicleService.findAll());
        return "admin/vehicles/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("branches", branchRepository.findByActiveTrue());
        model.addAttribute("transmissions", TransmissionType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        return "admin/vehicles/form";
    }

    @PostMapping
    public String save(@RequestParam String licensePlate,
                        @RequestParam String make, @RequestParam String model2,
                        @RequestParam int year, @RequestParam String color,
                        @RequestParam Long categoryId, @RequestParam Long branchId,
                        @RequestParam TransmissionType transmission,
                        @RequestParam FuelType fuelType,
                        @RequestParam int numSeats, @RequestParam int numDoors,
                        @RequestParam BigDecimal dailyRate,
                        @RequestParam(required = false) String mainImageUrl,
                        RedirectAttributes redirectAttributes) {

        Vehicle v = Vehicle.builder()
                .licensePlate(licensePlate)
                .make(make).model(model2).year(year).color(color)
                .category(categoryRepository.findById(categoryId).orElseThrow())
                .branch(branchRepository.findById(branchId).orElseThrow())
                .transmission(transmission).fuelType(fuelType)
                .numSeats(numSeats).numDoors(numDoors)
                .dailyRate(dailyRate)
                .status(VehicleStatus.AVAILABLE)
                .mainImageUrl(mainImageUrl)
                .build();

        vehicleService.save(v);
        redirectAttributes.addFlashAttribute("success", "הרכב נוסף בהצלחה!");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam VehicleStatus status,
                                RedirectAttributes redirectAttributes) {
        vehicleService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "סטטוס הרכב עודכן.");
        return "redirect:/admin/vehicles";
    }
}
