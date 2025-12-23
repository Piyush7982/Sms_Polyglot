package org.meesho.sms_sender.Classes;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private String details;
    private Instant timestamp;

    public static ErrorResponse of(String message, String details) {
        return new ErrorResponse(false, message, details, Instant.now());
    }
}