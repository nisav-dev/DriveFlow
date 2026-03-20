package com.driveflow.controller.admin;

import com.driveflow.enums.VehicleStatus;
import com.driveflow.service.BookingService;
import com.driveflow.service.UserService;
import com.driveflow.service.VehicleService;
import com.driveflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final VehicleRepository vehicleRepository;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("monthlyRevenue", bookingService.getMonthlyRevenue());
        model.addAttribute("activeBookings", bookingService.countActive());
        model.addAttribute("availableVehicles", vehicleService.countByStatus(VehicleStatus.AVAILABLE));
        model.addAttribute("rentedVehicles", vehicleService.countByStatus(VehicleStatus.RENTED));
        model.addAttribute("maintenanceVehicles", vehicleService.countByStatus(VehicleStatus.MAINTENANCE));
        model.addAttribute("totalVehicles", vehicleRepository.count());
        model.addAttribute("recentBookings", bookingService.findAll().stream().limit(8).toList());
        model.addAttribute("totalCustomers", userService.findAllCustomers().size());
        return "admin/dashboard";
    }
}
