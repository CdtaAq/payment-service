package com.insurance.payment.controller;

import com.insurance.payment.service.StripeService;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-checkout-session")
    public Map<String, Object> createCheckoutSession(@RequestParam double amount) throws Exception {
        Session session = stripeService.createCheckoutSession(
                amount,
                "usd",
                "http://localhost:3000/success",
                "http://localhost:3000/cancel"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("url", session.getUrl());

        return response;
    }
}
