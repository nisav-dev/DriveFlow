package com.driveflow.rest.dto;

import com.driveflow.entity.Booking;
import com.driveflow.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "פרטי הזמנה")
public class BookingDto {

    @Schema(description = "מזהה ייחודי", example = "1")
    private Long id;

    @Schema(description = "מספר הזמנה", example = "DRIVEFLOW-2026-00001")
    private String bookingNumber;

    @Schema(description = "שם לקוח")
    private String customerName;

    @Schema(description = "רכב")
    private String vehicleName;

    @Schema(description = "לוחית רישוי", example = "12-345-67")
    private String licensePlate;

    @Schema(description = "סניף איסוף")
    private String pickupBranchName;

    @Schema(description = "סניף החזרה")
    private String returnBranchName;

    @Schema(description = "תאריך ושעת איסוף")
    private LocalDateTime pickupDatetime;

    @Schema(description = "תאריך ושעת החזרה מתוכנן")
    private LocalDateTime returnDatetime;

    @Schema(description = "מספר ימים", example = "5")
    private Integer numDays;

    @Schema(description = "מחיר בסיס ב-₪", example = "925.00")
    private BigDecimal basePrice;

    @Schema(description = "מחיר תוספות ב-₪", example = "75.00")
    private BigDecimal extrasPrice;

    @Schema(description = "חיובים נוספים ב-₪ (איחור וכו')", example = "0.00")
    private BigDecimal additionalCharges;

    @Schema(description = "מחיר סופי ב-₪", example = "1000.00")
    private BigDecimal totalPrice;

    @Schema(description = "סטטוס הזמנה", example = "CONFIRMED")
    private BookingStatus status;

    public static BookingDto from(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .bookingNumber(b.getBookingNumber())
                .customerName(b.getCustomer() != null ? b.getCustomer().getUser().getFullName() : null)
                .vehicleName(b.getVehicle() != null ? b.getVehicle().getDisplayName() : null)
                .licensePlate(b.getVehicle() != null ? b.getVehicle().getLicensePlate() : null)
                .pickupBranchName(b.getPickupBranch() != null ? b.getPickupBranch().getName() : null)
                .returnBranchName(b.getReturnBranch() != null ? b.getReturnBranch().getName() : null)
                .pickupDatetime(b.getPickupDatetime())
                .returnDatetime(b.getReturnDatetime())
                .numDays(b.getNumDays())
                .basePrice(b.getBasePrice())
                .extrasPrice(b.getExtrasPrice())
                .additionalCharges(b.getAdditionalCharges())
                .totalPrice(b.getTotalPrice())
                .status(b.getStatus())
                .build();
    }
}
