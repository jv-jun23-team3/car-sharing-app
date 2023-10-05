package ua.mate.team3.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.mapper.PaymentMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.PaymentRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.impl.StripePaymentService;
import ua.mate.team3.carsharingapp.service.strategy.PaymentHandlerStrategy;

@ExtendWith(MockitoExtension.class)
public class StripePaymentServiceTest {
    private static Payment payment = new Payment();
    private static Rental rental = new Rental();
    private static PaymentDto paymentDto = new PaymentDto();
    private static PaymentRequestDto requestDto = new PaymentRequestDto();
    @InjectMocks
    private StripePaymentService paymentService;
    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentHandlerStrategy paymentHandlerStrategy;
    @Mock
    private NotificationService notificationService;

    @BeforeAll
    static void beforeAll() {
        User user = new User();
        user.setId(1L);
        Car car = new Car();
        car.setDailyFee(BigDecimal.TEN);
        rental.setId(1L);
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDate(LocalDateTime.now().plusDays(7));
        rental.setActualReturnDate(null);
        rental.setCar(car);
        rental.setUser(user);
        rental.setDeleted(false);
        requestDto.setType(Payment.Type.PAYMENT);
        requestDto.setRentalId(rental.getId());

        payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl("https://example.com/session");
        payment.setSessionId("123456789");
        payment.setAmount(new BigDecimal("100.00"));
        paymentDto.setStatus(Payment.Status.PENDING);
        paymentDto.setType(payment.getType());
        paymentDto.setRentalId(payment.getRental().getId());
        paymentDto.setSessionUrl(payment.getSessionUrl());
        paymentDto.setSessionId(payment.getSessionId());
        paymentDto.setAmount(payment.getAmount());

    }

    @Test
    public void createPaymentSession_validDto_throwsException() {
        when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> paymentService.createPaymentSession(requestDto));
        assertEquals("can't find rental with id: 1", exception.getMessage());
        verifyNoMoreInteractions(paymentMapper);
        verifyNoMoreInteractions(paymentHandlerStrategy);
    }

    @Test
    public void getSuccessfulPaymentMessage_validRequest_returnsMessage() {
        when(paymentRepository.getBySessionId(anyString())).thenReturn(payment);
        assertEquals("Payment was successful",
                paymentService.getSuccessfulPaymentMessage(payment.getSessionId()));
        verify(paymentRepository).getBySessionId(anyString());
        verify(paymentRepository).save(any(Payment.class));
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    public void getCanceledPaymentMessage_validRequest_returnsMessage() {
        when(paymentRepository.getBySessionId(anyString())).thenReturn(payment);
        assertEquals("Payment Paused",
                paymentService.getCanceledPaymentMessage(payment.getSessionId()));
        verify(paymentRepository).getBySessionId(anyString());
        verify(paymentRepository).save(any(Payment.class));
        verify(notificationService).sendNotification(anyString());

    }
}
