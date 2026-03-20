# DriveFlow — Roadmap פיתוח

---

## סקירה כללית

הפיתוח מחולק ל-5 Sprints שבועיים, כל Sprint מסתיים בגרסה פעילה שניתנת לבדיקה.

---

## Sprint 1 — תשתית ו-Authentication (שבוע 1)

**מטרה:** קבלת מערכת עם בסיס נתונים עובדת, Authentication מלא ודפים בסיסיים.

### משימות:

**הגדרת פרויקט:**
- [ ] יצירת פרויקט Spring Boot עם Maven
- [ ] הגדרת כל ה-Dependencies ב-pom.xml
- [ ] חיבור PostgreSQL / MySQL
- [ ] הגדרת application.properties

**Entities ו-Repositories:**
- [ ] יצירת כל ה-Entities (User, Customer, Vehicle, VehicleCategory, Branch)
- [ ] יצירת כל ה-Repositories
- [ ] יצירת Enums

**Authentication:**
- [ ] הגדרת Spring Security (SecurityConfig.java)
- [ ] CustomUserDetailsService
- [ ] דף Login + Register (Thymeleaf)
- [ ] הצפנת סיסמאות BCrypt

**Layout ו-UI Base:**
- [ ] Base Layout (header, footer, navbar)
- [ ] הגדרת Bootstrap 5 RTL
- [ ] CSS בסיסי (driveflow.css)

**Data Initialization:**
- [ ] DataInitializer — סניפים, קטגוריות, תוספות, משתמש Admin

### תוצר Sprint 1:
✅ מערכת עם כניסה/הרשמה עובדת, Admin יכול להתחבר

---

## Sprint 2 — ניהול רכבים וחיפוש (שבוע 2)

**מטרה:** מנהל יכול לנהל רכבים, לקוח יכול לחפש ולראות פרטי רכב.

### משימות:

**Entities נוספות:**
- [ ] יצירת Entities: Booking, BookingExtra, Extra, Payment, MaintenanceRecord, DamageReport
- [ ] קשרים בין כל הישויות

**Vehicle Management (Admin):**
- [ ] VehicleService — CRUD + findAvailable
- [ ] AdminVehicleController
- [ ] Templates: admin/vehicles/list.html, form.html, detail.html
- [ ] אפשרות שינוי סטטוס רכב

**Vehicle Search (Customer):**
- [ ] SearchCriteriaDTO + Validation
- [ ] VehicleService.findAvailableVehicles()
- [ ] SearchController
- [ ] search/results.html — Grid עם סינון
- [ ] vehicles/details.html — פרטי רכב מלאים

**דף הבית:**
- [ ] HomeController
- [ ] home/index.html — Hero + טופס חיפוש + קטגוריות

### תוצר Sprint 2:
✅ לקוח יכול לחפש רכבים, מנהל מנהל את הצי

---

## Sprint 3 — מערכת הזמנות (שבוע 3)

**מטרה:** תהליך הזמנה מלא מקצה לקצה — מחיפוש ועד אישור.

### משימות:

**Booking Flow:**
- [ ] BookingService — createBooking, calculatePrice, generateBookingNumber
- [ ] BookingController — Wizard (extras → details → payment)
- [ ] BookingCreateDTO + Validation
- [ ] Templates: booking/extras.html, details.html, payment.html, confirmation.html
- [ ] Session/FlashAttributes לשמירת נתונים בין שלבי Wizard

**Payment (Simulated):**
- [ ] PaymentService — processPayment (Simulation)
- [ ] PaymentDTO
- [ ] עדכון סטטוס הזמנה לאחר תשלום

**Email Notifications:**
- [ ] EmailService — אישור הזמנה + אישור ביטול
- [ ] HTML Email Templates

**Customer Account:**
- [ ] CustomerAccountController
- [ ] account/bookings.html — היסטוריה
- [ ] account/booking-detail.html
- [ ] ביטול הזמנה + חישוב דמי ביטול
- [ ] account/profile.html — עדכון פרטים

### תוצר Sprint 3:
✅ לקוח יכול להזמין, לשלם ולנהל הזמנות

---

## Sprint 4 — Admin Dashboard ותפעול (שבוע 4)

**מטרה:** מנהל ונציג יכולים לנהל את כל התפעול היומיומי.

### משימות:

**Admin Dashboard:**
- [ ] AdminDashboardController — KPIs
- [ ] DashboardStatsDTO
- [ ] admin/dashboard.html — KPIs + גרפים (Chart.js)
- [ ] גרף הכנסות + גרף ניצולת

**Booking Management (Admin):**
- [ ] AdminBookingController
- [ ] admin/bookings/list.html — כל ההזמנות + Tabs
- [ ] admin/bookings/pickup-form.html — טופס איסוף
- [ ] admin/bookings/return-form.html — טופס החזרה
- [ ] admin/bookings/detail.html

**Customer Management:**
- [ ] AdminCustomerController
- [ ] admin/customers/list.html + detail.html

**Maintenance:**
- [ ] MaintenanceService
- [ ] AdminMaintenanceController
- [ ] admin/maintenance/list.html + form.html

**Damage Reports:**
- [ ] DamageReportService
- [ ] דיווח נזקים בטופס החזרה
- [ ] admin/damage-reports/list.html

### תוצר Sprint 4:
✅ Admin ו-Agent יכולים לנהל את כל התפעול

---

## Sprint 5 — דוחות, QA ו-Polish (שבוע 5)

**מטרה:** מערכת מוכנה לפרזנטציה אקדמית — מלאה, בדוקה ויפה.

### משימות:

**Reports:**
- [ ] ReportService — הכנסות, הזמנות, לקוחות, ניצולת
- [ ] AdminReportController
- [ ] admin/reports/ — כל דפי הדוחות
- [ ] ייצוא CSV

**REST API:**
- [ ] VehicleApiController — /api/v1/vehicles/search
- [ ] BookingApiController — /api/v1/bookings
- [ ] AdminApiController — /api/v1/admin/dashboard/stats

**Testing:**
- [ ] BookingServiceTest — כל test cases
- [ ] VehicleServiceTest
- [ ] PaymentServiceTest
- [ ] SearchControllerTest

**Polish & UX:**
- [ ] עמודי שגיאה (404, 500)
- [ ] הודעות Flash (Success/Error)
- [ ] Loading States ב-JavaScript
- [ ] Validation messages בעברית
- [ ] Responsive בדיקה לכל מסכים

**Documentation:**
- [ ] README.md מקיף
- [ ] Javadoc לכל ה-Services
- [ ] הגדרות נתונים פיקטיביים מלאים

**Final QA:**
- [ ] בדיקת תהליך מלא End-to-End
- [ ] בדיקת כל role (CUSTOMER, AGENT, ADMIN)
- [ ] בדיקת validation בכל הטפסים

### תוצר Sprint 5:
✅ מערכת מוכנה לפרזנטציה — מלאה, מתועדת ובדוקה

---

## סיכום Roadmap

| Sprint | שבוע | תוצר עיקרי |
|--------|------|-----------|
| Sprint 1 | שבוע 1 | תשתית + Authentication |
| Sprint 2 | שבוע 2 | ניהול רכבים + חיפוש |
| Sprint 3 | שבוע 3 | Booking מלא + תשלום |
| Sprint 4 | שבוע 4 | Admin Dashboard + תפעול |
| Sprint 5 | שבוע 5 | דוחות + QA + Polish |

---

## Definition of Done

כל Sprint נחשב מוכן כשמתקיים:
- ✅ כל המשימות ב-Sprint הושלמו
- ✅ אין Compile Errors
- ✅ כל ה-Tests עוברים
- ✅ הממשק responsive ועובד בדפדפן
- ✅ כל הנתיבים (URLs) עובדים כצפוי
- ✅ אין שגיאות בלוגים

---

*גרסה: 1.0 | DriveFlow — Development Roadmap*
