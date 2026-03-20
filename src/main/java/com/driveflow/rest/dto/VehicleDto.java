package com.driveflow.rest.dto;

import com.driveflow.entity.Vehicle;
import com.driveflow.enums.FuelType;
import com.driveflow.enums.TransmissionType;
import com.driveflow.enums.VehicleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "פרטי רכב")
public class VehicleDto {

    @Schema(description = "מזהה ייחודי", example = "1")
    private Long id;

    @Schema(description = "לוחית רישוי", example = "12-345-67")
    private String licensePlate;

    @Schema(description = "שם מלא של הרכב", example = "Toyota Corolla 2024")
    private String displayName;

    @Schema(description = "יצרן", example = "Toyota")
    private String make;

    @Schema(description = "דגם", example = "Corolla")
    private String model;

    @Schema(description = "שנת ייצור", example = "2024")
    private Integer year;

    @Schema(description = "צבע", example = "לבן")
    private String color;

    @Schema(description = "קטגוריה")
    private String categoryName;

    @Schema(description = "סניף מיקום")
    private String branchName;

    @Schema(description = "תיבת הילוכים", example = "AUTOMATIC")
    private TransmissionType transmission;

    @Schema(description = "סוג דלק", example = "PETROL")
    private FuelType fuelType;

    @Schema(description = "מספר מושבים", example = "5")
    private Integer numSeats;

    @Schema(description = "מספר דלתות", example = "5")
    private Integer numDoors;

    @Schema(description = "מחיר יומי ב-₪", example = "185.00")
    private BigDecimal dailyRate;

    @Schema(description = "סטטוס הרכב", example = "AVAILABLE")
    private VehicleStatus status;

    @Schema(description = "מזגן", example = "true")
    private Boolean hasAc;

    @Schema(description = "בלוטות'", example = "true")
    private Boolean hasBluetooth;

    @Schema(description = "GPS מובנה", example = "false")
    private Boolean hasGpsBuiltin;

    @Schema(description = "תמונה ראשית URL")
    private String mainImageUrl;

    public static VehicleDto from(Vehicle v) {
        return VehicleDto.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .displayName(v.getDisplayName())
                .make(v.getMake())
                .model(v.getModel())
                .year(v.getYear())
                .color(v.getColor())
                .categoryName(v.getCategory() != null ? v.getCategory().getName() : null)
                .branchName(v.getBranch() != null ? v.getBranch().getName() : null)
                .transmission(v.getTransmission())
                .fuelType(v.getFuelType())
                .numSeats(v.getNumSeats())
                .numDoors(v.getNumDoors())
                .dailyRate(v.getDailyRate())
                .status(v.getStatus())
                .hasAc(v.isHasAc())
                .hasBluetooth(v.isHasBluetooth())
                .hasGpsBuiltin(v.isHasGpsBuiltin())
                .mainImageUrl(v.getMainImageUrl())
                .build();
    }
}
