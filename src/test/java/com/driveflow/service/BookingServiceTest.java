package com.driveflow.service;

import com.driveflow.entity.*;
import com.driveflow.enums.*;
import com.driveflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("בדיקות BookingService")
class BookingServiceTest {

    @Autowired private BookingService bookingService;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private BranchRepository branchRepository;
    @Autowired private VehicleCategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ExtraRepository extraRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Branch branch;
    private Vehicle vehicle;
    private Customer customer;
    private Extra gpsExtra;

    @BeforeEach
    void setUp() {
        branch = branchRepository.save(Branch.builder()
                .name("סניף בדיקה").city("תל אביב")
                .address("רחוב בדיקה 1").active(true).build());

        VehicleCategory category = categoryRepository.save(VehicleCategory.builder()
                .name("TestCat").depositAmount(new BigDecimal("500")).minAgeRequired(21).build());

        vehicle = vehicleRepository.save(Vehicle.builder()
                .licensePlate("99-BK-01").make("Toyota").model("Corolla").year(2024).color("לבן")
                .category(category).branch(branch)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).dailyRate(new BigDecimal("185"))
                .status(VehicleStatus.AVAILABLE).build());

        User user = userRepository.save(User.builder()
                .email("test.booking@driveflow.test")
                .password(passwordEncoder.encode("Test1234"))
                .firstName("בדיקה").lastName("לקוח")
                .role(UserRole.CUSTOMER).active(true).build());

        customer = customerRepository.save(Customer.builder()
                .user(user).customerType(CustomerType.REGULAR).loyaltyPoints(0)
                .licenseNumber("IL-TEST-01").licenseExpiry(LocalDate.of(2028, 12, 31))
                .licenseCountry("IL").addressCountry("ישראל").build());

        gpsExtra = extraRepository.save(Extra.builder()
                .name("GPS Test").dailyPrice(new BigDecimal("25"))
                .pricingType(PricingType.DAILY).active(true).build());
    }

    // ── TC-002: יצירת הזמנה ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-002a: יצירת הזמנה תקינה — נשמרת עם סטטוס PENDING")
    void createBooking_ValidData_ShouldCreatePendingBooking() {
        // Given
        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.plusDays(4);

        // When
        Booking booking = bookingService.createBooking(
                customer.getId(), vehicle.getId(),
                branch.getId(), branch.getId(),
                pickup, returnDate, null);

        // Then
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getBookingNumber()).startsWith("DRIVEFLOW-");
        assertThat(booking.getNumDays()).isEqualTo(4);
        assertThat(booking.getBasePrice()).isEqualByComparingTo(new BigDecimal("740.00")); // 185×4
    }

    @Test
    @DisplayName("TC-002b: מחיר הזמנה עם תוספות מחושב נכון")
    void createBooking_WithExtras_PriceIsCorrect() {
        // Given — 185×3 ימים + GPS 25×3 = 555 + 75 = 630
        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.plusDays(3);

        // When
        Booking booking = bookingService.createBooking(
                customer.getId(), vehicle.getId(),
                branch.getId(), branch.getId(),
                pickup, returnDate,
                java.util.List.of(gpsExtra.getId()));

        // Then
        assertThat(booking.getBasePrice()).isEqualByComparingTo(new BigDecimal("555.00"));
        assertThat(booking.getExtrasPrice()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(new BigDecimal("630.00"));
        assertThat(booking.getBookingExtras()).hasSize(1);
    }

    @Test
    @DisplayName("TC-002c: מספר הזמנה ייחודי לכל הזמנה")
    void createBooking_TwoBookings_HaveUniqueNumbers() {
        // Given
        LocalDateTime pickup1 = LocalDateTime.now().plusDays(5);
        LocalDateTime return1 = pickup1.plusDays(3);
        LocalDateTime pickup2 = LocalDateTime.now().plusDays(20);
        LocalDateTime return2 = pickup2.plusDays(3);

        // When
        Booking b1 = bookingService.createBooking(
                customer.getId(), vehicle.getId(), branch.getId(), branch.getId(), pickup1, return1, null);

        // Create second vehicle for second booking
        Vehicle vehicle2 = vehicleRepository.save(Vehicle.builder()
                .licensePlate("99-BK-02").make("Kia").model("Rio").year(2023).color("אפור")
                .category(vehicle.getCategory()).branch(branch)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).dailyRate(new BigDecimal("150"))
                .status(VehicleStatus.AVAILABLE).build());

        Booking b2 = bookingService.createBooking(
                customer.getId(), vehicle2.getId(), branch.getId(), branch.getId(), pickup2, return2, null);

        // Then
        assertThat(b1.getBookingNumber()).isNotEqualTo(b2.getBookingNumber());
        assertThat(b1.getBookingNumber()).matches("DRIVEFLOW-\\d{4}-\\d{5}");
        assertThat(b2.getBookingNumber()).matches("DRIVEFLOW-\\d{4}-\\d{5}");
    }

    // ── TC-003: ניהול הזמנה ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-003a: אישור הזמנה — סטטוס עובר ל-CONFIRMED")
    void confirmBooking_ShouldChangeStatusToConfirmed() {
        // Given
        Booking booking = createPendingBooking();

        // When
        Booking confirmed = bookingService.confirm(booking.getId());

        // Then
        assertThat(confirmed.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("TC-003b: ביטול הזמנה — סטטוס עובר ל-CANCELLED עם סיבה")
    void cancelBooking_ShouldCancelWithReason() {
        // Given
        Booking booking = createPendingBooking();
        String reason = "לקוח שינה תוכניות";

        // When
        Booking cancelled = bookingService.cancel(booking.getId(), reason);

        // Then
        assertThat(cancelled.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(cancelled.getCancellationReason()).isEqualTo(reason);
    }

    // ── TC-004: מסירה והחזרה ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-004a: מסירת רכב — סטטוס הזמנה ACTIVE, רכב RENTED")
    void markActive_ShouldSetActiveAndRentVehicle() {
        // Given
        Booking booking = bookingService.confirm(createPendingBooking().getId());

        // When
        Booking active = bookingService.markActive(booking.getId(), 15000);

        // Then
        assertThat(active.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(active.getPickupMileage()).isEqualTo(15000);
        assertThat(active.getVehicle().getStatus()).isEqualTo(VehicleStatus.RENTED);
    }

    @Test
    @DisplayName("TC-004b: החזרה תקינה — הזמנה COMPLETED, רכב AVAILABLE")
    void completeBooking_NoIssues_ShouldCompleteAndFreeVehicle() {
        // Given
        Booking active = bookingService.markActive(
                bookingService.confirm(createPendingBooking().getId()).getId(), 15000);

        // When
        Booking completed = bookingService.complete(active.getId(), 15900, false);

        // Then
        assertThat(completed.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        assertThat(completed.getReturnMileage()).isEqualTo(15900);
        assertThat(completed.getVehicle().getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    @DisplayName("TC-004c: החזרה עם נזק — הזמנה COMPLETED_WITH_DAMAGE, רכב MAINTENANCE")
    void completeBooking_WithDamage_ShouldSendToMaintenance() {
        // Given
        Booking active = bookingService.markActive(
                bookingService.confirm(createPendingBooking().getId()).getId(), 15000);

        // When
        Booking completed = bookingService.complete(active.getId(), 15900, true);

        // Then
        assertThat(completed.getStatus()).isEqualTo(BookingStatus.COMPLETED_WITH_DAMAGE);
        assertThat(completed.getVehicle().getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
    }

    @Test
    @DisplayName("TC-004d: החזרה מאוחרת — מחושב חיוב נוסף")
    void completeBooking_LateReturn_ShouldAddLateFee() {
        // Given — צור הזמנה עם מועד החזרה בעבר
        LocalDateTime pickup = LocalDateTime.now().minusDays(5);
        LocalDateTime returnDate = LocalDateTime.now().minusDays(1); // אמורה להיות אתמול

        Booking booking = bookingService.createBooking(
                customer.getId(), vehicle.getId(),
                branch.getId(), branch.getId(),
                pickup, returnDate, null);
        booking = bookingService.confirm(booking.getId());
        booking = bookingService.markActive(booking.getId(), 15000);

        BigDecimal dailyRate = vehicle.getDailyRate();

        // When — מחזיר עכשיו (יום מאוחר)
        Booking completed = bookingService.complete(booking.getId(), 15900, false);

        // Then
        assertThat(completed.getAdditionalCharges()).isGreaterThanOrEqualTo(dailyRate);
        assertThat(completed.getTotalPrice())
                .isGreaterThan(completed.getBasePrice());
    }

    // ── TC-005: isCancellable ────────────────────────────────────────────

    @Test
    @DisplayName("TC-005: הזמנה מאושרת לפני האיסוף — ניתנת לביטול")
    void isCancellable_ConfirmedBeforePickup_ShouldBeTrue() {
        // Given
        Booking booking = bookingService.confirm(createPendingBooking().getId());

        // When/Then
        assertThat(booking.isCancellable()).isTrue();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Booking createPendingBooking() {
        LocalDateTime pickup = LocalDateTime.now().plusDays(5);
        LocalDateTime returnDate = pickup.plusDays(3);
        return bookingService.createBooking(
                customer.getId(), vehicle.getId(),
                branch.getId(), branch.getId(),
                pickup, returnDate, null);
    }
}
