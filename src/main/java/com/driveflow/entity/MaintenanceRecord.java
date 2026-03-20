package com.driveflow.entity;

import com.driveflow.enums.MaintenanceStatus;
import com.driveflow.enums.MaintenanceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private BigDecimal cost;
    private String serviceProvider;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;
    private Integer mileageAtService;
    private LocalDate nextServiceDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Business logic
    public boolean isCompleted() {
        return status == MaintenanceStatus.COMPLETED;
    }

    public boolean isServiceDueSoon() {
        return nextServiceDue != null
                && nextServiceDue.isBefore(LocalDate.now().plusDays(30));
    }
}
