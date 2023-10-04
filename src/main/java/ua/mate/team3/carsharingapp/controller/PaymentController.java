package ua.mate.team3.carsharingapp.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.service.PaymentService;

@Tag(name = "Payment controller", description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService stripePaymentService;

    @Operation(summary = "Create new payment session", description = "Create new payment session")
    @PostMapping
    public PaymentResponseDto createPaymentSession(@RequestBody PaymentRequestDto requestDto)
            throws StripeException {
        return stripePaymentService.createPaymentSession(requestDto);
    }

    @Operation(summary = "success endpoint for redirection from Stripe",
            description = "success endpoint for redirection from Stripe after successful payment")
    @GetMapping("/success")
    public String getSuccessPaymentMessage(@RequestParam String sessionId) {
        return stripePaymentService.getSuccessfulPaymentMessage(sessionId);
    }

    @Operation(summary = "cancel endpoint for redirection from Stripe",
            description = "cancel endpoint for redirection from Stripe after canceled payment")
    @GetMapping("/cancel")
    public String getCancelledPaymentMessage(@RequestParam String sessionId) {
        return stripePaymentService.getCanceledPaymentMessage(sessionId);
    }

    @Operation(summary = "get all payments by user id", description = "Create new payment session")
    @GetMapping("/")
    public List<PaymentDto> getAllPayments(@RequestParam Long userId) {
        return stripePaymentService.getAllPayments(userId);
    }
}
