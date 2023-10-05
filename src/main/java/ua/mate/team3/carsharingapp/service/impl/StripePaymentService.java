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
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.mapper.PaymentMapper;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.repository.PaymentRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.PaymentService;
import ua.mate.team3.carsharingapp.service.strategy.PaymentHandlerStrategy;

@RequiredArgsConstructor
@Service
public class StripePaymentService implements PaymentService {
    public static final String PAYMENT_PAUSED = "Payment Paused";
    public static final String CURRENCY = "usd";
    public static final String SUCCESS_URL = "http://localhost:8080/payments/success";
    public static final String CANCEL_URL = "http://localhost:8080/payments/cancel";
    public static final String SESSION_ID_PARAM = "?sessionId={CHECKOUT_SESSION_ID}";
    public static final String SUCCESSFUL_PAYMENT = "Payment was successful";

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentHandlerStrategy paymentHandlerStrategy;
    private final NotificationService notificationService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        Rental rental = rentalRepository.findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("can't find rental with id: "
                        + requestDto.getRentalId()));
        Session session = Session.create(buildSessionParams(rental, requestDto));
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(requestDto.getType());
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        BigDecimal amount = paymentHandlerStrategy.getHandler(requestDto.getType()).handlePayment(
                rental.getRentalDate(), rental.getReturnDate(), rental.getCar().getDailyFee());
        payment.setAmount(amount);
        paymentRepository.save(payment);
        notificationService.sendNotification(
                "The rental is returned and payment: " + payment + " is pending");
        return paymentMapper.toDtoFromSession(session);
    }

    public String getSuccessfulPaymentMessage(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);
        notificationService.sendNotification(
                "The payment: " + payment + " is paid successfully");
        return SUCCESSFUL_PAYMENT;
    }

    public List<PaymentDto> getAllPayments(Long id) {
        return paymentRepository.findAllByUserId(id).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public String getCanceledPaymentMessage(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId);
        payment.setStatus(Payment.Status.CANCELLED);
        paymentRepository.save(payment);
        notificationService.sendNotification(
                "The payment: " + payment + " is paused");
        return PAYMENT_PAUSED;
    }

    private SessionCreateParams buildSessionParams(Rental rental, PaymentRequestDto requestDto)
            throws StripeException {
        ProductCreateParams productParams = new ProductCreateParams.Builder()
                .setName(rental.getCar().getModel())
                .build();
        Product product = Product.create(productParams);
        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
                .setUnitAmountDecimal(calculateTotalPrice(rental.getRentalDate(),
                        rental.getActualReturnDate(),
                        rental.getCar().getDailyFee(), requestDto.getType()))
                .setCurrency(CURRENCY)
                .setProduct(product.getId())
                .build();
        Price price = Price.create(priceCreateParams);
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(1L)
                        .build())
                .setSuccessUrl(SUCCESS_URL + SESSION_ID_PARAM)
                .setCancelUrl(CANCEL_URL + SESSION_ID_PARAM)
                .build();
    }

    private BigDecimal calculateTotalPrice(LocalDateTime from, LocalDateTime to,
                                           BigDecimal pricePerDay, Payment.Type type) {
        return paymentHandlerStrategy.getHandler(type).handlePayment(from, to, pricePerDay);
    }

}
