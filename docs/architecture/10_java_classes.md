# DriveFlow — Java Classes Design

---

## עקרונות ארכיטקטורה

- **Separation of Concerns** — Entity / DTO / Service / Controller / Repository
- **Encapsulation** — כל שדה private, גישה דרך getters/setters
- **Composition** — BookingService משתמש ב-VehicleService, PaymentService
- **Collections** — שימוש ב-List\<T\> ב-Services וב-OneToMany

---

## 1. Entity: `User`

```java
@Entity
@Table(name = "users")
public class User {

    // Fields
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // BCrypt encoded

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;  // CUSTOMER, AGENT, ADMIN

    @Column(nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Composition
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Customer customer;

    // Methods
    public String getFullName() { return firstName + " " + lastName; }
    public boolean isAdmin() { return role == UserRole.ADMIN; }
    public boolean isAgent() { return role == UserRole.AGENT || role == UserRole.ADMIN; }
}
```

---

## 2. Entity: `Customer`

```java
@Entity
@Table(name = "customers")
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String idNumber;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String licenseCountry;
    private String addressStreet;
    private String addressCity;
    private String addressCountry;
    private int loyaltyPoints;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;  // REGULAR, BUSINESS, VIP

    // Composition - Collections
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // Business Logic Methods
    public boolean hasValidLicense() {
        return licenseExpiry != null && licenseExpiry.isAfter(LocalDate.now());
    }

    public int getTotalBookingsCount() {
        return bookings.size();
    }

    public BigDecimal getTotalAmountSpent() {
        return bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .map(Booking::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }
}
```

---

## 3. Entity: `VehicleCategory`

```java
@Entity
@Table(name = "vehicle_categories")
public class VehicleCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    private String icon;
    private int minAgeRequired;
    private BigDecimal depositAmount;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();

    // Methods
    public int getAvailableVehiclesCount() {
        return (int) vehicles.stream()
            .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
            .count();
    }
}
```

---

## 4. Entity: `Vehicle`

```java
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    private int year;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private VehicleCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;  // MANUAL, AUTOMATIC

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;  // PETROL, DIESEL, HYBRID, ELECTRIC

    private int numSeats;
    private int numDoors;
    private int luggageCapacity;
    private boolean hasAc;
    private boolean hasBluetooth;
    private boolean hasGpsBuiltin;

    @Column(nullable = false)
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;  // AVAILABLE, RENTED, MAINTENANCE, OUT_OF_SERVICE

    private int mileage;
    private String mainImageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Composition
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    // Business Logic Methods
    public boolean isAvailable() { return status == VehicleStatus.AVAILABLE; }

    public String getDisplayName() { return year + " " + make + " " + model; }

    public BigDecimal calculateRentalPrice(int days) {
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    public void updateMileage(int newMileage) {
        if (newMileage > this.mileage) {
            this.mileage = newMileage;
        }
    }
}
```

---

## 5. Entity: `Booking`

```java
@Entity
@Table(name = "bookings")
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private int numDays;
    private BigDecimal basePrice;
    private BigDecimal extrasPrice;
    private BigDecimal additionalCharges;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private Integer pickupMileage;
    private Integer returnMileage;

    @Enumerated(EnumType.STRING)
    private FuelLevel pickupFuelLevel;

    @Enumerated(EnumType.STRING)
    private FuelLevel returnFuelLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    private String notes;
    private String cancellationReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Composition - Collections
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingExtra> bookingExtras = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<DamageReport> damageReports = new ArrayList<>();

    // Business Logic Methods
    public boolean isActive() { return status == BookingStatus.ACTIVE; }
    public boolean isCancellable() {
        return status == BookingStatus.CONFIRMED &&
               LocalDateTime.now().isBefore(pickupDatetime);
    }

    public boolean isLatReturn() {
        return actualReturnDatetime != null &&
               actualReturnDatetime.isAfter(returnDatetime);
    }

    public int getExtraDaysCount() {
        if (!isLatReturn()) return 0;
        long hours = java.time.Duration.between(returnDatetime, actualReturnDatetime).toHours();
        return (int) Math.ceil(hours / 24.0);
    }

    public BigDecimal calculateLateFee() {
        return vehicle.getDailyRate().multiply(BigDecimal.valueOf(getExtraDaysCount()));
    }

    public void addBookingExtra(BookingExtra extra) {
        bookingExtras.add(extra);
        extra.setBooking(this);
    }

    public boolean hasDamages() { return !damageReports.isEmpty(); }
}
```

---

## 6. Entity: `Payment`

```java
@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "ILS";

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;  // CREDIT_CARD, PAYPAL, BANK_TRANSFER

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;  // INITIAL, ADDITIONAL, REFUND

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;  // PENDING, SUCCESS, FAILED, REFUNDED

    @Column(unique = true)
    private String transactionId;

    private String cardLastFour;

    @Column(nullable = false)
    private LocalDateTime paymentDatetime;

    private String notes;

    // Business Logic
    public boolean isSuccessful() { return status == PaymentStatus.SUCCESS; }

    public static String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" +
               (int)(Math.random() * 10000);
    }
}
```

---

## 7. Entity: `Extra`

```java
@Entity
@Table(name = "extras")
public class Extra {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal dailyPrice;
    private BigDecimal fixedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType pricingType;  // DAILY, FIXED

    private String icon;
    private boolean isActive = true;

    // Business Logic
    public BigDecimal calculatePrice(int days) {
        if (pricingType == PricingType.FIXED) {
            return fixedPrice;
        }
        return dailyPrice.multiply(BigDecimal.valueOf(days));
    }
}
```

---

## 8. Entity: `MaintenanceRecord`

```java
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private MaintenanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Business Logic
    public boolean isCompleted() { return status == MaintenanceStatus.COMPLETED; }

    public boolean isServiceDueSoon() {
        return nextServiceDue != null &&
               nextServiceDue.isBefore(LocalDate.now().plusDays(30));
    }
}
```

---

## 9. Entity: `DamageReport`

```java
@Entity
@Table(name = "damage_reports")
public class DamageReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageType damageType;

    private String damageLocation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private boolean customerFault = false;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageStatus status;

    // Business Logic
    public BigDecimal getChargeableAmount() {
        if (!customerFault) return BigDecimal.ZERO;
        return actualCost != null ? actualCost :
               (estimatedCost != null ? estimatedCost : BigDecimal.ZERO);
    }
}
```

---

## 10. Service Classes

### `BookingService`
```java
@Service
@Transactional
public class BookingService {

    // Dependencies (Composition)
    private final BookingRepository bookingRepository;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final EmailService emailService;

    // Business Methods
    public List<Vehicle> searchAvailableVehicles(Long branchId,
                                                  LocalDateTime pickupDate,
                                                  LocalDateTime returnDate) {...}

    public Booking createBooking(BookingCreateDTO dto, Long customerId) {...}

    public Booking confirmBooking(Long bookingId, PaymentDTO paymentDTO) {...}

    public Booking pickupVehicle(Long bookingId, VehiclePickupDTO dto) {...}

    public Booking returnVehicle(Long bookingId, VehicleReturnDTO dto) {...}

    public Booking cancelBooking(Long bookingId, String reason) {...}

    public BigDecimal calculateCancellationFee(Booking booking) {...}

    private String generateBookingNumber() {...}
}
```

### `VehicleService`
```java
@Service
public class VehicleService {

    public List<Vehicle> findAvailableVehicles(Long branchId,
                                                LocalDateTime from,
                                                LocalDateTime to,
                                                Long categoryId) {...}

    public boolean isVehicleAvailable(Long vehicleId,
                                       LocalDateTime from,
                                       LocalDateTime to) {...}

    public Vehicle addVehicle(VehicleCreateDTO dto) {...}
    public Vehicle updateVehicleStatus(Long vehicleId, VehicleStatus status) {...}
    public List<Vehicle> findVehiclesByBranch(Long branchId) {...}
}
```

---

## Enums

```java
public enum UserRole { CUSTOMER, AGENT, ADMIN }
public enum CustomerType { REGULAR, BUSINESS, VIP }
public enum VehicleStatus { AVAILABLE, RENTED, MAINTENANCE, OUT_OF_SERVICE }
public enum TransmissionType { MANUAL, AUTOMATIC }
public enum FuelType { PETROL, DIESEL, HYBRID, ELECTRIC }
public enum FuelLevel { FULL, THREE_QUARTERS, HALF, QUARTER, EMPTY }
public enum BookingStatus { PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED, COMPLETED_WITH_DAMAGE }
public enum PaymentMethod { CREDIT_CARD, PAYPAL, BANK_TRANSFER }
public enum PaymentType { INITIAL, ADDITIONAL, REFUND }
public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
public enum PricingType { DAILY, FIXED }
public enum MaintenanceType { SCHEDULED_SERVICE, REPAIR, CLEANING, INSPECTION, TIRE_CHANGE, OTHER }
public enum MaintenanceStatus { SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }
public enum DamageType { SCRATCH, DENT, BROKEN_GLASS, INTERIOR_DAMAGE, MECHANICAL, OTHER }
public enum DamageStatus { REPORTED, UNDER_REVIEW, REPAIR_ORDERED, REPAIRED, CLOSED }
```

---

*גרסה: 1.0 | DriveFlow — Java Classes Design*
