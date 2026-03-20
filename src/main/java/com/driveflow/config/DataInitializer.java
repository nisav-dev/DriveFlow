package com.driveflow.config;

import com.driveflow.entity.*;
import com.driveflow.enums.*;
import com.driveflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final VehicleRepository vehicleRepository;
    private final ExtraRepository extraRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (vehicleRepository.count() > 0) return; // core fleet already seeded

        // ── Branches ──────────────────────────────────────────────────────────────
        Branch natbag = branchRepository.save(Branch.builder()
                .name("נתב\"ג — שדה התעופה").city("לוד")
                .address("טרמינל 3, שדה התעופה בן-גוריון, לוד")
                .phone("03-9750001").email("natbag@driveflow.co.il")
                .openingHours("פתוח 24/7")
                .latitude(32.0055).longitude(34.8854)
                .airportBranch(true).active(true).build());

        Branch tlv = branchRepository.save(Branch.builder()
                .name("תל אביב — מרכז").city("תל אביב")
                .address("רחוב דיזנגוף 50, תל אביב")
                .phone("03-9750002").email("tlv@driveflow.co.il")
                .openingHours("א׳–ה׳ 08:00–20:00, ו׳ 08:00–14:00")
                .latitude(32.0853).longitude(34.7818)
                .active(true).build());

        Branch jlm = branchRepository.save(Branch.builder()
                .name("ירושלים — מרכז העיר").city("ירושלים")
                .address("רחוב יפו 120, ירושלים")
                .phone("02-9750003").email("jlm@driveflow.co.il")
                .openingHours("א׳–ה׳ 08:00–20:00, ו׳ 08:00–13:00")
                .latitude(31.7829).longitude(35.2007)
                .active(true).build());

        Branch haifa = branchRepository.save(Branch.builder()
                .name("חיפה — נמל").city("חיפה")
                .address("שדרות בן-גוריון 10, חיפה")
                .phone("04-9750004").email("haifa@driveflow.co.il")
                .openingHours("א׳–ה׳ 08:00–19:00, ו׳ 08:00–14:00")
                .latitude(32.8156).longitude(34.9869)
                .active(true).build());

        Branch beersheva = branchRepository.save(Branch.builder()
                .name("באר שבע — מרכז").city("באר שבע")
                .address("רחוב הנשיא 1, באר שבע")
                .phone("08-9750005").email("bs@driveflow.co.il")
                .openingHours("א׳–ה׳ 08:00–19:00, ו׳ 08:00–14:00")
                .latitude(31.2530).longitude(34.7915)
                .active(true).build());

        Branch eilat = branchRepository.save(Branch.builder()
                .name("אילת — שדה תעופה").city("אילת")
                .address("שדה התעופה אילת, אילת")
                .phone("08-9750006").email("eilat@driveflow.co.il")
                .openingHours("א׳–ה׳ 07:00–21:00, ו׳ 07:00–15:00")
                .latitude(29.5613).longitude(34.9587)
                .airportBranch(true).active(true).build());

        Branch nazareth = branchRepository.save(Branch.builder()
                .name("נצרת — מרכז").city("נצרת")
                .address("רחוב פאולוס השישי 50, נצרת")
                .phone("04-9750007").email("nazareth@driveflow.co.il")
                .openingHours("א׳–ה׳ 08:00–18:00, ו׳ 08:00–14:00")
                .latitude(32.7021).longitude(35.2978)
                .active(true).build());

        // ── Vehicle Categories ─────────────────────────────────────────────────
        VehicleCategory economy = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Economy").description("רכבים חסכוניים לנסיעה עירונית יומיומית")
                .icon("bi-car-front").minAgeRequired(21).depositAmount(new BigDecimal("500")).build());

        VehicleCategory compact = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Compact").description("רכבים קומפקטיים מרווחים לנסיעות בין-עירוניות")
                .icon("bi-car-front-fill").minAgeRequired(21).depositAmount(new BigDecimal("700")).build());

        VehicleCategory suv = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("SUV").description("רכבי שטח וSUV לכל שטח ולמשפחות")
                .icon("bi-truck").minAgeRequired(23).depositAmount(new BigDecimal("1500")).build());

        VehicleCategory mini = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Mini").description("רכבים מיני קלים לחניה עירונית")
                .icon("bi-car-front").minAgeRequired(21).depositAmount(new BigDecimal("400")).build());

        VehicleCategory luxury = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Luxury").description("רכבי יוקרה לאירועים ונסיעות מיוחדות")
                .icon("bi-star").minAgeRequired(25).depositAmount(new BigDecimal("3000")).build());

        VehicleCategory van = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Van").description("מיניוואנים ורכבי מטען קלים")
                .icon("bi-truck-front").minAgeRequired(23).depositAmount(new BigDecimal("1200")).build());

        VehicleCategory electric = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Electric").description("רכבים חשמליים ידידותיים לסביבה")
                .icon("bi-lightning-charge").minAgeRequired(21).depositAmount(new BigDecimal("800")).build());

        VehicleCategory premium = vehicleCategoryRepository.save(VehicleCategory.builder()
                .name("Premium").description("רכבים פרמיום לנסיעות עסקיות")
                .icon("bi-briefcase").minAgeRequired(23).depositAmount(new BigDecimal("2000")).build());

        // ── Vehicles ───────────────────────────────────────────────────────────
        vehicleRepository.save(Vehicle.builder().licensePlate("11-222-33").make("Hyundai").model("i10")
                .year(2023).color("לבן").category(mini).branch(tlv)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(4).numDoors(5).luggageCapacity(1).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("120")).status(VehicleStatus.AVAILABLE).mileage(12000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("22-333-44").make("Hyundai").model("i20")
                .year(2024).color("אפור").category(economy).branch(natbag)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).luggageCapacity(2).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("150")).status(VehicleStatus.AVAILABLE).mileage(8500).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("33-444-55").make("Toyota").model("Corolla")
                .year(2024).color("שחור").category(compact).branch(natbag)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).luggageCapacity(2).hasAc(true).hasBluetooth(true).hasGpsBuiltin(true)
                .dailyRate(new BigDecimal("185")).status(VehicleStatus.AVAILABLE).mileage(15000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("44-555-66").make("Toyota").model("RAV4")
                .year(2023).color("כסף").category(suv).branch(jlm)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.HYBRID)
                .numSeats(5).numDoors(5).luggageCapacity(4).hasAc(true).hasBluetooth(true).hasGpsBuiltin(true)
                .dailyRate(new BigDecimal("280")).status(VehicleStatus.AVAILABLE).mileage(22000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("55-666-77").make("Kia").model("Sportage")
                .year(2024).color("כחול").category(suv).branch(haifa)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).luggageCapacity(4).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("260")).status(VehicleStatus.AVAILABLE).mileage(5000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("66-777-88").make("BMW").model("5 Series")
                .year(2024).color("שחור").category(luxury).branch(tlv)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(4).luggageCapacity(3).hasAc(true).hasBluetooth(true).hasGpsBuiltin(true)
                .dailyRate(new BigDecimal("650")).status(VehicleStatus.AVAILABLE).mileage(3000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("77-888-99").make("Tesla").model("Model 3")
                .year(2024).color("לבן").category(electric).branch(tlv)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.ELECTRIC)
                .numSeats(5).numDoors(4).luggageCapacity(3).hasAc(true).hasBluetooth(true).hasGpsBuiltin(true)
                .dailyRate(new BigDecimal("320")).status(VehicleStatus.AVAILABLE).mileage(9000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("88-999-00").make("Volkswagen").model("Transporter")
                .year(2022).color("לבן").category(van).branch(beersheva)
                .transmission(TransmissionType.MANUAL).fuelType(FuelType.DIESEL)
                .numSeats(8).numDoors(5).luggageCapacity(8).hasAc(true).hasBluetooth(false)
                .dailyRate(new BigDecimal("350")).status(VehicleStatus.AVAILABLE).mileage(45000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("99-000-11").make("Mercedes").model("C-Class")
                .year(2023).color("כסף").category(premium).branch(natbag)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(4).luggageCapacity(3).hasAc(true).hasBluetooth(true).hasGpsBuiltin(true)
                .dailyRate(new BigDecimal("450")).status(VehicleStatus.AVAILABLE).mileage(18000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("10-100-22").make("Suzuki").model("Swift")
                .year(2023).color("אדום").category(mini).branch(jlm)
                .transmission(TransmissionType.MANUAL).fuelType(FuelType.PETROL)
                .numSeats(4).numDoors(5).luggageCapacity(1).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("110")).status(VehicleStatus.AVAILABLE).mileage(20000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("11-200-33").make("Mazda").model("CX-5")
                .year(2024).color("ירוק").category(suv).branch(eilat)
                .transmission(TransmissionType.AUTOMATIC).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).luggageCapacity(4).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("290")).status(VehicleStatus.AVAILABLE).mileage(7000).build());

        vehicleRepository.save(Vehicle.builder().licensePlate("12-300-44").make("Ford").model("Focus")
                .year(2022).color("כחול").category(compact).branch(nazareth)
                .transmission(TransmissionType.MANUAL).fuelType(FuelType.PETROL)
                .numSeats(5).numDoors(5).luggageCapacity(2).hasAc(true).hasBluetooth(true)
                .dailyRate(new BigDecimal("160")).status(VehicleStatus.AVAILABLE).mileage(33000).build());

        // ── Extras ─────────────────────────────────────────────────────────────
        extraRepository.save(Extra.builder().name("GPS ניווט").description("מכשיר ניווט GPS נייד")
                .dailyPrice(new BigDecimal("25")).pricingType(PricingType.DAILY)
                .icon("bi-geo-alt").active(true).build());

        extraRepository.save(Extra.builder().name("כיסא תינוק").description("כיסא בטיחות לתינוקות 0-13 ק\"ג")
                .dailyPrice(new BigDecimal("30")).pricingType(PricingType.DAILY)
                .icon("bi-person-arms-up").active(true).build());

        extraRepository.save(Extra.builder().name("כיסא ילדים").description("כיסא בטיחות לילדים 9-36 ק\"ג")
                .dailyPrice(new BigDecimal("25")).pricingType(PricingType.DAILY)
                .icon("bi-person").active(true).build());

        extraRepository.save(Extra.builder().name("כיסא נוסף").description("כיסא מתקפל לנוסע נוסף (לרכבי ואן)")
                .dailyPrice(new BigDecimal("15")).pricingType(PricingType.DAILY)
                .icon("bi-plus-circle").active(true).build());

        extraRepository.save(Extra.builder().name("ביטוח מורחב").description("ביטוח אפס דדקטיבל לכל נזק")
                .dailyPrice(new BigDecimal("45")).pricingType(PricingType.DAILY)
                .icon("bi-shield-check").active(true).build());

        extraRepository.save(Extra.builder().name("מנוי תדלוק").description("שירות מילוי דלק מלא בהחזרה")
                .fixedPrice(new BigDecimal("150")).pricingType(PricingType.FIXED)
                .icon("bi-fuel-pump").active(true).build());

        // ── Admin User ─────────────────────────────────────────────────────────
        userRepository.findByEmailIgnoreCase("admin@driveflow.co.il").orElseGet(() ->
                userRepository.save(User.builder()
                        .email("admin@driveflow.co.il")
                        .password(passwordEncoder.encode("Admin1234"))
                        .firstName("מנהל").lastName("מערכת")
                        .phone("03-9750000")
                        .role(UserRole.ADMIN)
                        .active(true).build()));

        // ── Agent User ─────────────────────────────────────────────────────────
        userRepository.findByEmailIgnoreCase("agent@driveflow.co.il").orElseGet(() ->
                userRepository.save(User.builder()
                        .email("agent@driveflow.co.il")
                        .password(passwordEncoder.encode("Agent1234"))
                        .firstName("דנה").lastName("לוי")
                        .phone("03-9750010")
                        .role(UserRole.AGENT)
                        .active(true).build()));

        // ── Sample Customer ────────────────────────────────────────────────────
        User customerUser = userRepository.findByEmailIgnoreCase("yossi@example.com").orElseGet(() ->
                userRepository.save(User.builder()
                        .email("yossi@example.com")
                        .password(passwordEncoder.encode("Yossi1234"))
                        .firstName("יוסי").lastName("כהן")
                        .phone("050-1234567")
                        .role(UserRole.CUSTOMER)
                        .active(true).build()));

        if (customerRepository.findByUserId(customerUser.getId()).isEmpty()) {
            customerRepository.save(Customer.builder()
                    .user(customerUser)
                    .idNumber("123456789")
                    .licenseNumber("IL-987654")
                    .licenseExpiry(java.time.LocalDate.of(2028, 6, 30))
                    .licenseCountry("IL")
                    .addressStreet("רחוב הרצל 10").addressCity("תל אביב").addressCountry("ישראל")
                    .loyaltyPoints(0)
                    .customerType(CustomerType.REGULAR)
                    .build());
        }

        // ── Sample Booking ─────────────────────────────────────────────────────
        Customer customer = customerRepository.findAll().stream()
                .filter(c -> c.getUser().getEmail().equals("yossi@example.com"))
                .findFirst().orElseThrow();
        Vehicle corolla = vehicleRepository.findAll().stream()
                .filter(v -> v.getModel().equals("Corolla"))
                .findFirst().orElseThrow();

        if (bookingRepository.findByBookingNumber("DRIVEFLOW-2026-00001").isEmpty()) {
            Booking booking = Booking.builder()
                    .bookingNumber("DRIVEFLOW-2026-00001")
                    .customer(customer)
                    .vehicle(corolla)
                    .pickupBranch(natbag)
                    .returnBranch(natbag)
                    .pickupDatetime(LocalDateTime.now().plusDays(5))
                    .returnDatetime(LocalDateTime.now().plusDays(10))
                    .numDays(5)
                    .basePrice(corolla.calculateRentalPrice(5))
                    .extrasPrice(BigDecimal.ZERO)
                    .additionalCharges(BigDecimal.ZERO)
                    .totalPrice(corolla.calculateRentalPrice(5))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }
    }
}
