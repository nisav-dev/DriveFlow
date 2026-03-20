package com.driveflow.service;

import com.driveflow.entity.Branch;
import com.driveflow.entity.Vehicle;
import com.driveflow.entity.VehicleCategory;
import com.driveflow.enums.FuelType;
import com.driveflow.enums.TransmissionType;
import com.driveflow.enums.VehicleStatus;
import com.driveflow.repository.BranchRepository;
import com.driveflow.repository.VehicleCategoryRepository;
import com.driveflow.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("בדיקות VehicleService")
class VehicleServiceTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private VehicleCategoryRepository categoryRepository;

    private Branch testBranch;
    private VehicleCategory testCategory;

    @BeforeEach
    void setUp() {
        testBranch = branchRepository.save(Branch.builder()
                .name("סניף בדיקה").city("תל אביב")
                .address("רחוב בדיקה 1").active(true).build());

        testCategory = categoryRepository.save(VehicleCategory.builder()
                .name("TestCategory").depositAmount(new BigDecimal("500"))
                .minAgeRequired(21).build());
    }

    // ── TC-001: חיפוש רכבים זמינים ─────────────────────────────────────────

    @Test
    @DisplayName("TC-001a: חיפוש בסיסי — מחזיר רכבים זמינים מהסניף הנכון")
    void searchAvailableVehicles_ShouldReturnOnlyAvailableVehiclesFromBranch() {
        // Given
        Vehicle available = saveVehicle("11-TEST-01", VehicleStatus.AVAILABLE);
        Vehicle rented = saveVehicle("11-TEST-02", VehicleStatus.RENTED);

        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.plusDays(3);

        // When
        List<Vehicle> results = vehicleService.findAvailableVehicles(
                testBranch.getId(), pickup, returnDate, null);

        // Then
        assertThat(results).contains(available);
        assertThat(results).doesNotContain(rented);
        assertThat(results).allMatch(v -> v.getStatus() == VehicleStatus.AVAILABLE);
    }

    @Test
    @DisplayName("TC-001b: סינון לפי קטגוריה — מחזיר רק רכבים מהקטגוריה")
    void searchVehicles_WithCategoryFilter_ReturnsOnlyMatchingCategory() {
        // Given
        VehicleCategory otherCategory = categoryRepository.save(VehicleCategory.builder()
                .name("OtherCategory").depositAmount(new BigDecimal("600")).minAgeRequired(21).build());

        Vehicle vehicleInCategory = saveVehicle("22-TEST-01", VehicleStatus.AVAILABLE);
        Vehicle vehicleOtherCat = Vehicle.builder()
                .licensePlate("22-TEST-02").make("Honda").model("Civic").year(2023).color("לבן")
                .category(otherCategory).branch(testBranch)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).dailyRate(new BigDecimal("160"))
                .status(VehicleStatus.AVAILABLE).build();
        vehicleRepository.save(vehicleOtherCat);

        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.plusDays(3);

        // When
        List<Vehicle> results = vehicleService.findAvailableVehicles(
                testBranch.getId(), pickup, returnDate, testCategory.getId());

        // Then
        assertThat(results).contains(vehicleInCategory);
        assertThat(results).doesNotContain(vehicleOtherCat);
        assertThat(results).allMatch(v -> v.getCategory().getId().equals(testCategory.getId()));
    }

    @Test
    @DisplayName("TC-001c: תאריך החזרה לפני תאריך איסוף — זורק IllegalArgumentException")
    void searchVehicles_ReturnBeforePickup_ThrowsException() {
        // Given
        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.minusDays(1); // לפני האיסוף!

        // When/Then
        assertThatThrownBy(() ->
                vehicleService.findAvailableVehicles(testBranch.getId(), pickup, returnDate, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-001d: עדכון סטטוס רכב — משתנה בהצלחה")
    void updateVehicleStatus_ShouldPersistChange() {
        // Given
        Vehicle vehicle = saveVehicle("33-TEST-01", VehicleStatus.AVAILABLE);

        // When
        vehicleService.updateStatus(vehicle.getId(), VehicleStatus.MAINTENANCE);

        // Then
        Vehicle updated = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
    }

    @Test
    @DisplayName("TC-001e: ספירת רכבים לפי סטטוס — מחזיר מספר נכון")
    void countByStatus_ShouldReturnCorrectCount() {
        // Given
        saveVehicle("44-TEST-01", VehicleStatus.AVAILABLE);
        saveVehicle("44-TEST-02", VehicleStatus.AVAILABLE);
        saveVehicle("44-TEST-03", VehicleStatus.MAINTENANCE);

        // When
        long available = vehicleService.countByStatus(VehicleStatus.AVAILABLE);
        long maintenance = vehicleService.countByStatus(VehicleStatus.MAINTENANCE);

        // Then
        assertThat(available).isGreaterThanOrEqualTo(2);
        assertThat(maintenance).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("TC-001f: חישוב מחיר השכרה — נכון לפי ימים ומחיר יומי")
    void vehicleCalculateRentalPrice_ShouldMultiplyDailyRateByDays() {
        // Given
        Vehicle vehicle = saveVehicle("55-TEST-01", VehicleStatus.AVAILABLE);
        // dailyRate = ₪185, days = 5

        // When
        BigDecimal price = vehicle.calculateRentalPrice(5);

        // Then
        assertThat(price).isEqualByComparingTo(new BigDecimal("925.00"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Vehicle saveVehicle(String plate, VehicleStatus status) {
        return vehicleRepository.save(Vehicle.builder()
                .licensePlate(plate).make("Toyota").model("Corolla").year(2024).color("לבן")
                .category(testCategory).branch(testBranch)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).dailyRate(new BigDecimal("185"))
                .status(status).build());
    }
}
