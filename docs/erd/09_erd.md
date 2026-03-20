# DriveFlow — ERD (Entity Relationship Diagram)

---

## תרשים ERD טקסטואלי

```
┌─────────────────────┐         ┌─────────────────────┐
│        USERS        │         │      CUSTOMERS      │
├─────────────────────┤         ├─────────────────────┤
│ PK  id              │◄────────│ FK  user_id          │
│     email           │  1:1    │ PK  id              │
│     password        │         │     id_number       │
│     first_name      │         │     license_number  │
│     last_name       │         │     license_expiry  │
│     phone           │         │     date_of_birth   │
│     role            │         │     address_street  │
│     is_active       │         │     address_city    │
│     created_at      │         │     loyalty_points  │
└─────────────────────┘         │     customer_type   │
                                └──────────┬──────────┘
                                           │ 1
                                           │
                                           │ N
                                ┌──────────▼──────────┐
                                │      BOOKINGS       │
                                ├─────────────────────┤
              ┌─────────────────│ FK  customer_id      │
              │                 │ PK  id              │
              │                 │     booking_number  │
              │          FK─────│     vehicle_id      │
              │          FK─────│     pickup_branch_id │
              │          FK─────│     return_branch_id │
              │          FK─────│     agent_id        │
              │                 │     pickup_datetime │
              │                 │     return_datetime │
              │                 │     num_days        │
              │                 │     base_price      │
              │                 │     extras_price    │
              │                 │     total_price     │
              │                 │     status          │
              │                 └──────────┬──────────┘
              │                            │ 1
              │                            │
              │           ┌────────────────┼────────────────┐
              │           │ N              │ N              │ N
              │  ┌────────▼───────┐ ┌──────▼──────┐ ┌──────▼────────┐
              │  │    PAYMENTS    │ │BOOKING_EXTRAS│ │DAMAGE_REPORTS │
              │  ├────────────────┤ ├─────────────┤ ├───────────────┤
              │  │ PK  id         │ │ PK  id      │ │ PK  id        │
              │  │ FK  booking_id │ │ FK booking_id│ │ FK booking_id │
              │  │     amount     │ │ FK extra_id  │ │ FK vehicle_id │
              │  │     method     │ │   quantity  │ │    type       │
              │  │     type       │ │ price_charged│ │   description │
              │  │     status     │ └──────┬──────┘ │ estimated_cost│
              │  │  transaction_id│        │ N      │ customer_fault│
              │  └────────────────┘        │        └───────────────┘
              │                     ┌──────▼──────┐
              │                     │    EXTRAS   │
              │                     ├─────────────┤
              │                     │ PK  id      │
              │                     │     name    │
              │                     │ daily_price │
              │                     │ fixed_price │
              │                     │pricing_type │
              │                     └─────────────┘
              │
              │
     ┌────────▼──────────────────────────────────────────────┐
     │                      VEHICLES                          │
     ├───────────────────────────────────────────────────────┤
     │ PK  id              │ FK  category_id                  │
     │     license_plate   │ FK  branch_id                    │
     │     make            │     transmission                 │
     │     model           │     fuel_type                    │
     │     year            │     num_seats                    │
     │     color           │     num_doors                    │
     │     daily_rate      │     has_ac                       │
     │     status          │     has_bluetooth                │
     │     mileage         │     main_image_url               │
     └──────┬──────────────┴──────────────────┬──────────────┘
            │                                 │
            │ FK                              │ FK
            │                                 │
     ┌──────▼──────────────┐      ┌───────────▼─────────────┐
     │  VEHICLE_CATEGORIES │      │        BRANCHES         │
     ├─────────────────────┤      ├─────────────────────────┤
     │ PK  id              │      │ PK  id                  │
     │     name            │      │     name                │
     │     description     │      │     city                │
     │     icon            │      │     address             │
     │  min_age_required   │      │     phone               │
     │  deposit_amount     │      │     opening_hours       │
     └─────────────────────┘      │     latitude            │
                                  │     longitude           │
                                  │     is_active           │
                                  │     airport_branch      │
                                  └─────────────────────────┘


     ┌─────────────────────────────────────────────────────┐
     │                  MAINTENANCE_RECORDS                │
     ├─────────────────────────────────────────────────────┤
     │ PK  id                                              │
     │ FK  vehicle_id ──────────────────► VEHICLES        │
     │ FK  created_by ──────────────────► USERS           │
     │     maintenance_type                                │
     │     description                                     │
     │     cost                                            │
     │     start_date / end_date                           │
     │     status                                          │
     └─────────────────────────────────────────────────────┘
```

---

## קשרים בין הישויות

| ישות A | קשר | ישות B | הסבר |
|--------|-----|--------|-------|
| `users` | 1:1 | `customers` | כל משתמש לקוח יש פרופיל לקוח אחד |
| `customers` | 1:N | `bookings` | ללקוח יכולות להיות הזמנות רבות |
| `vehicles` | 1:N | `bookings` | לרכב יכולות להיות הזמנות רבות (בתקופות שונות) |
| `branches` | 1:N | `bookings` | סניף כסניף איסוף בהזמנות רבות |
| `branches` | 1:N | `bookings` | סניף כסניף החזרה בהזמנות רבות |
| `vehicle_categories` | 1:N | `vehicles` | קטגוריה כוללת רכבים רבים |
| `branches` | 1:N | `vehicles` | סניף כולל רכבים רבים |
| `bookings` | 1:N | `payments` | להזמנה יכולים להיות תשלומים מרובים |
| `bookings` | M:N | `extras` | דרך `booking_extras` |
| `bookings` | 1:N | `damage_reports` | להזמנה יכולים להיות דוחות נזק מרובים |
| `vehicles` | 1:N | `maintenance_records` | לרכב יכולות להיות רשומות תחזוקה מרובות |
| `vehicles` | 1:N | `damage_reports` | לרכב יכולים להיות דוחות נזק מרובים |
| `users` | 1:N | `bookings` | נציג (agent) מטפל בהזמנות |
| `users` | 1:N | `maintenance_records` | נציג יצר את הרשומה |

---

## כללי עסקי חשובים (Business Rules)

1. **זמינות רכב** — רכב זמין אם:
   - `vehicles.status = 'AVAILABLE'`
   - ואין `bookings` עם `status IN ('CONFIRMED','ACTIVE')` שחופפות לתאריכים המבוקשים

2. **מחיר הזמנה** — מחושב כ:
   ```
   base_price = vehicle.daily_rate × num_days
   extras_price = Σ(extra.daily_price × num_days OR extra.fixed_price)
   total_price = base_price + extras_price + additional_charges
   ```

3. **מספר הזמנה** — נוצר אוטומטית בפורמט:
   ```
   DRIVEFLOW-{YEAR}-{5-digit-sequence}
   Example: DRIVEFLOW-2026-00123
   ```

4. **נעילת מחיר** — מחיר הרכב ב-`bookings.base_price` נעול בזמן יצירת ההזמנה

5. **CASCADE** — מחיקת `bookings` מוחקת גם `booking_extras`, `payments` (Soft Delete מועדף)

---

*גרסה: 1.0 | DriveFlow — ERD*
