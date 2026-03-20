package com.driveflow.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "עטיפת תשובה אחידה")
public class ApiResponse<T> {

    @Schema(description = "הצלחה / כישלון", example = "true")
    private boolean success;

    @Schema(description = "הנתונים שהתבקשו")
    private T data;

    @Schema(description = "הודעה", example = "OK")
    private String message;

    @Schema(description = "חותמת זמן")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).data(data).message("OK").build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).build();
    }
}
