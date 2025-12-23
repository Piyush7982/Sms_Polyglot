package org.meesho.sms_sender.Controllers;

import org.meesho.sms_sender.Classes.ErrorResponse;
import org.meesho.sms_sender.Classes.SmsRequest;
import org.meesho.sms_sender.Classes.SuccessResponse;
import org.meesho.sms_sender.Services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    // Endpoint: POST /v1/sms/send
    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@RequestBody SmsRequest request) {
        try {
            smsService.processSms(request);
            return ResponseEntity.ok(SuccessResponse.of("SMS Request Processed", request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403)
                    .body(ErrorResponse.of("Request blocked", e.getMessage()));
        }
    }

    // Endpoint: POST /v1/sms/blacklist
    @PostMapping("/blacklist")
    public ResponseEntity<?> addToBlacklist(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String reason) {

        try {
            smsService.addToBlacklist(phoneNumber, reason);
            return ResponseEntity.ok(SuccessResponse.of("Number added to blacklist", phoneNumber));
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(403)
                    .body(ErrorResponse.of("Request blocked", e.getMessage()));
        }

    }
}