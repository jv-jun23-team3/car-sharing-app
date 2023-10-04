package ua.mate.team3.carsharingapp.service.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import ua.mate.team3.carsharingapp.model.Payment;

public interface PaymentHandler {
    BigDecimal handlePayment(LocalDateTime from, LocalDateTime to, BigDecimal pricePerDay);

    boolean isApplicable(Payment.Type type);
}
