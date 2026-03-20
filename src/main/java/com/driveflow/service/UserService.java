package com.driveflow.service;

import com.driveflow.entity.Customer;
import com.driveflow.entity.User;
import com.driveflow.enums.CustomerType;
import com.driveflow.enums.UserRole;
import com.driveflow.repository.CustomerRepository;
import com.driveflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String email, String password, String firstName, String lastName, String phone) {
        return register(email, password, firstName, lastName, phone, null, null, null);
    }

    @Transactional
    public User register(String email, String password, String firstName, String lastName, String phone,
                         String idNumber, String licenseNumber, LocalDate licenseExpiry) {
        String normalizedEmail = normalize(email);
        if (normalizedEmail.isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new RuntimeException("Email already in use: " + normalizedEmail);
        }

        User user = User.builder()
            .email(normalizedEmail)
            .password(passwordEncoder.encode(password))
            .firstName(safeValue(firstName))
            .lastName(safeValue(lastName))
            .phone(normalizeNullable(phone))
            .role(UserRole.CUSTOMER)
            .active(true)
            .build();

        user = userRepository.save(user);

        Customer customer = Customer.builder()
            .user(user)
            .idNumber(normalizeNullable(idNumber))
            .licenseNumber(normalizeNullable(licenseNumber))
            .licenseExpiry(licenseExpiry)
            .customerType(CustomerType.REGULAR)
            .loyaltyPoints(0)
            .licenseCountry("IL")
            .addressCountry("ישראל")
            .build();

        customerRepository.save(customer);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalize(email));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String safeValue(String value) {
        String normalized = normalizeNullable(value);
        return normalized == null ? "" : normalized;
    }
}
