package com.driveflow.repository;
import com.driveflow.entity.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {
    List<VehicleCategory> findAll();
}
