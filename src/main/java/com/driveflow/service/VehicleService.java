package com.driveflow.service;

import com.driveflow.entity.Vehicle;
import com.driveflow.enums.BookingStatus;
import com.driveflow.enums.VehicleStatus;
import com.driveflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> findAvailableVehicles(Long branchId, LocalDateTime pickupDate,
                                                LocalDateTime returnDate, Long categoryId) {
        if (pickupDate == null || returnDate == null) {
            throw new IllegalArgumentException("Pickup and return dates are required");
        }
        if (pickupDate.isAfter(returnDate)) {
            throw new IllegalArgumentException("Pickup date must be before return date");
        }
        return vehicleRepository.findAvailableVehicles(branchId, pickupDate, returnDate, categoryId,
                VehicleStatus.AVAILABLE, List.of(BookingStatus.CONFIRMED, BookingStatus.ACTIVE));
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void updateStatus(Long vehicleId, VehicleStatus status) {
        vehicleRepository.findById(vehicleId).ifPresent(v -> {
            v.setStatus(status);
            vehicleRepository.save(v);
        });
    }

    public List<Vehicle> findByBranch(Long branchId) {
        return vehicleRepository.findByBranchIdAndStatus(branchId, VehicleStatus.AVAILABLE);
    }

    public List<Vehicle> findFeaturedAvailable() {
        return vehicleRepository.findTop6ByStatusOrderByDailyRateAsc(VehicleStatus.AVAILABLE);
    }

    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status).size();
    }
}
