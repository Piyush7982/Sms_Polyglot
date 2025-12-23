package org.meesho.sms_sender.Services;
import org.meesho.sms_sender.Classes.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class SmsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String kafkaTopic;

    public void processSms(SmsRequest request) {
        // 1. Validate Phone Number
        if (request.getPhoneNumber() == null || !request.getPhoneNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }
        // 2. Check Blacklist in Redis 
        // We check if the phone number exists as a key in Redis
        String blockStatus = redisTemplate.opsForValue().get(request.getPhoneNumber());
        
        if (blockStatus != null) {
            System.out.println("BLOCKED: User " + request.getPhoneNumber() + " is in the blocklist.");
            throw new RuntimeException("User is Blocked");
        }

        // 3. Mock 3rd Party Call 
        System.out.println("Sending SMS to Third Party: " + request.getPhoneNumber() + ": " + request.getMessage());
       

        // 4. Send Event to Kafka 
        // This pushes the data to the 'sms_send_events' topic
        System.out.println("Sending to Kafka: " + request);
        kafkaTemplate.send(kafkaTopic, request);
    }

    public void addToBlacklist(String phoneNumber, String reason) {
        redisTemplate.opsForValue().set(phoneNumber, reason == null ? "blocked" : reason);
    }
}