package com.driveflow.controller;

import com.driveflow.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        // error and logout are handled directly via ${param.error} / ${param.logout} in login.html
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam @Email String email,
                           @RequestParam @Size(min = 8) String password,
                           @RequestParam String confirmPassword,
                           @RequestParam @NotBlank String firstName,
                           @RequestParam @NotBlank String lastName,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) String idNumber,
                           @RequestParam(required = false) String licenseNumber,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate licenseExpiry,
                           RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "הסיסמאות אינן תואמות.");
                return "redirect:/register";
            }

            userService.register(email, password, firstName, lastName, phone, idNumber, licenseNumber, licenseExpiry);
            redirectAttributes.addFlashAttribute("success", "נרשמת בהצלחה! כנס עם פרטיך.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "לא הצלחנו להשלים את ההרשמה: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
