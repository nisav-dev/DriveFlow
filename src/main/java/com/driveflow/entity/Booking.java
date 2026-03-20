package com.driveflow.entity;

import com.driveflow.enums.BookingStatus;
import com.driveflow.enums.FuelLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_branch_id", nullable = false)
    private Branch pickupBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_branch_id", nullable = false)
    private Branch returnBranch;

    @Column(nullable = false)
    private LocalDateTime pickupDatetime;

    @Column(nullable = false)
    private LocalDateTime returnDatetime;

    private LocalDateTime actualReturnDatetime;

    @Column(nullable = false)
    private int numDays;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private BigDecimal extrasPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal additionalCharges = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    private Integer pickupMileage;
    private Integer returnMileage;

    @Enumerated(EnumType.STRING)
    private FuelLevel pickupFuelLevel;

    @Enumerated(EnumType.STRING)
    private FuelLevel returnFuelLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingExtra> bookingExtras = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DamageReport> damageReports = new ArrayList<>();

    // Business logic
    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public boolean isCancellable() {
        return (status == BookingStatus.CONFIRMED || status == BookingStatus.PENDING)
                && LocalDateTime.now().isBefore(pickupDatetime);
    }

    public boolean isLateReturn() {
        return actualReturnDatetime != null
                && actualReturnDatetime.isAfter(returnDatetime);
    }

    public int getExtraDaysCount() {
        if (!isLateReturn()) return 0;
        long hours = Duration.between(returnDatetime, actualReturnDatetime).toHours();
        return (int) Math.ceil(hours / 24.0);
    }

    public BigDecimal calculateLateFee() {
        return vehicle.getDailyRate().multiply(BigDecimal.valueOf(getExtraDaysCount()));
    }

    public boolean hasDamages() {
        return !damageReports.isEmpty();
    }

    public void addBookingExtra(BookingExtra extra) {
        bookingExtras.add(extra);
        extra.setBooking(this);
    }
}
