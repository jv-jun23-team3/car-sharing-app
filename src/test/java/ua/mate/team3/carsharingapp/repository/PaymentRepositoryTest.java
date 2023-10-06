package ua.mate.team3.carsharingapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ua.mate.team3.carsharingapp.model.Payment;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Sql(scripts = {
            "classpath:db/insert-payment.sql"
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:db/delete-payment.sql"
            }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findAllByUserId_validUserId_returnsPayment() {
        List<Payment> payments = paymentRepository.findAllByUserId(2L);
        assertEquals(1, payments.size());
        Payment payment = payments.get(0);
        assertEquals(1L, payment.getId());
        assertEquals(2L, payment.getRental().getUser().getId());
        assertEquals(1L, payment.getRental().getId());
        assertNotNull(payment.getSessionId());
        assertNotNull(payment.getSessionUrl());
    }
}
