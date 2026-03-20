# DriveFlow — API Endpoints

---

## בסיס URL

```
Base URL: http://localhost:8080
API Prefix (REST): /api/v1
Web Views: /
Admin Views: /admin
```

---

## Authentication Endpoints

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| GET | `/login` | דף כניסה | כולם |
| POST | `/login` | עיבוד כניסה | כולם |
| GET | `/register` | דף הרשמה | אורחים |
| POST | `/register` | עיבוד הרשמה | אורחים |
| POST | `/logout` | יציאה מהמערכת | מחוברים |
| GET | `/forgot-password` | דף שחזור סיסמה | אורחים |
| POST | `/forgot-password` | שליחת מייל שחזור | אורחים |

---

## Customer Web Pages

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| GET | `/` | דף הבית | כולם |
| GET | `/search` | תוצאות חיפוש | כולם |
| GET | `/vehicles/{id}` | פרטי רכב | כולם |
| GET | `/booking/new` | התחלת הזמנה | CUSTOMER |
| GET | `/booking/extras` | בחירת תוספות | CUSTOMER |
| GET | `/booking/details` | פרטי הזמנה + אישור | CUSTOMER |
| GET | `/booking/payment` | עמוד תשלום | CUSTOMER |
| POST | `/booking/pay` | עיבוד תשלום | CUSTOMER |
| GET | `/booking/confirmation/{number}` | אישור הזמנה | CUSTOMER |
| GET | `/my-account` | אזור אישי | CUSTOMER |
| GET | `/my-account/bookings` | היסטוריית הזמנות | CUSTOMER |
| GET | `/my-account/bookings/{id}` | פרטי הזמנה ספציפית | CUSTOMER |
| POST | `/my-account/bookings/{id}/cancel` | ביטול הזמנה | CUSTOMER |
| GET | `/my-account/profile` | פרופיל אישי | CUSTOMER |
| POST | `/my-account/profile` | עדכון פרופיל | CUSTOMER |

---

## Admin Web Pages

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| GET | `/admin/dashboard` | לוח מחוונים | ADMIN, AGENT |
| GET | `/admin/vehicles` | ניהול רכבים | ADMIN |
| GET | `/admin/vehicles/new` | טופס רכב חדש | ADMIN |
| POST | `/admin/vehicles` | שמירת רכב חדש | ADMIN |
| GET | `/admin/vehicles/{id}/edit` | עריכת רכב | ADMIN |
| POST | `/admin/vehicles/{id}` | עדכון רכב | ADMIN |
| POST | `/admin/vehicles/{id}/delete` | מחיקת רכב | ADMIN |
| POST | `/admin/vehicles/{id}/status` | עדכון סטטוס רכב | ADMIN, AGENT |
| GET | `/admin/bookings` | ניהול הזמנות | ADMIN, AGENT |
| GET | `/admin/bookings/{id}` | פרטי הזמנה | ADMIN, AGENT |
| POST | `/admin/bookings/{id}/pickup` | עדכון איסוף רכב | AGENT |
| POST | `/admin/bookings/{id}/return` | עדכון החזרת רכב | AGENT |
| POST | `/admin/bookings/{id}/status` | עדכון סטטוס | ADMIN |
| GET | `/admin/customers` | ניהול לקוחות | ADMIN |
| GET | `/admin/customers/{id}` | פרופיל לקוח | ADMIN |
| POST | `/admin/customers/{id}/toggle` | חסימה/שחרור | ADMIN |
| GET | `/admin/maintenance` | ניהול תחזוקה | ADMIN, AGENT |
| GET | `/admin/maintenance/new` | טופס תחזוקה | ADMIN, AGENT |
| POST | `/admin/maintenance` | שמירת תחזוקה | ADMIN, AGENT |
| GET | `/admin/damage-reports` | דוחות נזק | ADMIN |
| GET | `/admin/reports` | מסך דוחות | ADMIN |
| GET | `/admin/reports/revenue` | דוח הכנסות | ADMIN |
| GET | `/admin/reports/bookings` | דוח הזמנות | ADMIN |
| GET | `/admin/reports/fleet` | דוח ניצולת | ADMIN |
| GET | `/admin/branches` | ניהול סניפים | ADMIN |
| GET | `/admin/categories` | ניהול קטגוריות | ADMIN |

---

## REST API Endpoints (JSON)

### Vehicles API

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| GET | `/api/v1/vehicles/search` | חיפוש רכבים זמינים | כולם |
| GET | `/api/v1/vehicles/{id}` | פרטי רכב (JSON) | כולם |
| GET | `/api/v1/vehicles/{id}/availability` | בדיקת זמינות | כולם |
| GET | `/api/v1/branches` | רשימת סניפים | כולם |
| GET | `/api/v1/categories` | רשימת קטגוריות | כולם |

**Search Parameters:**
```
GET /api/v1/vehicles/search
  ?pickupBranchId=1
  &pickupDate=2026-04-01T10:00
  &returnDate=2026-04-05T10:00
  &categoryId=2        (optional)
  &maxPrice=300        (optional)
  &transmission=AUTO   (optional)
  &page=0
  &size=12
```

### Booking API

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| POST | `/api/v1/bookings` | יצירת הזמנה | CUSTOMER |
| GET | `/api/v1/bookings/{id}` | פרטי הזמנה | CUSTOMER (owner) |
| POST | `/api/v1/bookings/{id}/cancel` | ביטול | CUSTOMER |
| POST | `/api/v1/bookings/{id}/payment` | תשלום | CUSTOMER |
| GET | `/api/v1/bookings/calculate-price` | חישוב מחיר | CUSTOMER |

**Create Booking Request:**
```json
POST /api/v1/bookings
{
  "vehicleId": 5,
  "pickupBranchId": 1,
  "returnBranchId": 1,
  "pickupDatetime": "2026-04-01T10:00:00",
  "returnDatetime": "2026-04-05T10:00:00",
  "extraIds": [1, 3],
  "notes": "רכב לנסיעה לאילת"
}
```

**Create Booking Response:**
```json
{
  "bookingId": 123,
  "bookingNumber": "DRIVEFLOW-2026-00123",
  "status": "PENDING",
  "vehicle": { "id": 5, "displayName": "2024 Toyota Corolla" },
  "numDays": 4,
  "basePrice": 600.00,
  "extrasPrice": 200.00,
  "totalPrice": 800.00,
  "pickupDatetime": "2026-04-01T10:00:00",
  "returnDatetime": "2026-04-05T10:00:00"
}
```

### Admin API

| Method | URL | תיאור | גישה |
|--------|-----|--------|------|
| GET | `/api/v1/admin/dashboard/stats` | KPI Stats (JSON) | ADMIN |
| GET | `/api/v1/admin/vehicles` | רשימת רכבים | ADMIN |
| GET | `/api/v1/admin/bookings` | רשימת הזמנות | ADMIN |
| GET | `/api/v1/admin/reports/revenue` | נתוני הכנסות | ADMIN |
| GET | `/api/v1/admin/reports/fleet-utilization` | ניצולת צי | ADMIN |

---

## HTTP Status Codes בשימוש

| קוד | תיאור |
|-----|--------|
| `200 OK` | בקשה הצליחה |
| `201 Created` | ישות נוצרה |
| `302 Found` | Redirect (Thymeleaf forms) |
| `400 Bad Request` | שגיאת Validation |
| `401 Unauthorized` | לא מחובר |
| `403 Forbidden` | אין הרשאה |
| `404 Not Found` | לא נמצא |
| `409 Conflict` | ניגוד (רכב לא זמין) |
| `500 Internal Server Error` | שגיאת שרת |

---

*גרסה: 1.0 | DriveFlow — API Endpoints*
