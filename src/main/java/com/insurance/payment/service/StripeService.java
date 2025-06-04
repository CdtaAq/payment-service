package com.insurance.payment.service;

import com.insurance.payment.config.StripeConfig;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Autowired
    private StripeConfig stripeConfig;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecretKey();
    }

    public Session createCheckoutSession(double amount, String currency, String successUrl, String cancelUrl) throws Exception {
        long amountInCents = (long) (amount * 100);

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency)
                            .setUnitAmount(amountInCents)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Auto Insurance Policy")
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        return Session.create(params);
    }
}
