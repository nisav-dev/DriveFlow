package com.driveflow.entity;

import com.driveflow.enums.DamageStatus;
import com.driveflow.enums.DamageType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "damage_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DamageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageType damageType;

    private String damageLocation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private BigDecimal estimatedCost;
    private BigDecimal actualCost;

    @Column(nullable = false)
    private boolean customerFault = false;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageStatus status = DamageStatus.REPORTED;

    // Business logic
    public BigDecimal getChargeableAmount() {
        if (!customerFault) return BigDecimal.ZERO;
        if (actualCost != null) return actualCost;
        return estimatedCost != null ? estimatedCost : BigDecimal.ZERO;
    }
}
