package com.driveflow.entity;

import com.driveflow.enums.*;
import com.driveflow.util.VehicleImageResolver;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(name = "manufacture_year")
    private int year;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private VehicleCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Column(nullable = false)
    private int numSeats;

    @Column(nullable = false)
    private int numDoors;

    private Integer luggageCapacity;
    private boolean hasAc = true;
    private boolean hasBluetooth = true;
    private boolean hasGpsBuiltin = false;

    @Column(nullable = false)
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(nullable = false)
    private int mileage = 0;

    private String mainImageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    // Business logic
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public String getDisplayName() {
        return year + " " + make + " " + model;
    }

    public String getImageUrl() {
        if (mainImageUrl != null && !mainImageUrl.isBlank()) {
            return mainImageUrl;
        }
        return VehicleImageResolver.resolve(make, model, category != null ? category.getName() : null);
    }

    public BigDecimal calculateRentalPrice(int days) {
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    public void updateMileage(int newMileage) {
        if (newMileage > this.mileage) {
            this.mileage = newMileage;
        }
    }
}
