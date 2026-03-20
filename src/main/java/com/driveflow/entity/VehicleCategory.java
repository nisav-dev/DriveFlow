package com.driveflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;

    @Column(nullable = false)
    private int minAgeRequired = 21;

    @Column(nullable = false)
    private BigDecimal depositAmount;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    // Business logic
    public long getAvailableVehiclesCount() {
        return vehicles.stream()
                .filter(v -> v.getStatus().name().equals("AVAILABLE"))
                .count();
    }
}
