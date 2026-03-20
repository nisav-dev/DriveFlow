package com.driveflow.entity;

import com.driveflow.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private String idNumber;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private LocalDate licenseExpiry;

    @Column(nullable = false)
    private String licenseCountry = "IL";

    private String addressStreet;
    private String addressCity;

    @Column(nullable = false)
    private String addressCountry = "ישראל";

    @Column(nullable = false)
    private int loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType = CustomerType.REGULAR;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Business logic
    public boolean hasValidLicense() {
        return licenseExpiry != null && licenseExpiry.isAfter(LocalDate.now());
    }

    public int getTotalBookingsCount() {
        return bookings.size();
    }

    public BigDecimal getTotalAmountSpent() {
        return bookings.stream()
                .filter(b -> b.getStatus().name().startsWith("COMPLETED"))
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }
}
