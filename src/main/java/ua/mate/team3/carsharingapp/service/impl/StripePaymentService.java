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
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.mapper.PaymentMapper;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.PaymentRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.PaymentService;
import ua.mate.team3.carsharingapp.service.strategy.PaymentHandlerStrategy;

@RequiredArgsConstructor
@Service
public class StripePaymentService implements PaymentService {
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String PAYMENT_PAUSED = "Payment Paused";
    private static final String CURRENCY = "usd";
    private static final String SUCCESS_URL = "http://localhost:8088/payments/success";
    private static final String CANCEL_URL = "http://localhost:8088/payments/cancel";
    private static final String SESSION_ID_PARAM = "?sessionId={CHECKOUT_SESSION_ID}";
    private static final String SUCCESSFUL_PAYMENT = "Payment was successful";
    private static final Long FROM_CENTS_TO_DOLLARS = 100L;
    private static final String PAYMENT_INFO_TEMPLATE = "The payment🤑:"
            + "\n📋 **Payment ID:** %d"
            + "\n📊 **Payment status:** %s"
            + "\n🏎️ **Payment's rental ID:** %d"
            + "\n💰 **Amount:** %s";

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
        Payment payment = getPayment(requestDto, rental, session);
        BigDecimal amount = paymentHandlerStrategy.getHandler(requestDto.getType()).handlePayment(
                rental.getRentalDate(), rental.getReturnDate(), rental.getCar().getDailyFee());
        payment.setAmount(amount.divide(BigDecimal.valueOf(FROM_CENTS_TO_DOLLARS)));
        Payment savedPayment = paymentRepository.save(payment);
        notificationService.sendNotification(
                formMessage(savedPayment) + " is pending😉");
        return paymentMapper.toDtoFromSession(session);
    }

    public String getSuccessfulPaymentMessage(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        Payment savedPayment = paymentRepository.save(payment);
        notificationService.sendNotification(
                formMessage(savedPayment) + " is paid successfully✅");
        return SUCCESSFUL_PAYMENT;
    }

    public List<PaymentDto> getAllPayments(Long id, Authentication authentication) {
        if (id == null) {
            GrantedAuthority grantedAuthority = getGrantedAuthority(authentication);
            if (grantedAuthority.getAuthority().equals(ROLE_MANAGER)) {
                return paymentRepository.findAll().stream()
                        .map(paymentMapper::toDto)
                        .toList();
            }
            throw new AccessDeniedException("Only Manager can get all payments");
        } else {
            checkValidIdIfNotManager(id, authentication);
            return paymentRepository.findAllByUserId(id).stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }
    }

    @Override
    public String getCanceledPaymentMessage(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId);
        payment.setStatus(Payment.Status.CANCELLED);
        Payment savedPayment = paymentRepository.save(payment);
        notificationService.sendNotification(
                formMessage(savedPayment) + " is paused🛑");
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

    private Payment getPayment(PaymentRequestDto requestDto, Rental rental, Session session) {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(requestDto.getType());
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        return payment;
    }

    private GrantedAuthority getGrantedAuthority(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        GrantedAuthority grantedAuthority = authorities.stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("Can't find role"));
        return grantedAuthority;
    }

    private void checkValidIdIfNotManager(Long userId, Authentication authentication) {
        GrantedAuthority grantedAuthority = getGrantedAuthority(authentication);
        if (grantedAuthority.getAuthority().equals(ROLE_MANAGER)) {
            return;
        }
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getId(), userId)) {
            throw new AccessDeniedException("Only Manager can get other user information");
        }
    }

    private String formMessage(Payment payment) {
        return String.format(PAYMENT_INFO_TEMPLATE,
                payment.getId(),
                payment.getStatus().name(),
                payment.getRental().getId(),
                payment.getAmount());
    }

    private BigDecimal calculateTotalPrice(LocalDateTime from, LocalDateTime to,
                                           BigDecimal pricePerDay, Payment.Type type) {
        return paymentHandlerStrategy.getHandler(type).handlePayment(from, to, pricePerDay);
    }
}
