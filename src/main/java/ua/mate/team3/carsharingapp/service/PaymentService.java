package ua.mate.team3.carsharingapp.service;

import com.stripe.exception.StripeException;
import java.util.List;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Payment;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto) throws StripeException;

    String getSuccessfulPaymentMessage(String sessionId);

    String getCanceledPaymentMessage(String sessionId);

    List<Payment> getAllPayments(Long id);
}
