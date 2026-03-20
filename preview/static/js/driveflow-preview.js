const DriveFlowPreview = (() => {
  const STORAGE_KEY = "driveflow-preview-state";
  const branches = [
    'נתב"ג — שדה התעופה',
    'תל אביב — מרכז',
    'ירושלים — מרכז',
    'חיפה — נמל',
    'אילת — טיילת',
    'באר שבע — מרכז',
    'הרצליה — פיתוח'
  ];

  const vehicles = [
    { id: "i20", name: "Hyundai i20 2023", category: "Economy", pricePerDay: 150, transmission: "אוטומטי", features: ["Bluetooth", "מיזוג"], seats: 5, popularity: 89, image: "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=500&h=280&fit=crop" },
    { id: "corolla", name: "Toyota Corolla 2024", category: "Compact", pricePerDay: 185, transmission: "אוטומטי", features: ["Bluetooth", "מיזוג", "היברידי"], seats: 5, popularity: 96, image: "https://images.unsplash.com/photo-1590362891991-f776e747a588?w=500&h=280&fit=crop" },
    { id: "golf", name: "Volkswagen Golf 2024", category: "Compact", pricePerDay: 195, transmission: "אוטומטי", features: ["Bluetooth", "GPS"], seats: 5, popularity: 84, image: "https://images.unsplash.com/photo-1619767886558-efdc259cde1a?w=500&h=280&fit=crop" },
    { id: "rav4", name: "Toyota RAV4 2024", category: "SUV", pricePerDay: 320, transmission: "אוטומטי", features: ["מיזוג", "היברידי", "GPS"], seats: 5, popularity: 92, image: "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=500&h=280&fit=crop" },
    { id: "bmw7", name: "BMW 7 Series 2024", category: "Luxury", pricePerDay: 850, transmission: "אוטומטי", features: ["Bluetooth", "GPS"], seats: 5, popularity: 72, image: "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=500&h=280&fit=crop" },
    { id: "sportage", name: "Kia Sportage 2024", category: "SUV", pricePerDay: 315, transmission: "אוטומטי", features: ["Bluetooth", "מיזוג"], seats: 5, popularity: 87, image: "https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?w=500&h=280&fit=crop" }
  ];

  function defaultPickup() {
    return "2026-04-10T10:00";
  }

  function defaultDropoff() {
    return "2026-04-15T10:00";
  }

  function defaultState() {
    const selectedVehicle = vehicles.find((vehicle) => vehicle.id === "corolla");
    return {
      branch: branches[0],
      pickup: defaultPickup(),
      dropoff: defaultDropoff(),
      selectedVehicleId: selectedVehicle.id,
      extras: [],
      customer: {
        name: "יוסי כהן",
        email: "yossi.cohen@gmail.com",
        phone: "050-1234567",
        license: "1234567"
      },
      paymentMethod: "card",
      confirmationNumber: "DRIVEFLOW-2026-00123"
    };
  }

  function loadState() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      const parsed = raw ? JSON.parse(raw) : {};
      return { ...defaultState(), ...parsed };
    } catch (_error) {
      return defaultState();
    }
  }

  function saveState(nextState) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(nextState));
  }

  function getSelectedVehicle(state) {
    return vehicles.find((vehicle) => vehicle.id === state.selectedVehicleId) || vehicles[1];
  }

  function ensureValidDates(state) {
    if (!state.pickup) state.pickup = defaultPickup();
    if (!state.dropoff) state.dropoff = defaultDropoff();
    if (new Date(state.dropoff) <= new Date(state.pickup)) {
      const pickupDate = new Date(state.pickup);
      pickupDate.setDate(pickupDate.getDate() + 5);
      state.dropoff = pickupDate.toISOString().slice(0, 16);
    }
    return state;
  }

  function getRentalDays(state) {
    const start = new Date(state.pickup);
    const end = new Date(state.dropoff);
    const diff = end.getTime() - start.getTime();
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return Math.max(days, 1);
  }

  function formatCurrency(amount) {
    return `₪${amount.toLocaleString("he-IL")}`;
  }

  function formatDateTime(value) {
    if (!value) return "";
    return new Intl.DateTimeFormat("he-IL", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    }).format(new Date(value));
  }

  function getExtrasPriced(state) {
    const days = getRentalDays(state);
    return (state.extras || []).map((extra) => ({
      ...extra,
      total: extra.mode === "daily" ? extra.price * days : extra.price
    }));
  }

  function getTotals(state) {
    const vehicle = getSelectedVehicle(state);
    const baseTotal = vehicle.pricePerDay * getRentalDays(state);
    const extrasTotal = getExtrasPriced(state).reduce((sum, extra) => sum + extra.total, 0);
    return { baseTotal, extrasTotal, grandTotal: baseTotal + extrasTotal };
  }

  function populateBranchOptions(select, selectedValue) {
    select.innerHTML = "";
    branches.forEach((branch) => {
      const option = document.createElement("option");
      option.value = branch;
      option.textContent = branch;
      option.selected = branch === selectedValue;
      select.appendChild(option);
    });
  }

  function bindQuickLinks(state) {
    document.querySelectorAll("[data-category-link]").forEach((link) => {
      link.addEventListener("click", () => {
        state.category = link.dataset.categoryLink;
        saveState(state);
      });
    });

    document.querySelectorAll("[data-branch-link]").forEach((link) => {
      link.addEventListener("click", () => {
        state.branch = link.dataset.branchLink;
        saveState(state);
      });
    });

    document.querySelectorAll("[data-vehicle-link]").forEach((link) => {
      link.addEventListener("click", () => {
        const vehicle = vehicles.find((item) => item.name === link.dataset.vehicleLink);
        if (vehicle) {
          state.selectedVehicleId = vehicle.id;
          saveState(state);
        }
      });
    });
  }

  function initHome() {
    const state = ensureValidDates(loadState());
    const form = document.getElementById("homeSearchForm");
    if (!form) return;
    const branch = document.getElementById("homeBranch");
    const pickup = document.getElementById("homePickup");
    const dropoff = document.getElementById("homeDropoff");
    branch.value = state.branch || "";
    pickup.value = state.pickup;
    dropoff.value = state.dropoff;
    bindQuickLinks(state);

    form.addEventListener("submit", (event) => {
      event.preventDefault();
      state.branch = branch.value || branches[0];
      state.pickup = pickup.value || defaultPickup();
      state.dropoff = dropoff.value || defaultDropoff();
      ensureValidDates(state);
      saveState(state);
      window.location.href = "search.html";
    });
  }

  function renderResults(vehiclesToRender, state) {
    const grid = document.getElementById("resultsGrid");
    const summary = document.getElementById("resultsSummary");
    const emptyState = document.getElementById("emptyState");
    const selectedFilters = document.getElementById("selectedFilters");
    const days = getRentalDays(state);
    grid.innerHTML = "";
    selectedFilters.innerHTML = "";

    if (state.branch) {
      selectedFilters.appendChild(buildChip(state.branch));
    }
    if (state.category) {
      selectedFilters.appendChild(buildChip(state.category));
    }
    summary.textContent = `${vehiclesToRender.length} רכבים זמינים · ${state.branch} · ${days} ימים`;

    if (!vehiclesToRender.length) {
      emptyState.classList.remove("d-none");
      return;
    }

    emptyState.classList.add("d-none");
    vehiclesToRender.forEach((vehicle) => {
      const total = vehicle.pricePerDay * days;
      const column = document.createElement("div");
      column.className = "col-md-6 col-xl-4";
      column.innerHTML = `
        <article class="vehicle-card result-card">
          <div class="position-relative">
            <img src="${vehicle.image}" alt="${vehicle.name}">
            <span class="vehicle-badge ${vehicle.category === "SUV" ? "suv" : vehicle.category === "Luxury" ? "luxury" : ""}">${vehicle.category}</span>
          </div>
          <div class="p-3 p-lg-4">
            <h3 class="vehicle-title">${vehicle.name}</h3>
            <div class="vehicle-specs mb-3">
              <span class="spec-item"><i class="bi bi-people"></i>${vehicle.seats} מושבים</span>
              <span class="spec-item"><i class="bi bi-gear"></i>${vehicle.transmission}</span>
              <span class="spec-item"><i class="bi bi-stars"></i>${vehicle.features[0]}</span>
            </div>
            <div class="result-card-meta">
              <div>
                <div><span class="price-amount">${formatCurrency(vehicle.pricePerDay)}</span> <span class="price-unit">ליום</span></div>
                <div class="result-total">סה"כ לתקופה: ${formatCurrency(total)}</div>
              </div>
              <button type="button" class="btn btn-primary rounded-3" data-book-vehicle="${vehicle.id}">בחר</button>
            </div>
          </div>
        </article>
      `;
      grid.appendChild(column);
    });

    document.querySelectorAll("[data-book-vehicle]").forEach((button) => {
      button.addEventListener("click", () => {
        state.selectedVehicleId = button.dataset.bookVehicle;
        saveState(state);
        window.location.href = "booking.html";
      });
    });
  }

  function buildChip(text) {
    const chip = document.createElement("span");
    chip.className = "filter-chip";
    chip.textContent = text;
    return chip;
  }

  function initSearch() {
    const state = ensureValidDates(loadState());
    const form = document.getElementById("searchForm");
    const branch = document.getElementById("searchBranch");
    const pickup = document.getElementById("searchPickup");
    const dropoff = document.getElementById("searchDropoff");
    const priceRange = document.getElementById("priceRange");
    const priceRangeValue = document.getElementById("priceRangeValue");
    const sortSelect = document.getElementById("sortSelect");
    const resetBtn = document.getElementById("resetFiltersBtn");
    const emptyResetBtn = document.getElementById("emptyResetBtn");

    populateBranchOptions(branch, state.branch);
    pickup.value = state.pickup;
    dropoff.value = state.dropoff;

    const applyFilters = () => {
      state.branch = branch.value;
      state.pickup = pickup.value || defaultPickup();
      state.dropoff = dropoff.value || defaultDropoff();
      ensureValidDates(state);

      const categories = [...document.querySelectorAll(".category-filter:checked")].map((item) => item.value);
      const transmissions = [...document.querySelectorAll(".transmission-filter:checked")].map((item) => item.value);
      const features = [...document.querySelectorAll(".feature-filter:checked")].map((item) => item.value);
      const maxPrice = Number(priceRange.value);
      const sort = sortSelect.value;

      let filtered = [...vehicles].filter((vehicle) => vehicle.pricePerDay <= maxPrice);
      if (categories.length) filtered = filtered.filter((vehicle) => categories.includes(vehicle.category));
      if (transmissions.length) filtered = filtered.filter((vehicle) => transmissions.includes(vehicle.transmission));
      if (features.length) filtered = filtered.filter((vehicle) => features.every((feature) => vehicle.features.includes(feature)));
      if (state.category && !categories.length) filtered = filtered.filter((vehicle) => vehicle.category === state.category);

      filtered.sort((left, right) => {
        if (sort === "price-desc") return right.pricePerDay - left.pricePerDay;
        if (sort === "name") return left.name.localeCompare(right.name, "he");
        if (sort === "popular") return right.popularity - left.popularity;
        return left.pricePerDay - right.pricePerDay;
      });

      priceRangeValue.textContent = formatCurrency(maxPrice);
      saveState(state);
      renderResults(filtered, state);
    };

    form.addEventListener("submit", (event) => {
      event.preventDefault();
      applyFilters();
    });

    [priceRange, sortSelect, ...document.querySelectorAll(".category-filter, .transmission-filter, .feature-filter")].forEach((input) => {
      input.addEventListener("input", applyFilters);
      input.addEventListener("change", applyFilters);
    });

    const resetFilters = () => {
      document.querySelectorAll(".category-filter, .transmission-filter, .feature-filter").forEach((input) => {
        input.checked = false;
      });
      state.category = "";
      priceRange.value = "900";
      sortSelect.value = "price-asc";
      applyFilters();
    };

    resetBtn.addEventListener("click", resetFilters);
    emptyResetBtn.addEventListener("click", resetFilters);
    applyFilters();
  }

  function renderBookingSummary(state) {
    const vehicle = getSelectedVehicle(state);
    const totals = getTotals(state);
    const extras = getExtrasPriced(state);
    document.getElementById("bookingVehicleImage").src = vehicle.image;
    document.getElementById("bookingVehicleName").textContent = vehicle.name;
    document.getElementById("bookingVehicleMeta").textContent = `${vehicle.category} · ${vehicle.transmission} · ${vehicle.seats} מושבים`;
    document.getElementById("bookingBranch").textContent = state.branch;
    document.getElementById("bookingPickup").textContent = formatDateTime(state.pickup);
    document.getElementById("bookingDropoff").textContent = formatDateTime(state.dropoff);
    document.getElementById("bookingDays").textContent = `${getRentalDays(state)} ימים`;
    document.getElementById("bookingBaseTotal").textContent = formatCurrency(totals.baseTotal);
    document.getElementById("bookingExtrasTotal").textContent = formatCurrency(totals.extrasTotal);
    document.getElementById("bookingGrandTotal").textContent = formatCurrency(totals.grandTotal);

    const list = document.getElementById("selectedExtrasList");
    list.innerHTML = "";
    extras.forEach((extra) => {
      const item = document.createElement("div");
      item.className = "selected-extra-pill";
      item.innerHTML = `<span>${extra.name}</span><strong>${formatCurrency(extra.total)}</strong>`;
      list.appendChild(item);
    });
  }

  function initBooking() {
    const state = ensureValidDates(loadState());
    document.getElementById("driverName").value = state.customer.name;
    document.getElementById("driverEmail").value = state.customer.email;
    document.getElementById("driverPhone").value = state.customer.phone;
    document.getElementById("driverLicense").value = state.customer.license;

    document.querySelectorAll(".extra-item").forEach((item) => {
      const checkbox = item.querySelector(".extra-checkbox");
      const isSelected = (state.extras || []).some((extra) => extra.id === item.dataset.extraId);
      checkbox.checked = isSelected;
      item.classList.toggle("is-selected", isSelected);
      item.addEventListener("change", syncExtras);
    });

    function syncExtras() {
      state.extras = [...document.querySelectorAll(".extra-item")].filter((item) => item.querySelector(".extra-checkbox").checked).map((item) => ({
        id: item.dataset.extraId,
        name: item.dataset.extraName,
        price: Number(item.dataset.extraPrice),
        mode: item.dataset.extraMode
      }));

      document.querySelectorAll(".extra-item").forEach((item) => {
        item.classList.toggle("is-selected", item.querySelector(".extra-checkbox").checked);
      });

      saveState(state);
      renderBookingSummary(state);
    }

    document.getElementById("continueToPaymentBtn").addEventListener("click", () => {
      const customer = {
        name: document.getElementById("driverName").value.trim(),
        email: document.getElementById("driverEmail").value.trim(),
        phone: document.getElementById("driverPhone").value.trim(),
        license: document.getElementById("driverLicense").value.trim()
      };

      if (!customer.name || !customer.email || !customer.phone || !customer.license) {
        alert("יש למלא את כל פרטי הנהג לפני המשך לתשלום.");
        return;
      }

      state.customer = customer;
      saveState(state);
      window.location.href = "payment.html";
    });

    renderBookingSummary(state);
  }

  function renderPaymentSummary(state) {
    const vehicle = getSelectedVehicle(state);
    const totals = getTotals(state);
    document.getElementById("paymentVehicleName").textContent = vehicle.name;
    document.getElementById("paymentBaseTotal").textContent = formatCurrency(totals.baseTotal);
    document.getElementById("paymentExtrasTotal").textContent = formatCurrency(totals.extrasTotal);
    document.getElementById("paymentGrandTotal").textContent = formatCurrency(totals.grandTotal);
    document.getElementById("payNowLabel").textContent = `שלם ${formatCurrency(totals.grandTotal)} עכשיו`;

    const extrasList = document.getElementById("paymentExtrasList");
    extrasList.innerHTML = "";
    getExtrasPriced(state).forEach((extra) => {
      const item = document.createElement("div");
      item.className = "selected-extra-pill";
      item.innerHTML = `<span>${extra.name}</span><strong>${formatCurrency(extra.total)}</strong>`;
      extrasList.appendChild(item);
    });
  }

  function initPayment() {
    const state = ensureValidDates(loadState());
    const paymentMethodGroup = document.getElementById("paymentMethodGroup");
    const paymentForm = document.getElementById("paymentForm");
    const paymentError = document.getElementById("paymentError");
    const loader = document.getElementById("payLoader");
    const cardFields = document.getElementById("cardFields");
    const paypalFields = document.getElementById("paypalFields");

    const toggleMethod = (method) => {
      state.paymentMethod = method;
      saveState(state);
      paymentMethodGroup.querySelectorAll("button").forEach((button) => {
        button.classList.toggle("btn-outline-primary", button.dataset.method === method);
        button.classList.toggle("btn-outline-secondary", button.dataset.method !== method);
        button.classList.toggle("active", button.dataset.method === method);
      });
      cardFields.classList.toggle("d-none", method !== "card");
      paypalFields.classList.toggle("d-none", method !== "paypal");
    };

    paymentMethodGroup.querySelectorAll("button").forEach((button) => {
      button.addEventListener("click", () => toggleMethod(button.dataset.method));
    });

    paymentForm.addEventListener("submit", (event) => {
      event.preventDefault();
      paymentError.classList.add("d-none");

      if (state.paymentMethod === "card") {
        const cardHolder = document.getElementById("cardHolder").value.trim();
        const cardNumber = document.getElementById("cardNumber").value.replace(/\s+/g, "");
        const cardExpiry = document.getElementById("cardExpiry").value.trim();
        const cardCvv = document.getElementById("cardCvv").value.trim();

        if (!cardHolder || cardNumber.length < 12 || !/^\d{2}\/\d{2}$/.test(cardExpiry) || cardCvv.length < 3) {
          paymentError.textContent = "יש להשלים פרטי כרטיס תקינים לפני ביצוע התשלום.";
          paymentError.classList.remove("d-none");
          return;
        }
      }

      loader.classList.remove("d-none");
      const payButton = document.getElementById("payNowBtn");
      payButton.disabled = true;
      state.confirmationNumber = `DRIVEFLOW-${new Date().getFullYear()}-${String(Math.floor(Math.random() * 90000) + 10000)}`;
      saveState(state);

      setTimeout(() => {
        window.location.href = "confirmation.html";
      }, 1200);
    });

    toggleMethod(state.paymentMethod || "card");
    renderPaymentSummary(state);
  }

  function initConfirmation() {
    const state = ensureValidDates(loadState());
    const vehicle = getSelectedVehicle(state);
    const totals = getTotals(state);

    document.getElementById("confirmationEmail").textContent = state.customer.email;
    document.getElementById("confirmationNumber").textContent = state.confirmationNumber;
    document.getElementById("confirmationVehicle").textContent = vehicle.name;
    document.getElementById("confirmationVehicleMeta").textContent = `${vehicle.category} · ${vehicle.transmission} · ${vehicle.seats} מושבים`;
    document.getElementById("confirmationBranch").textContent = state.branch;
    document.getElementById("confirmationPickup").textContent = formatDateTime(state.pickup);
    document.getElementById("confirmationDropoff").textContent = formatDateTime(state.dropoff);
    document.getElementById("confirmationPaymentMethod").textContent = `אמצעי תשלום: ${state.paymentMethod === "paypal" ? "PayPal" : "כרטיס אשראי"}`;
    document.getElementById("confirmationBaseTotal").textContent = formatCurrency(totals.baseTotal);
    document.getElementById("confirmationExtrasTotal").textContent = formatCurrency(totals.extrasTotal);
    document.getElementById("confirmationGrandTotal").textContent = formatCurrency(totals.grandTotal);

    const extrasList = document.getElementById("confirmationExtrasList");
    extrasList.innerHTML = "";
    getExtrasPriced(state).forEach((extra) => {
      const item = document.createElement("div");
      item.className = "selected-extra-pill";
      item.innerHTML = `<span>${extra.name}</span><strong>${formatCurrency(extra.total)}</strong>`;
      extrasList.appendChild(item);
    });
  }

  function initAdmin() {
    const menuToggle = document.getElementById("adminMenuToggle");
    const sidebar = document.getElementById("adminSidebar");
    const backdrop = document.getElementById("adminBackdrop");
    if (menuToggle && sidebar && backdrop) {
      const toggleMenu = (open) => {
        sidebar.classList.toggle("is-open", open);
        backdrop.classList.toggle("is-visible", open);
      };
      menuToggle.addEventListener("click", () => toggleMenu(!sidebar.classList.contains("is-open")));
      backdrop.addEventListener("click", () => toggleMenu(false));
    }

    if (window.Chart) {
      new Chart(document.getElementById("revenueChart"), {
        type: "bar",
        data: {
          labels: ["שבוע 1", "שבוע 2", "שבוע 3", "שבוע 4"],
          datasets: [{ data: [18200, 22400, 24100, 22750], backgroundColor: "rgba(23,58,89,0.86)", borderRadius: 10, borderSkipped: false }]
        },
        options: {
          plugins: { legend: { display: false } },
          scales: {
            y: { ticks: { callback: (value) => `₪${value.toLocaleString("he-IL")}` }, grid: { color: "#eef2f6" } },
            x: { grid: { display: false } }
          }
        }
      });

      new Chart(document.getElementById("categoryChart"), {
        type: "doughnut",
        data: {
          labels: ["Economy", "Compact", "SUV", "Mini", "Luxury"],
          datasets: [{ data: [28, 22, 20, 12, 8], backgroundColor: ["#173a59", "#2f6d9e", "#d2a73f", "#28a745", "#9aa6b2"], borderWidth: 0 }]
        },
        options: {
          cutout: "68%",
          plugins: { legend: { position: "bottom", labels: { font: { family: "Heebo", size: 11 } } } }
        }
      });
    }
  }

  function init() {
    const page = document.body.dataset.page;
    if (page === "home") initHome();
    if (page === "search") initSearch();
    if (page === "booking") initBooking();
    if (page === "payment") initPayment();
    if (page === "confirmation") initConfirmation();
    if (page === "admin") initAdmin();
  }

  return { init };
})();

document.addEventListener("DOMContentLoaded", DriveFlowPreview.init);
