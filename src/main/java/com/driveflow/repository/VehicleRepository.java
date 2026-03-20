package com.driveflow.repository;

import com.driveflow.entity.Vehicle;
import com.driveflow.enums.BookingStatus;
import com.driveflow.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByBranchIdAndStatus(Long branchId, VehicleStatus status);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findTop6ByStatusOrderByDailyRateAsc(VehicleStatus status);

    @Query("""
        SELECT v FROM Vehicle v
        WHERE (:branchId IS NULL OR v.branch.id = :branchId)
          AND v.status = :availableStatus
          AND (:categoryId IS NULL OR v.category.id = :categoryId)
          AND v.id NOT IN (
              SELECT b.vehicle.id FROM Booking b
              WHERE b.status IN :activeStatuses
                AND b.pickupDatetime < :returnDate
                AND b.returnDatetime > :pickupDate
          )
        ORDER BY v.dailyRate ASC
    """)
    List<Vehicle> findAvailableVehicles(
            @Param("branchId") Long branchId,
            @Param("pickupDate") LocalDateTime pickupDate,
            @Param("returnDate") LocalDateTime returnDate,
            @Param("categoryId") Long categoryId,
            @Param("availableStatus") VehicleStatus availableStatus,
            @Param("activeStatuses") List<BookingStatus> activeStatuses
    );
}
