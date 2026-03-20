# DriveFlow — Database Schema

---

## 1. טבלה: `users`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | כתובת מייל |
| `password` | VARCHAR(255) | NOT NULL | סיסמה מוצפנת (BCrypt) |
| `first_name` | VARCHAR(100) | NOT NULL | שם פרטי |
| `last_name` | VARCHAR(100) | NOT NULL | שם משפחה |
| `phone` | VARCHAR(20) | | מספר טלפון |
| `role` | ENUM('CUSTOMER','AGENT','ADMIN') | NOT NULL, DEFAULT 'CUSTOMER' | תפקיד במערכת |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | חשבון פעיל/חסום |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT | תאריך יצירה |
| `updated_at` | TIMESTAMP | NOT NULL | תאריך עדכון אחרון |

**אינדקסים:** `email` (UNIQUE), `role`

---

## 2. טבלה: `customers`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `user_id` | BIGINT | FK → users.id, UNIQUE, NOT NULL | קשר ל-users |
| `id_number` | VARCHAR(20) | UNIQUE | תעודת זהות/דרכון |
| `date_of_birth` | DATE | | תאריך לידה |
| `license_number` | VARCHAR(50) | | מספר רישיון נהיגה |
| `license_expiry` | DATE | | תוקף רישיון נהיגה |
| `license_country` | VARCHAR(50) | DEFAULT 'IL' | מדינת הנפקת רישיון |
| `address_street` | VARCHAR(255) | | רחוב + מספר |
| `address_city` | VARCHAR(100) | | עיר |
| `address_country` | VARCHAR(50) | DEFAULT 'ישראל' | מדינה |
| `loyalty_points` | INT | NOT NULL, DEFAULT 0 | נקודות נאמנות |
| `customer_type` | ENUM('REGULAR','BUSINESS','VIP') | NOT NULL, DEFAULT 'REGULAR' | סוג לקוח |

**אינדקסים:** `user_id` (UNIQUE FK), `id_number`

---

## 3. טבלה: `branches`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `name` | VARCHAR(100) | NOT NULL | שם סניף |
| `city` | VARCHAR(100) | NOT NULL | עיר |
| `address` | VARCHAR(255) | NOT NULL | כתובת מלאה |
| `phone` | VARCHAR(20) | | טלפון סניף |
| `email` | VARCHAR(255) | | מייל סניף |
| `opening_hours` | VARCHAR(100) | | שעות פעילות (טקסט חופשי) |
| `latitude` | DECIMAL(10,8) | | קו רוחב לגוגל מפות |
| `longitude` | DECIMAL(11,8) | | קו אורך לגוגל מפות |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | סניף פעיל/סגור |
| `airport_branch` | BOOLEAN | NOT NULL, DEFAULT FALSE | סניף שדה תעופה |

---

## 4. טבלה: `vehicle_categories`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `name` | VARCHAR(50) | NOT NULL, UNIQUE | שם קטגוריה |
| `description` | TEXT | | תיאור קטגוריה |
| `icon` | VARCHAR(100) | | שם קובץ אייקון |
| `min_age_required` | INT | NOT NULL, DEFAULT 21 | גיל מינימלי לנהיגה |
| `deposit_amount` | DECIMAL(10,2) | NOT NULL | ערבון נדרש |

**ערכי name לדוגמה:** Mini, Economy, Compact, Intermediate, Full-Size, SUV, Luxury, Van

---

## 5. טבלה: `vehicles`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `license_plate` | VARCHAR(20) | UNIQUE, NOT NULL | לוחית רישוי |
| `make` | VARCHAR(50) | NOT NULL | יצרן (Toyota, Hyundai...) |
| `model` | VARCHAR(50) | NOT NULL | דגם |
| `year` | INT | NOT NULL | שנת ייצור |
| `color` | VARCHAR(30) | | צבע |
| `category_id` | BIGINT | FK → vehicle_categories.id, NOT NULL | קטגוריה |
| `branch_id` | BIGINT | FK → branches.id, NOT NULL | סניף |
| `transmission` | ENUM('MANUAL','AUTOMATIC') | NOT NULL | תיבת הילוכים |
| `fuel_type` | ENUM('PETROL','DIESEL','HYBRID','ELECTRIC') | NOT NULL | סוג דלק |
| `num_seats` | INT | NOT NULL | מספר מושבים |
| `num_doors` | INT | NOT NULL | מספר דלתות |
| `luggage_capacity` | INT | | נפח מטען (ליטר) |
| `has_ac` | BOOLEAN | NOT NULL, DEFAULT TRUE | מיזוג אוויר |
| `has_bluetooth` | BOOLEAN | NOT NULL, DEFAULT TRUE | Bluetooth |
| `has_gps_builtin` | BOOLEAN | NOT NULL, DEFAULT FALSE | GPS מובנה |
| `daily_rate` | DECIMAL(10,2) | NOT NULL | מחיר ליום (₪) |
| `status` | ENUM('AVAILABLE','RENTED','MAINTENANCE','OUT_OF_SERVICE') | NOT NULL, DEFAULT 'AVAILABLE' | סטטוס |
| `mileage` | INT | NOT NULL, DEFAULT 0 | קילומטראז' נוכחי |
| `main_image_url` | VARCHAR(255) | | URL תמונה ראשית |
| `description` | TEXT | | תיאור שיווקי |
| `created_at` | TIMESTAMP | NOT NULL | תאריך הוספה |

**אינדקסים:** `license_plate` (UNIQUE), `category_id`, `branch_id`, `status`

---

## 6. טבלה: `extras`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `name` | VARCHAR(100) | NOT NULL | שם תוספת |
| `description` | TEXT | | תיאור התוספת |
| `daily_price` | DECIMAL(10,2) | | מחיר ליום |
| `fixed_price` | DECIMAL(10,2) | | מחיר קבוע (לא ליום) |
| `pricing_type` | ENUM('DAILY','FIXED') | NOT NULL | סוג תמחור |
| `icon` | VARCHAR(50) | | אייקון Bootstrap |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | תוספת זמינה |

**דוגמאות:** GPS (30₪/יום), כיסא ילדים (25₪/יום), ביטוח מורחב (45₪/יום), חבילת דלק (150₪ fixed)

---

## 7. טבלה: `bookings`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `booking_number` | VARCHAR(30) | UNIQUE, NOT NULL | מספר הזמנה (DRIVEFLOW-YYYY-XXXXX) |
| `customer_id` | BIGINT | FK → customers.id, NOT NULL | קשר ללקוח |
| `vehicle_id` | BIGINT | FK → vehicles.id, NOT NULL | קשר לרכב |
| `pickup_branch_id` | BIGINT | FK → branches.id, NOT NULL | סניף איסוף |
| `return_branch_id` | BIGINT | FK → branches.id, NOT NULL | סניף החזרה |
| `pickup_datetime` | DATETIME | NOT NULL | תאריך ושעת איסוף |
| `return_datetime` | DATETIME | NOT NULL | תאריך ושעת החזרה מוסכמת |
| `actual_return_datetime` | DATETIME | | תאריך ושעת החזרה בפועל |
| `num_days` | INT | NOT NULL | מספר ימי ההשכרה |
| `base_price` | DECIMAL(10,2) | NOT NULL | מחיר בסיס (לפני תוספות) |
| `extras_price` | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | עלות תוספות |
| `additional_charges` | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | חיובים נוספים (נזק, ימים נוספים) |
| `total_price` | DECIMAL(10,2) | NOT NULL | סה"כ מחיר סופי |
| `status` | ENUM('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED','COMPLETED_WITH_DAMAGE') | NOT NULL, DEFAULT 'PENDING' | סטטוס הזמנה |
| `pickup_mileage` | INT | | קילומטראז' באיסוף |
| `return_mileage` | INT | | קילומטראז' בהחזרה |
| `pickup_fuel_level` | ENUM('FULL','THREE_QUARTERS','HALF','QUARTER','EMPTY') | | מצב דלק באיסוף |
| `return_fuel_level` | ENUM('FULL','THREE_QUARTERS','HALF','QUARTER','EMPTY') | | מצב דלק בהחזרה |
| `agent_id` | BIGINT | FK → users.id | נציג שטיפל |
| `notes` | TEXT | | הערות כלליות |
| `cancellation_reason` | TEXT | | סיבת ביטול (אם בוטל) |
| `created_at` | TIMESTAMP | NOT NULL | תאריך יצירת הזמנה |
| `updated_at` | TIMESTAMP | NOT NULL | תאריך עדכון אחרון |

**אינדקסים:** `booking_number` (UNIQUE), `customer_id`, `vehicle_id`, `status`, `pickup_datetime`

---

## 8. טבלה: `booking_extras`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `booking_id` | BIGINT | FK → bookings.id, NOT NULL | קשר להזמנה |
| `extra_id` | BIGINT | FK → extras.id, NOT NULL | קשר לתוספת |
| `quantity` | INT | NOT NULL, DEFAULT 1 | כמות |
| `price_charged` | DECIMAL(10,2) | NOT NULL | מחיר שחויב (snapshot) |

**UNIQUE:** (`booking_id`, `extra_id`)

---

## 9. טבלה: `payments`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `booking_id` | BIGINT | FK → bookings.id, NOT NULL | קשר להזמנה |
| `amount` | DECIMAL(10,2) | NOT NULL | סכום |
| `currency` | VARCHAR(3) | NOT NULL, DEFAULT 'ILS' | מטבע |
| `payment_method` | ENUM('CREDIT_CARD','PAYPAL','BANK_TRANSFER') | NOT NULL | שיטת תשלום |
| `payment_type` | ENUM('INITIAL','ADDITIONAL','REFUND') | NOT NULL | סוג תשלום |
| `status` | ENUM('PENDING','SUCCESS','FAILED','REFUNDED') | NOT NULL, DEFAULT 'PENDING' | סטטוס |
| `transaction_id` | VARCHAR(100) | UNIQUE | מזהה עסקה (Simulated) |
| `card_last_four` | VARCHAR(4) | | 4 ספרות אחרונות (אם כרטיס) |
| `payment_datetime` | TIMESTAMP | NOT NULL | תאריך ושעת תשלום |
| `notes` | TEXT | | הערות |

---

## 10. טבלה: `maintenance_records`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `vehicle_id` | BIGINT | FK → vehicles.id, NOT NULL | קשר לרכב |
| `maintenance_type` | ENUM('SCHEDULED_SERVICE','REPAIR','CLEANING','INSPECTION','TIRE_CHANGE','OTHER') | NOT NULL | סוג תחזוקה |
| `description` | TEXT | NOT NULL | תיאור העבודה שבוצעה |
| `cost` | DECIMAL(10,2) | | עלות תחזוקה |
| `service_provider` | VARCHAR(100) | | שם הגורם המבצע |
| `start_date` | DATE | NOT NULL | תאריך התחלה |
| `end_date` | DATE | | תאריך סיום |
| `mileage_at_service` | INT | | קילומטראז' בזמן השירות |
| `next_service_due` | DATE | | תאריך שירות הבא |
| `status` | ENUM('SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED') | NOT NULL | סטטוס |
| `created_by` | BIGINT | FK → users.id | מי יצר את הרשומה |
| `created_at` | TIMESTAMP | NOT NULL | תאריך יצירה |

---

## 11. טבלה: `damage_reports`

| עמודה | סוג | אילוצים | תיאור |
|-------|-----|---------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | מזהה ייחודי |
| `booking_id` | BIGINT | FK → bookings.id, NOT NULL | קשר להזמנה |
| `vehicle_id` | BIGINT | FK → vehicles.id, NOT NULL | קשר לרכב |
| `damage_type` | ENUM('SCRATCH','DENT','BROKEN_GLASS','INTERIOR_DAMAGE','MECHANICAL','OTHER') | NOT NULL | סוג נזק |
| `damage_location` | VARCHAR(100) | | מיקום על הרכב |
| `description` | TEXT | NOT NULL | תיאור מפורט |
| `estimated_cost` | DECIMAL(10,2) | | הערכת עלות תיקון |
| `actual_cost` | DECIMAL(10,2) | | עלות תיקון בפועל |
| `customer_fault` | BOOLEAN | NOT NULL, DEFAULT FALSE | אשמת הלקוח |
| `reported_at` | TIMESTAMP | NOT NULL | מתי דווח |
| `reported_by` | BIGINT | FK → users.id | מי דיווח |
| `status` | ENUM('REPORTED','UNDER_REVIEW','REPAIR_ORDERED','REPAIRED','CLOSED') | NOT NULL, DEFAULT 'REPORTED' | סטטוס |

---

*גרסה: 1.0 | DriveFlow — Database Schema*
