package ua.mate.team3.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.mapper.PaymentMapper;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.repository.PaymentRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.PaymentService;

@RequiredArgsConstructor
@Service
public class StripePaymentService implements PaymentService {
    public static final String PAYMENT_PAUSED = "Payment Paused";
    public static final String CURRENCY = "usd";
    public static final String SUCCESS_URL = "http://localhost:8080/payments/success?sessionId=";
    public static final String CANCEL_URL = "http://localhost:8080/payments/cancel";

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        Rental rental = rentalRepository.findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("can't find rental with id: "
                        + requestDto.getRentalId()));
        SessionCreateParams sessionCreateParams = buildSessionParams(rental);
        Session session = Session.create(sessionCreateParams);
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmount(BigDecimal.valueOf(session.getAmountTotal()));

        paymentRepository.save(payment);
        return paymentMapper.toDtoFromSession(session);
    }

    public List<PaymentResponseDto> getSuccessfulPayments(String sessionId)
            throws StripeException {
        Session session = Session.retrieve(sessionId);
        Payment payment = paymentRepository.getBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);
        return paymentRepository.findAllByStatus(Payment.Status.PAID)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<Payment> getAllPayments(Long id) {
        return paymentRepository.findAllByUserId(id);
    }

    @Override
    public String getCanceledPayment(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        session.expire();
        return PAYMENT_PAUSED;
    }

    private SessionCreateParams buildSessionParams(Rental rental) throws StripeException {
        ProductCreateParams productParams = new ProductCreateParams.Builder()
                .setName(rental.getCar().getModel())
                .build();
        Product product = Product.create(productParams);
        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
                .setUnitAmountDecimal(rental.getCar().getDailyFee()
                        .multiply(BigDecimal.valueOf(100)))
                .setCurrency(CURRENCY)
                .setProduct(product.getId())
                .build();
        Price price = Price.create(priceCreateParams);
        SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(1L)
                        .build())
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .build();
        return sessionCreateParams;
    }

}
