package com.driveflow.rest.dto;

import com.driveflow.entity.Branch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "פרטי סניף")
public class BranchDto {

    @Schema(description = "מזהה ייחודי", example = "1")
    private Long id;

    @Schema(description = "שם הסניף", example = "תל אביב — מרכז")
    private String name;

    @Schema(description = "עיר", example = "תל אביב")
    private String city;

    @Schema(description = "כתובת מלאה", example = "דרך מנחם בגין 132")
    private String address;

    @Schema(description = "טלפון", example = "03-1234567")
    private String phone;

    @Schema(description = "שעות פעילות", example = "א-ה 07:00-21:00 | ו 07:00-15:00")
    private String openingHours;

    @Schema(description = "קו רוחב (GPS)", example = "32.0853")
    private Double latitude;

    @Schema(description = "קו אורך (GPS)", example = "34.7818")
    private Double longitude;

    @Schema(description = "סניף שדה תעופה", example = "false")
    private Boolean airportBranch;

    @Schema(description = "פעיל", example = "true")
    private Boolean active;

    public static BranchDto from(Branch b) {
        return BranchDto.builder()
                .id(b.getId())
                .name(b.getName())
                .city(b.getCity())
                .address(b.getAddress())
                .phone(b.getPhone())
                .openingHours(b.getOpeningHours())
                .latitude(b.getLatitude())
                .longitude(b.getLongitude())
                .airportBranch(b.isAirportBranch())
                .active(b.isActive())
                .build();
    }
}
