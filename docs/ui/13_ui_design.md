# DriveFlow — UI Design Guide

---

## 1. פלטת צבעים

| שם | קוד HEX | שימוש |
|----|---------|-------|
| Primary Blue | `#1A3C5E` | Header, כפתורים ראשיים, לוגו |
| Accent Gold | `#D4A843` | Badge, הדגשות, CTA מיוחדים |
| Light Blue | `#2D6A9F` | Hover States, קישורים |
| Dark Gray | `#2C2C2C` | טקסט ראשי |
| Medium Gray | `#6C757D` | טקסט משני |
| Light Gray | `#F8F9FA` | רקע דפים |
| White | `#FFFFFF` | כרטיסים, טפסים |
| Success Green | `#28A745` | אישורים, סטטוס פעיל |
| Warning Orange | `#FFC107` | אזהרות, ממתין |
| Danger Red | `#DC3545` | שגיאות, ביטול |
| Border | `#E9ECEF` | הפרדות, מסגרות |

---

## 2. טיפוגרפיה

| שימוש | פונט | גודל | משקל |
|-------|------|------|------|
| כותרת ראשית H1 | Heebo | 2.5rem | 700 |
| כותרת H2 | Heebo | 1.75rem | 600 |
| כותרת H3 | Heebo | 1.25rem | 600 |
| גוף טקסט | Heebo | 1rem | 400 |
| לייבל טופס | Heebo | 0.875rem | 500 |
| טקסט קטן | Heebo | 0.75rem | 400 |
| קוד/מספרים | Roboto Mono | 0.875rem | 400 |

**Google Fonts:**
```html
<link href="https://fonts.googleapis.com/css2?family=Heebo:wght@300;400;500;600;700&family=Roboto+Mono&display=swap" rel="stylesheet">
```

---

## 3. Component Design System

### Buttons

```css
/* Primary Button */
.btn-primary {
    background: #1A3C5E;
    border-color: #1A3C5E;
    border-radius: 8px;
    padding: 12px 28px;
    font-weight: 600;
    transition: all 0.2s ease;
}
.btn-primary:hover {
    background: #2D6A9F;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(26,60,94,0.3);
}

/* Gold CTA Button */
.btn-gold {
    background: #D4A843;
    color: #1A3C5E;
    border: none;
    border-radius: 8px;
    padding: 12px 28px;
    font-weight: 700;
}

/* Outline Button */
.btn-outline-primary {
    border: 2px solid #1A3C5E;
    color: #1A3C5E;
    border-radius: 8px;
}
```

### Cards

```css
.card {
    border: 1px solid #E9ECEF;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    transition: all 0.2s ease;
}
.card:hover {
    box-shadow: 0 8px 24px rgba(0,0,0,0.12);
    transform: translateY(-2px);
}
```

### Vehicle Card

```html
<div class="vehicle-card card h-100">
  <div class="vehicle-card-image position-relative">
    <img src="..." class="card-img-top" style="height: 200px; object-fit: cover;">
    <span class="badge badge-category">SUV</span>
  </div>
  <div class="card-body">
    <h5 class="vehicle-name">2024 Toyota RAV4</h5>
    <div class="vehicle-specs d-flex gap-3 text-muted small my-2">
      <span><i class="bi bi-people"></i> 5 מושבים</span>
      <span><i class="bi bi-gear"></i> אוטומטי</span>
      <span><i class="bi bi-wind"></i> מיזוג</span>
    </div>
    <div class="d-flex justify-content-between align-items-center mt-3">
      <div class="price">
        <span class="text-muted small">מ-</span>
        <span class="price-amount">₪350</span>
        <span class="text-muted small">/יום</span>
      </div>
      <a href="..." class="btn btn-primary btn-sm">הזמן עכשיו</a>
    </div>
  </div>
</div>
```

---

## 4. Header Design

```html
<header class="site-header sticky-top">
  <nav class="navbar navbar-expand-lg" style="background: #1A3C5E;">
    <!-- Logo -->
    <a class="navbar-brand" href="/">
      <span style="color: #D4A843; font-weight: 800; font-size: 1.5rem;">Drive</span>
      <span style="color: white; font-weight: 300; font-size: 1.5rem;">Flow</span>
    </a>

    <!-- Navigation -->
    <div class="navbar-nav mx-auto">
      <a class="nav-link text-white" href="/search">חפש רכב</a>
      <a class="nav-link text-white" href="/branches">סניפים</a>
      <a class="nav-link text-white" href="/about">אודות</a>
    </div>

    <!-- Auth Buttons -->
    <div class="d-flex gap-2">
      <a href="/login" class="btn btn-outline-light btn-sm">כניסה</a>
      <a href="/register" class="btn btn-gold btn-sm">הרשמה</a>
    </div>
  </nav>
</header>
```

---

## 5. Hero Section (דף בית)

```css
.hero-section {
    background: linear-gradient(135deg, #1A3C5E 0%, #2D6A9F 60%, #1A3C5E 100%);
    min-height: 85vh;
    position: relative;
    overflow: hidden;
}

.hero-section::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 80px;
    background: white;
    clip-path: ellipse(55% 100% at 50% 100%);
}

.search-box {
    background: white;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
```

---

## 6. Status Badges

```css
/* Booking Status Badges */
.badge-pending    { background: #FFF3CD; color: #856404; }
.badge-confirmed  { background: #D1ECF1; color: #0C5460; }
.badge-active     { background: #D4EDDA; color: #155724; }
.badge-completed  { background: #E2E3E5; color: #383D41; }
.badge-cancelled  { background: #F8D7DA; color: #721C24; }

/* Vehicle Status Badges */
.badge-available    { background: #D4EDDA; color: #155724; }
.badge-rented       { background: #CCE5FF; color: #004085; }
.badge-maintenance  { background: #FFF3CD; color: #856404; }
.badge-out-of-service { background: #F8D7DA; color: #721C24; }
```

---

## 7. Admin Dashboard Design

```css
/* KPI Cards */
.kpi-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    border-left: 4px solid;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.kpi-card.revenue { border-left-color: #1A3C5E; }
.kpi-card.bookings { border-left-color: #28A745; }
.kpi-card.utilization { border-left-color: #D4A843; }
.kpi-card.customers { border-left-color: #17A2B8; }

/* Admin Sidebar */
.admin-sidebar {
    background: #1A3C5E;
    min-height: 100vh;
    width: 260px;
    padding-top: 80px;
}
.admin-sidebar .nav-link {
    color: rgba(255,255,255,0.7);
    padding: 12px 20px;
    border-radius: 8px;
    transition: all 0.2s;
}
.admin-sidebar .nav-link:hover,
.admin-sidebar .nav-link.active {
    background: rgba(255,255,255,0.1);
    color: white;
}
```

---

## 8. RTL (Right-to-Left) Support

```html
<!-- HTML tag -->
<html lang="he" dir="rtl">

<!-- Bootstrap RTL CSS -->
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css">

/* Custom RTL overrides */
.text-start { text-align: right !important; }
.ms-auto { margin-right: auto !important; margin-left: 0 !important; }
```

---

## 9. Responsive Breakpoints

| Breakpoint | גודל מסך | שינויים עיקריים |
|------------|---------|----------------|
| xs | < 576px | ניווט מקורס, כרטיסי רכב בעמודה אחת |
| sm | 576–768px | כרטיסי רכב 2 בשורה |
| md | 768–992px | כרטיסי רכב 2–3 בשורה |
| lg | 992–1200px | Layout מלא עם sidebar |
| xl | > 1200px | Full Desktop experience |

---

## 10. Booking Wizard Progress Bar

```html
<div class="booking-progress">
  <div class="progress-steps d-flex justify-content-between position-relative">
    <div class="step completed">
      <div class="step-circle"><i class="bi bi-check"></i></div>
      <span>בחירת רכב</span>
    </div>
    <div class="step active">
      <div class="step-circle">2</div>
      <span>תוספות</span>
    </div>
    <div class="step">
      <div class="step-circle">3</div>
      <span>פרטים</span>
    </div>
    <div class="step">
      <div class="step-circle">4</div>
      <span>תשלום</span>
    </div>
    <div class="progress-line"></div>
  </div>
</div>
```

---

*גרסה: 1.0 | DriveFlow — UI Design Guide*
