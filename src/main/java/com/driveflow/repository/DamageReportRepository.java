package com.driveflow.repository;
import com.driveflow.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    List<DamageReport> findByVehicleId(Long vehicleId);
    List<DamageReport> findByBookingId(Long bookingId);
}
