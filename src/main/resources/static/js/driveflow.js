/* driveflow.js — Interactive micro-animations */
(function () {
  'use strict';

  /* ── 1. Navbar shadow on scroll ─────────────────────────────────── */
  const navbar = document.querySelector('.navbar-driveflow');
  if (navbar) {
    const syncNavbar = () => navbar.classList.toggle('navbar-scrolled', window.scrollY > 30);
    window.addEventListener('scroll', syncNavbar, { passive: true });
    syncNavbar();
  }

  /* ── 2. Scroll-reveal (IntersectionObserver) ────────────────────── */
  const REVEAL_SEL = [
    '.category-card',
    '.vehicle-card',
    '.feature-card',
    '.branch-list-item',
    '.kpi-card',
  ].join(',');

  if ('IntersectionObserver' in window) {
    const revealObs = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
          revealObs.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -30px 0px' });

    document.querySelectorAll(REVEAL_SEL).forEach((el) => {
      const rect = el.getBoundingClientRect();
      // Only animate elements that start below the fold
      if (rect.top > window.innerHeight * 0.92) {
        el.classList.add('reveal');
        // Stagger siblings in the same row
        const siblings = el.parentElement
          ? [...el.parentElement.children].filter(c => c.matches && c.matches(REVEAL_SEL))
          : [];
        const idx = siblings.indexOf(el);
        if (idx > 0) el.style.transitionDelay = (idx * 0.09) + 's';
        revealObs.observe(el);
      }
    });
  }

  /* ── 3. Animated number counters ────────────────────────────────── */
  if ('IntersectionObserver' in window) {
    const counterObs = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (!entry.isIntersecting) return;
        const el = entry.target;
        const target = parseInt(el.dataset.counter, 10);
        const suffix = el.dataset.suffix || '';
        const duration = 1500;
        const start = performance.now();
        const tick = (now) => {
          const p = Math.min((now - start) / duration, 1);
          const ease = 1 - Math.pow(1 - p, 3); // ease-out cubic
          el.textContent = Math.round(ease * target).toLocaleString('he-IL') + suffix;
          if (p < 1) requestAnimationFrame(tick);
        };
        requestAnimationFrame(tick);
        counterObs.unobserve(el);
      });
    }, { threshold: 0.5 });

    document.querySelectorAll('[data-counter]').forEach(el => counterObs.observe(el));
  }

  /* ── 4. Ripple effect on primary buttons ─────────────────────────── */
  function addRipple(e) {
    const btn = e.currentTarget;
    const rect = btn.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const clickX = typeof e.clientX === 'number' ? e.clientX : rect.left + rect.width / 2;
    const clickY = typeof e.clientY === 'number' ? e.clientY : rect.top + rect.height / 2;
    const wave = document.createElement('span');
    wave.className = 'ripple-wave';
    wave.style.width  = size + 'px';
    wave.style.height = size + 'px';
    wave.style.top    = (clickY - rect.top  - size / 2) + 'px';
    wave.style.left   = (clickX - rect.left - size / 2) + 'px';
    btn.appendChild(wave);
    wave.addEventListener('animationend', () => wave.remove());
  }

  document.querySelectorAll('.btn-primary, .btn-gold, .btn-search, .btn-accent').forEach(btn => {
    btn.addEventListener('click', addRipple);
  });

  /* ── 5. Smooth anchor scrolling ──────────────────────────────────── */
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', e => {
      const target = document.querySelector(a.getAttribute('href'));
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });

  /* ── 6. Pickup/Return datetime safeguards ─────────────────────────── */
  const pickupInputs = document.querySelectorAll('input[type="datetime-local"][name="pickupDate"]');
  const returnInputs = document.querySelectorAll('input[type="datetime-local"][name="returnDate"]');

  function toLocalInputValue(date) {
    const pad = (n) => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
  }

  function ensureDateRange(pickupInput, returnInput) {
    const now = new Date();
    now.setMinutes(0, 0, 0);
    now.setHours(now.getHours() + 1);
    const minPickup = toLocalInputValue(now);
    pickupInput.min = minPickup;

    if (!pickupInput.value) {
      const defaultPickup = new Date(now);
      defaultPickup.setHours(10, 0, 0, 0);
      if (defaultPickup < now) defaultPickup.setDate(defaultPickup.getDate() + 1);
      pickupInput.value = toLocalInputValue(defaultPickup);
    }

    const pickupDate = new Date(pickupInput.value);
    const minReturnDate = new Date(pickupDate);
    minReturnDate.setHours(minReturnDate.getHours() + 1);
    returnInput.min = toLocalInputValue(minReturnDate);

    if (!returnInput.value || new Date(returnInput.value) <= pickupDate) {
      const defaultReturn = new Date(pickupDate);
      defaultReturn.setDate(defaultReturn.getDate() + 3);
      returnInput.value = toLocalInputValue(defaultReturn);
    }
  }

  pickupInputs.forEach((pickupInput, idx) => {
    const returnInput = returnInputs[idx];
    if (!returnInput) return;
    ensureDateRange(pickupInput, returnInput);
    pickupInput.addEventListener('change', () => ensureDateRange(pickupInput, returnInput));
    returnInput.addEventListener('change', () => ensureDateRange(pickupInput, returnInput));
  });

})();
