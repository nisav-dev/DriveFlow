package com.driveflow.service;

import com.driveflow.entity.*;
import com.driveflow.enums.BookingStatus;
import com.driveflow.enums.VehicleStatus;
import com.driveflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final ExtraRepository extraRepository;

    private static final AtomicLong sequence = new AtomicLong(1);

    public List<Booking> findAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> findByNumber(String number) {
        return bookingRepository.findByBookingNumber(number);
    }

    public List<Booking> findByCustomer(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Booking> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional
    public Booking createBooking(Long customerId, Long vehicleId, Long pickupBranchId,
                                  Long returnBranchId, LocalDateTime pickupDatetime,
                                  LocalDateTime returnDatetime, List<Long> extraIds) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Branch pickupBranch = branchRepository.findById(pickupBranchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        Branch returnBranch = branchRepository.findById(returnBranchId)
                .orElse(pickupBranch);

        int numDaysRaw = (int) ChronoUnit.DAYS.between(pickupDatetime.toLocalDate(), returnDatetime.toLocalDate());
        final int numDays = numDaysRaw < 1 ? 1 : numDaysRaw;

        BigDecimal basePrice = vehicle.calculateRentalPrice(numDays);
        BigDecimal extrasPrice = BigDecimal.ZERO;

        Booking booking = Booking.builder()
                .bookingNumber(generateBookingNumber())
                .customer(customer)
                .vehicle(vehicle)
                .pickupBranch(pickupBranch)
                .returnBranch(returnBranch)
                .pickupDatetime(pickupDatetime)
                .returnDatetime(returnDatetime)
                .numDays(numDays)
                .basePrice(basePrice)
                .extrasPrice(extrasPrice)
                .additionalCharges(BigDecimal.ZERO)
                .totalPrice(basePrice)
                .status(BookingStatus.PENDING)
                .build();

        if (extraIds != null && !extraIds.isEmpty()) {
            for (Long extraId : extraIds) {
                extraRepository.findById(extraId).ifPresent(extra -> {
                    BigDecimal price = extra.calculatePrice(numDays);
                    BookingExtra be = BookingExtra.builder()
                            .extra(extra)
                            .quantity(1)
                            .priceCharged(price)
                            .build();
                    booking.addBookingExtra(be);
                    booking.setExtrasPrice(booking.getExtrasPrice().add(price));
                });
            }
            booking.setTotalPrice(basePrice.add(booking.getExtrasPrice()));
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirm(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancel(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking markActive(Long bookingId, int pickupMileage) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setPickupMileage(pickupMileage);
        booking.getVehicle().setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(booking.getVehicle());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking complete(Long bookingId, int returnMileage, boolean hasDamage) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setActualReturnDatetime(LocalDateTime.now());
        booking.setReturnMileage(returnMileage);

        if (booking.isLateReturn()) {
            BigDecimal lateFee = booking.calculateLateFee();
            booking.setAdditionalCharges(lateFee);
            booking.setTotalPrice(booking.getTotalPrice().add(lateFee));
        }

        booking.setStatus(hasDamage ? BookingStatus.COMPLETED_WITH_DAMAGE : BookingStatus.COMPLETED);
        VehicleStatus nextStatus = hasDamage ? VehicleStatus.MAINTENANCE : VehicleStatus.AVAILABLE;
        booking.getVehicle().setStatus(nextStatus);
        booking.getVehicle().updateMileage(returnMileage);
        vehicleRepository.save(booking.getVehicle());
        return bookingRepository.save(booking);
    }

    public long countActive() {
        return bookingRepository.countByStatusIn(List.of(BookingStatus.CONFIRMED, BookingStatus.ACTIVE));
    }

    public BigDecimal getMonthlyRevenue() {
        BigDecimal result = bookingRepository.sumRevenueFrom(
                List.of(BookingStatus.COMPLETED, BookingStatus.COMPLETED_WITH_DAMAGE),
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        return result != null ? result : BigDecimal.ZERO;
    }

    private String generateBookingNumber() {
        int year = LocalDateTime.now().getYear();
        long seq = bookingRepository.count() + sequence.getAndIncrement();
        return String.format("DRIVEFLOW-%d-%05d", year, seq);
    }
}
