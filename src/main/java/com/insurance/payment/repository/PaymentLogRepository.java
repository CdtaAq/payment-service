package com.insurance.payment.repository;

import com.insurance.payment.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    PaymentLog findBySessionId(String sessionId);
}
