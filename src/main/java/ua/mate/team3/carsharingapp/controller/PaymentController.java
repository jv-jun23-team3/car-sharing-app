package ua.mate.team3.carsharingapp.controller;

import com.stripe.exception.StripeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService stripePaymentService;

    @PostMapping
    public PaymentResponseDto createPaymentSession(@RequestBody PaymentRequestDto requestDto)
            throws StripeException {
        return stripePaymentService.createPaymentSession(requestDto);
    }

    @GetMapping("/success")
    public String getSuccessPaymentMessage(@RequestParam String sessionId) {
        return stripePaymentService.getSuccessfulPaymentMessage(sessionId);
    }

    @GetMapping("/cancel")
    public String getCancelledPaymentMessage(@RequestParam String sessionId) {
        return stripePaymentService.getCanceledPaymentMessage(sessionId);
    }

    @GetMapping("/")
    public List<Payment> getAllPayments(@RequestParam Long userId)
            throws StripeException {
        return stripePaymentService.getAllPayments(userId);
    }
}
