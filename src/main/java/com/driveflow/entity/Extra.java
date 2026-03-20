package com.driveflow.entity;

import com.driveflow.enums.PricingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "extras")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal dailyPrice;
    private BigDecimal fixedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType pricingType;

    private String icon;

    @Column(nullable = false)
    private boolean active = true;

    // Business logic
    public BigDecimal calculatePrice(int days) {
        if (pricingType == PricingType.FIXED) {
            return fixedPrice;
        }
        return dailyPrice.multiply(BigDecimal.valueOf(days));
    }
}
