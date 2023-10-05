package ua.mate.team3.carsharingapp.service.strategy;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mate.team3.carsharingapp.model.Payment;

@RequiredArgsConstructor
@Component
public class PaymentHandlerStrategy {
    private final List<PaymentHandler> paymentHandlers;

    public PaymentHandler getHandler(Payment.Type type) {
        return paymentHandlers.stream()
                .filter(ph -> ph.isApplicable(type))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Unsupported payment type: " + type));
    }
}
