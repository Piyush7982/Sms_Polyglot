package org.meesho.sms_sender.Classes;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponse {
    private boolean success;
    private String message;
    private Object data;
    private Instant timestamp;

    public static SuccessResponse of(String message, Object data) {
        return new SuccessResponse(true, message, data, Instant.now());
    }
}