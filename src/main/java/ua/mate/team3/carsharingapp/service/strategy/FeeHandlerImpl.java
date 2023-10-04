package ua.mate.team3.carsharingapp.service.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.model.Payment;

@Service
public class FeeHandlerImpl implements PaymentHandler {
    private static final BigDecimal DEFAULT_FEE = BigDecimal.TEN;
    private static final BigDecimal MULTIPLICAND_FROM_DOLLAR_TO_CENTS = BigDecimal.valueOf(100);

    @Override
    public BigDecimal handlePayment(LocalDateTime from, LocalDateTime to, BigDecimal pricePerDay) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(daysBetween))
                .multiply(DEFAULT_FEE).multiply(MULTIPLICAND_FROM_DOLLAR_TO_CENTS);;
        return totalPrice;
    }

    @Override
    public boolean isApplicable(Payment.Type type) {
        return type == Payment.Type.FINE;
    }
}
