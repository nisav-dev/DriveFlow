package com.driveflow.controller;

import com.driveflow.repository.BranchRepository;
import com.driveflow.repository.VehicleCategoryRepository;
import com.driveflow.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BranchRepository branchRepository;
    private final VehicleCategoryRepository categoryRepository;
    private final VehicleService vehicleService;

    @GetMapping("/")
    public String home(Model model) {
        var branches = branchRepository.findByActiveTrue();
        model.addAttribute("branches", branches);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("featuredVehicles", vehicleService.findFeaturedAvailable());

        LocalDateTime pickupDefault = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime returnDefault = pickupDefault.plusDays(5);
        model.addAttribute("pickupDefault", pickupDefault);
        model.addAttribute("returnDefault", returnDefault);
        model.addAttribute("defaultBranchId", branches.isEmpty() ? null : branches.get(0).getId());
        return "home/index";
    }
}
