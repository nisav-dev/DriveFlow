package com.driveflow.controller;

import com.driveflow.entity.Customer;
import com.driveflow.security.CustomUserDetails;
import com.driveflow.service.BookingService;
import com.driveflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-account")
@RequiredArgsConstructor
public class CustomerAccountController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Customer customer = userService.findCustomerByUserId(userDetails.getUser().getId())
                .orElseThrow();
        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookingService.findByCustomer(customer.getId()));
        return "account/dashboard";
    }

    @GetMapping("/bookings")
    public String bookings(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Customer customer = userService.findCustomerByUserId(userDetails.getUser().getId())
                .orElseThrow();
        model.addAttribute("bookings", bookingService.findByCustomer(customer.getId()));
        return "account/bookings";
    }
}
