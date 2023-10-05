package ua.mate.team3.carsharingapp.service;

import com.stripe.exception.StripeException;
import java.util.List;
import org.springframework.security.core.Authentication;
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto) throws StripeException;

    String getSuccessfulPaymentMessage(String sessionId);

    String getCanceledPaymentMessage(String sessionId);

    List<PaymentDto> getAllPayments(Long id, Authentication authentication);
}
