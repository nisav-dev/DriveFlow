package com.driveflow.repository;
import com.driveflow.entity.MaintenanceRecord;
import com.driveflow.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByVehicleId(Long vehicleId);
    List<MaintenanceRecord> findByStatus(MaintenanceStatus status);
}
