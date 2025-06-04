package com.insurance.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.payment.entity.PaymentLog;
import com.insurance.payment.repository.PaymentLogRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class WebhookController {

    private final PaymentLogRepository paymentLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookController(PaymentLogRepository paymentLogRepository) {
        this.paymentLogRepository = paymentLogRepository;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                JsonNode jsonNode = objectMapper.readTree(payload).get("data").get("object");
                String sessionId = jsonNode.get("id").asText();
                String customerEmail = jsonNode.get("customer_email").asText();
                double amount = jsonNode.get("amount_total").asDouble() / 100;

                PaymentLog log = PaymentLog.builder()
                        .sessionId(sessionId)
                        .customerEmail(customerEmail)
                        .amount(amount)
                        .status("COMPLETED")
                        .timestamp(LocalDateTime.now())
                        .build();

                paymentLogRepository.save(log);
            }

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Webhook error");
        }
    }
}
