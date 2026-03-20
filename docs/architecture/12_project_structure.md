# DriveFlow — מבנה פרויקט

---

## מבנה ספריות מלא

```
DriveFlow/
├── pom.xml                                    # Maven Build Configuration
├── README.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── driveflow/
│   │   │           ├── DriveFlowApplication.java          # Spring Boot Main
│   │   │           │
│   │   │           ├── config/                            # Spring Configuration
│   │   │           │   ├── SecurityConfig.java            # Spring Security config
│   │   │           │   ├── WebMvcConfig.java              # MVC configuration
│   │   │           │   └── DataInitializer.java           # Sample data loader
│   │   │           │
│   │   │           ├── entity/                            # JPA Entities
│   │   │           │   ├── User.java
│   │   │           │   ├── Customer.java
│   │   │           │   ├── Vehicle.java
│   │   │           │   ├── VehicleCategory.java
│   │   │           │   ├── Branch.java
│   │   │           │   ├── Booking.java
│   │   │           │   ├── BookingExtra.java
│   │   │           │   ├── Extra.java
│   │   │           │   ├── Payment.java
│   │   │           │   ├── MaintenanceRecord.java
│   │   │           │   └── DamageReport.java
│   │   │           │
│   │   │           ├── enums/                             # Java Enums
│   │   │           │   ├── UserRole.java
│   │   │           │   ├── CustomerType.java
│   │   │           │   ├── VehicleStatus.java
│   │   │           │   ├── TransmissionType.java
│   │   │           │   ├── FuelType.java
│   │   │           │   ├── FuelLevel.java
│   │   │           │   ├── BookingStatus.java
│   │   │           │   ├── PaymentMethod.java
│   │   │           │   ├── PaymentType.java
│   │   │           │   ├── PaymentStatus.java
│   │   │           │   ├── PricingType.java
│   │   │           │   ├── MaintenanceType.java
│   │   │           │   ├── MaintenanceStatus.java
│   │   │           │   ├── DamageType.java
│   │   │           │   └── DamageStatus.java
│   │   │           │
│   │   │           ├── repository/                        # Spring Data JPA Repositories
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── CustomerRepository.java
│   │   │           │   ├── VehicleRepository.java
│   │   │           │   ├── VehicleCategoryRepository.java
│   │   │           │   ├── BranchRepository.java
│   │   │           │   ├── BookingRepository.java
│   │   │           │   ├── ExtraRepository.java
│   │   │           │   ├── PaymentRepository.java
│   │   │           │   ├── MaintenanceRecordRepository.java
│   │   │           │   └── DamageReportRepository.java
│   │   │           │
│   │   │           ├── service/                           # Business Logic
│   │   │           │   ├── UserService.java
│   │   │           │   ├── CustomerService.java
│   │   │           │   ├── VehicleService.java
│   │   │           │   ├── BranchService.java
│   │   │           │   ├── BookingService.java
│   │   │           │   ├── PaymentService.java
│   │   │           │   ├── MaintenanceService.java
│   │   │           │   ├── DamageReportService.java
│   │   │           │   ├── ReportService.java
│   │   │           │   └── EmailService.java
│   │   │           │
│   │   │           ├── controller/                        # MVC Controllers
│   │   │           │   ├── HomeController.java            # GET /
│   │   │           │   ├── AuthController.java            # /login, /register
│   │   │           │   ├── SearchController.java          # /search
│   │   │           │   ├── VehicleController.java         # /vehicles
│   │   │           │   ├── BookingController.java         # /booking
│   │   │           │   ├── CustomerAccountController.java # /my-account
│   │   │           │   │
│   │   │           │   └── admin/
│   │   │           │       ├── AdminDashboardController.java
│   │   │           │       ├── AdminVehicleController.java
│   │   │           │       ├── AdminBookingController.java
│   │   │           │       ├── AdminCustomerController.java
│   │   │           │       ├── AdminMaintenanceController.java
│   │   │           │       ├── AdminReportController.java
│   │   │           │       └── AdminBranchController.java
│   │   │           │
│   │   │           ├── api/                               # REST API Controllers
│   │   │           │   ├── VehicleApiController.java      # /api/v1/vehicles
│   │   │           │   ├── BookingApiController.java      # /api/v1/bookings
│   │   │           │   └── AdminApiController.java        # /api/v1/admin
│   │   │           │
│   │   │           ├── dto/                               # Data Transfer Objects
│   │   │           │   ├── request/
│   │   │           │   │   ├── RegisterDTO.java
│   │   │           │   │   ├── LoginDTO.java
│   │   │           │   │   ├── BookingCreateDTO.java
│   │   │           │   │   ├── PaymentDTO.java
│   │   │           │   │   ├── VehicleCreateDTO.java
│   │   │           │   │   ├── VehicleReturnDTO.java
│   │   │           │   │   ├── VehiclePickupDTO.java
│   │   │           │   │   └── SearchCriteriaDTO.java
│   │   │           │   │
│   │   │           │   └── response/
│   │   │           │       ├── VehicleSearchResultDTO.java
│   │   │           │       ├── BookingResponseDTO.java
│   │   │           │       ├── DashboardStatsDTO.java
│   │   │           │       └── PriceCalculationDTO.java
│   │   │           │
│   │   │           ├── security/                          # Spring Security
│   │   │           │   ├── CustomUserDetailsService.java
│   │   │           │   └── CustomUserDetails.java
│   │   │           │
│   │   │           └── exception/                         # Custom Exceptions
│   │   │               ├── VehicleNotAvailableException.java
│   │   │               ├── BookingNotFoundException.java
│   │   │               ├── PaymentFailedException.java
│   │   │               └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties                     # App configuration
│   │       ├── application-dev.properties                 # Dev profile
│   │       ├── application-prod.properties                # Prod profile
│   │       │
│   │       ├── templates/                                 # Thymeleaf Templates
│   │       │   ├── layout/
│   │       │   │   ├── base.html                          # Base layout
│   │       │   │   ├── admin-base.html                    # Admin layout
│   │       │   │   ├── header.html                        # Header fragment
│   │       │   │   ├── footer.html                        # Footer fragment
│   │       │   │   └── navbar.html                        # Navigation fragment
│   │       │   │
│   │       │   ├── home/
│   │       │   │   └── index.html                         # דף הבית
│   │       │   │
│   │       │   ├── auth/
│   │       │   │   ├── login.html
│   │       │   │   ├── register.html
│   │       │   │   └── forgot-password.html
│   │       │   │
│   │       │   ├── search/
│   │       │   │   └── results.html                       # תוצאות חיפוש
│   │       │   │
│   │       │   ├── vehicles/
│   │       │   │   └── details.html                       # פרטי רכב
│   │       │   │
│   │       │   ├── booking/
│   │       │   │   ├── extras.html
│   │       │   │   ├── details.html
│   │       │   │   ├── payment.html
│   │       │   │   └── confirmation.html
│   │       │   │
│   │       │   ├── account/
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── bookings.html
│   │       │   │   ├── booking-detail.html
│   │       │   │   └── profile.html
│   │       │   │
│   │       │   ├── admin/
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── vehicles/
│   │       │   │   │   ├── list.html
│   │       │   │   │   ├── form.html
│   │       │   │   │   └── detail.html
│   │       │   │   ├── bookings/
│   │       │   │   │   ├── list.html
│   │       │   │   │   ├── detail.html
│   │       │   │   │   ├── pickup-form.html
│   │       │   │   │   └── return-form.html
│   │       │   │   ├── customers/
│   │       │   │   │   ├── list.html
│   │       │   │   │   └── detail.html
│   │       │   │   ├── maintenance/
│   │       │   │   │   ├── list.html
│   │       │   │   │   └── form.html
│   │       │   │   └── reports/
│   │       │   │       ├── index.html
│   │       │   │       ├── revenue.html
│   │       │   │       └── fleet.html
│   │       │   │
│   │       │   └── error/
│   │       │       ├── 404.html
│   │       │       └── 500.html
│   │       │
│   │       └── static/
│   │           ├── css/
│   │           │   ├── driveflow.css                      # Custom styles
│   │           │   └── admin.css                          # Admin styles
│   │           │
│   │           ├── js/
│   │           │   ├── main.js                            # Global scripts
│   │           │   ├── search.js                          # Search page logic
│   │           │   ├── booking.js                         # Booking wizard logic
│   │           │   ├── payment.js                         # Payment UI
│   │           │   └── admin/
│   │           │       ├── dashboard.js                   # Charts (Chart.js)
│   │           │       └── vehicles.js
│   │           │
│   │           └── images/
│   │               ├── logo/
│   │               ├── vehicles/
│   │               └── branches/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── driveflow/
│                   ├── service/
│                   │   ├── BookingServiceTest.java
│                   │   ├── VehicleServiceTest.java
│                   │   └── PaymentServiceTest.java
│                   │
│                   └── controller/
│                       ├── SearchControllerTest.java
│                       └── BookingControllerTest.java
│
└── docs/                                      # Project Documentation
    ├── architecture/
    │   ├── 08_database_schema.md
    │   ├── 10_java_classes.md
    │   ├── 11_api_endpoints.md
    │   └── 12_project_structure.md
    ├── requirements/
    │   ├── 01_product_specification.md
    │   ├── 02_user_types.md
    │   ├── 03_user_stories.md
    │   ├── 04_acceptance_criteria.md
    │   ├── 05_modules.md
    │   ├── 06_business_process.md
    │   └── 07_screens.md
    ├── erd/
    │   └── 09_erd.md
    ├── ui/
    │   └── 13_ui_design.md
    ├── qa/
    │   └── 15_qa_tests.md
    └── roadmap/
        └── 16_roadmap.md
```

---

## pom.xml — Dependencies עיקריות

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <!-- Thymeleaf Security Integration -->
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- OR MySQL -->
    <!--
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    -->

    <!-- H2 for Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## application.properties

```properties
# Application
spring.application.name=DriveFlow
server.port=8080

# Database (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/driveflow
spring.datasource.username=driveflow_user
spring.datasource.password=driveflow_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.driveflow=DEBUG
logging.level.org.springframework.security=INFO
```

---

*גרסה: 1.0 | DriveFlow — Project Structure*
