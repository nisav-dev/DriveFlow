# DriveFlow — QA Tests

---

## 1. בדיקות חיפוש רכבים

### TC-001: חיפוש רכב בסיסי

**מטרה:** וידוא שחיפוש עם פרמטרים תקינים מחזיר תוצאות נכונות

```java
@SpringBootTest
@AutoConfigureMockMvc
class VehicleSearchServiceTest {

    @Autowired
    private VehicleService vehicleService;

    @Test
    @DisplayName("חיפוש רכבים זמינים — מחזיר רכבים תקינים")
    void searchAvailableVehicles_ShouldReturnAvailableVehicles() {
        // Given
        Long branchId = 1L;
        LocalDateTime pickup = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 4, 15, 10, 0);

        // When
        List<Vehicle> results = vehicleService.findAvailableVehicles(
            branchId, pickup, returnDate, null
        );

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(v -> v.getStatus() == VehicleStatus.AVAILABLE);
        assertThat(results).allMatch(v -> v.getBranch().getId().equals(branchId));
    }

    @Test
    @DisplayName("חיפוש — לא מחזיר רכבים מוזמנים בתאריכים")
    void searchVehicles_ShouldExcludeBookedVehicles() {
        // Given — רכב עם הזמנה פעילה בתאריכים 10-15 אפריל
        Long vehicleId = 5L;
        LocalDateTime pickup = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 4, 15, 10, 0);

        // When
        List<Vehicle> results = vehicleService.findAvailableVehicles(
            1L, pickup, returnDate, null
        );

        // Then
        assertThat(results)
            .noneMatch(v -> v.getId().equals(vehicleId));
    }

    @Test
    @DisplayName("סינון לפי קטגוריה מחזיר רק רכבים מהקטגוריה")
    void searchVehicles_WithCategoryFilter_ReturnsCorrectCategory() {
        // Given
        Long categoryId = 6L; // SUV
        LocalDateTime pickup = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 4, 12, 10, 0);

        // When
        List<Vehicle> results = vehicleService.findAvailableVehicles(
            1L, pickup, returnDate, categoryId
        );

        // Then
        assertThat(results)
            .allMatch(v -> v.getCategory().getId().equals(categoryId));
    }

    @Test
    @DisplayName("חיפוש עם תאריך החזרה לפני תאריך איסוף — זורק Exception")
    void searchVehicles_InvalidDates_ThrowsException() {
        // Given
        LocalDateTime pickup = LocalDateTime.of(2026, 4, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 4, 10, 10, 0); // לפני!

        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
            vehicleService.findAvailableVehicles(1L, pickup, returnDate, null)
        );
    }
}
```

---

## 2. בדיקות יצירת הזמנה

### TC-002: יצירת הזמנה תקינה

```java
@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("יצירת הזמנה תקינה — נשמרת עם סטטוס PENDING")
    void createBooking_ValidData_ShouldCreatePendingBooking() {
        // Given
        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setVehicleId(3L);
        dto.setPickupBranchId(1L);
        dto.setReturnBranchId(1L);
        dto.setPickupDatetime(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setReturnDatetime(LocalDateTime.of(2026, 5, 5, 10, 0));
        dto.setExtraIds(List.of(1L, 3L)); // GPS + ביטוח מורחב

        Long customerId = 1L;

        // When
        Booking booking = bookingService.createBooking(dto, customerId);

        // Then
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getBookingNumber()).startsWith("DRIVEFLOW-2026-");
        assertThat(booking.getNumDays()).isEqualTo(4);
        assertThat(booking.getBookingExtras()).hasSize(2);
    }

    @Test
    @DisplayName("מחיר הזמנה מחושב נכון")
    void createBooking_PriceCalculation_ShouldBeCorrect() {
        // Given — רכב ב-₪185/יום, GPS ₪30/יום, 4 ימים
        BookingCreateDTO dto = buildBookingDTO(3L, 4, List.of(1L)); // vehicleId=3, GPS

        // When
        Booking booking = bookingService.createBooking(dto, 1L);

        // Then
        assertThat(booking.getBasePrice()).isEqualByComparingTo("740.00"); // 185×4
        assertThat(booking.getExtrasPrice()).isEqualByComparingTo("120.00"); // 30×4
        assertThat(booking.getTotalPrice()).isEqualByComparingTo("860.00");
    }

    @Test
    @DisplayName("הזמנה לרכב לא זמין — זורקת VehicleNotAvailableException")
    void createBooking_VehicleNotAvailable_ShouldThrowException() {
        // Given — רכב 5 כבר מוזמן בתאריכים אלה
        BookingCreateDTO dto = buildBookingDTOForBookedVehicle(5L);

        // When/Then
        assertThrows(VehicleNotAvailableException.class, () ->
            bookingService.createBooking(dto, 1L)
        );
    }

    @Test
    @DisplayName("מספר הזמנה ייחודי בכל הזמנה")
    void createBooking_BookingNumberIsUnique() {
        // Given
        BookingCreateDTO dto1 = buildValidBookingDTO(3L);
        BookingCreateDTO dto2 = buildValidBookingDTO(4L);

        // When
        Booking booking1 = bookingService.createBooking(dto1, 1L);
        Booking booking2 = bookingService.createBooking(dto2, 2L);

        // Then
        assertThat(booking1.getBookingNumber())
            .isNotEqualTo(booking2.getBookingNumber());
    }
}
```

---

## 3. בדיקות תשלום

### TC-003: עיבוד תשלום

```java
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Test
    @DisplayName("תשלום מאושר — הזמנה עוברת ל-CONFIRMED")
    void processPayment_Success_ShouldConfirmBooking() {
        // Given
        Long bookingId = createPendingBooking();
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentDTO.setCardLastFour("1234");
        paymentDTO.setSimulateSuccess(true);

        // When
        Payment payment = paymentService.processPayment(bookingId, paymentDTO);
        Booking booking = bookingService.findById(bookingId);

        // Then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getTransactionId()).startsWith("TXN-");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("תשלום נכשל — הזמנה נשארת PENDING")
    void processPayment_Failure_ShouldKeepPendingStatus() {
        // Given
        Long bookingId = createPendingBooking();
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSimulateSuccess(false);

        // When
        Payment payment = paymentService.processPayment(bookingId, paymentDTO);
        Booking booking = bookingService.findById(bookingId);

        // Then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("ביטול הזמנה — יוצר תשלום REFUND")
    void cancelBooking_ShouldCreateRefundPayment() {
        // Given
        Long bookingId = createConfirmedBooking();

        // When
        bookingService.cancelBooking(bookingId, "לקוח שינה תוכניות");

        // Then
        List<Payment> payments = paymentService.findByBookingId(bookingId);
        assertThat(payments)
            .anyMatch(p -> p.getPaymentType() == PaymentType.REFUND &&
                          p.getStatus() == PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("דמי ביטול מחושבים נכון לפי מדיניות")
    void calculateCancellationFee_LessThan48Hours_ShouldCharge50Percent() {
        // Given — הזמנה שהאיסוף בעוד פחות מ-48 שעות
        Booking booking = createBookingWithPickupIn24Hours();

        // When
        BigDecimal fee = bookingService.calculateCancellationFee(booking);

        // Then — 50% מהסכום הכולל
        BigDecimal expected = booking.getTotalPrice()
            .multiply(new BigDecimal("0.5"));
        assertThat(fee).isEqualByComparingTo(expected);
    }
}
```

---

## 4. בדיקות החזרת רכב

### TC-004: תהליך החזרת רכב

```java
@SpringBootTest
class VehicleReturnServiceTest {

    @Autowired
    private BookingService bookingService;

    @Test
    @DisplayName("החזרה תקינה — הזמנה עוברת ל-COMPLETED")
    void returnVehicle_NoIssues_ShouldCompleteBooking() {
        // Given
        Long bookingId = createActiveBooking();
        VehicleReturnDTO returnDTO = new VehicleReturnDTO();
        returnDTO.setReturnMileage(15200);
        returnDTO.setReturnFuelLevel(FuelLevel.FULL);
        returnDTO.setHasDamage(false);
        returnDTO.setActualReturnDatetime(LocalDateTime.now());

        // When
        Booking booking = bookingService.returnVehicle(bookingId, returnDTO);

        // Then
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        assertThat(booking.getReturnMileage()).isEqualTo(15200);
        assertThat(booking.getVehicle().getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    @DisplayName("החזרה עם נזק — הזמנה COMPLETED_WITH_DAMAGE, רכב ל-MAINTENANCE")
    void returnVehicle_WithDamage_ShouldCreateDamageReport() {
        // Given
        Long bookingId = createActiveBooking();
        VehicleReturnDTO returnDTO = new VehicleReturnDTO();
        returnDTO.setHasDamage(true);
        returnDTO.setDamageType(DamageType.SCRATCH);
        returnDTO.setDamageDescription("שריטה עמוקה בדלת ימין קדמית");
        returnDTO.setEstimatedCost(new BigDecimal("800.00"));
        returnDTO.setActualReturnDatetime(LocalDateTime.now());

        // When
        Booking booking = bookingService.returnVehicle(bookingId, returnDTO);

        // Then
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED_WITH_DAMAGE);
        assertThat(booking.getVehicle().getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
        assertThat(booking.getDamageReports()).hasSize(1);
        assertThat(booking.getDamageReports().get(0).getEstimatedCost())
            .isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("החזרה מאוחרת — מחשב ומוסיף חיוב נוסף")
    void returnVehicle_LateReturn_ShouldAddLateFee() {
        // Given — הזמנה שמועד החזרה עבר יום אחד
        Booking activeBooking = createActiveBookingWithReturnYesterday();
        VehicleReturnDTO returnDTO = new VehicleReturnDTO();
        returnDTO.setActualReturnDatetime(LocalDateTime.now()); // יום מאוחר!
        returnDTO.setHasDamage(false);

        // When
        Booking booking = bookingService.returnVehicle(activeBooking.getId(), returnDTO);

        // Then
        assertThat(booking.getAdditionalCharges())
            .isEqualByComparingTo(booking.getVehicle().getDailyRate());
        assertThat(booking.getTotalPrice())
            .isGreaterThan(booking.getBasePrice().add(booking.getExtrasPrice()));
    }
}
```

---

## 5. Controller Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /search — מחזיר דף עם תוצאות")
    void getSearchPage_ValidParams_Returns200() throws Exception {
        mockMvc.perform(get("/search")
            .param("pickupBranchId", "1")
            .param("pickupDate", "2026-04-10T10:00")
            .param("returnDate", "2026-04-15T10:00"))
            .andExpect(status().isOk())
            .andExpect(view().name("search/results"))
            .andExpect(model().attributeExists("vehicles"))
            .andExpect(model().attributeExists("searchCriteria"));
    }

    @Test
    @DisplayName("GET /search ללא פרמטרים — מחזיר דף עם הודעת שגיאה")
    void getSearchPage_NoParams_ShowsError() throws Exception {
        mockMvc.perform(get("/search"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("errors"));
    }
}
```

---

## סיכום בדיקות

| קטגוריה | כמות בדיקות | כיסוי |
|---------|------------|-------|
| Vehicle Search | 4 | חיפוש, סינון, תאריכים, זמינות |
| Booking Creation | 4 | יצירה, מחיר, זמינות, מספר ייחודי |
| Payment | 4 | הצלחה, כישלון, ביטול, דמי ביטול |
| Vehicle Return | 3 | תקין, נזק, איחור |
| Controllers | 2 | Search page |
| **סה"כ** | **17** | **כיסוי עיקרי** |

---

*גרסה: 1.0 | DriveFlow — QA Tests*
