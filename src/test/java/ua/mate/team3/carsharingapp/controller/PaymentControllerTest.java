package ua.mate.team3.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static Payment payment = new Payment();
    private static Rental rental = new Rental();
    private static PaymentRequestDto requestDto = new PaymentRequestDto();
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
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
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl("https://example.com/session");
        payment.setSessionId("123456789");
        payment.setAmount(new BigDecimal("100.00"));
    }

    @SneakyThrows
    @WithMockUser(username = "email@gmail.com")
    @Sql(scripts = {
            "classpath:db/insert-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:db/delete-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createPaymentSession_validDto_returnsPaymentResponseDto(){
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);
        assertTrue(actual.getSessionId().contains("cs_test_"));
        assertTrue(actual.getSessionUrl().contains("https://checkout.stripe.com/c/pay/cs_test_"));
    }

    @SneakyThrows
    @WithMockUser(username = "email@gmail.com")
    @Sql(scripts = {
            "classpath:db/insert-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:db/delete-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void getAllPayments_validUserId_returnsPaymentResponseDto(){
        MvcResult result = mockMvc.perform(get("/payments/?userId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        ArrayList actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ArrayList.class);
        List<Payment> actualDtos = objectMapper.convertValue(actual,
                new TypeReference<List<Payment>>() {
                });
    }

    @SneakyThrows
    @WithMockUser(username = "email@gmail.com")
    @Sql(scripts = {
            "classpath:db/insert-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:db/delete-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void getSuccessPaymentMessage_validSessionId_returnsPaymentMessage(){
        MvcResult result = mockMvc.perform(get("/payments/success?sessionId=cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        String actual = result.getResponse().getContentAsString();
        assertEquals("Payment was successful", actual);
    }

    @SneakyThrows
    @WithMockUser(username = "email@gmail.com")
    @Sql(scripts = {
            "classpath:db/insert-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:db/delete-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void getCancelledPaymentMessage_validSessionId_returnsPaymentMessage(){
        MvcResult result = mockMvc.perform(get("/payments/cancel?sessionId=cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        String actual = result.getResponse().getContentAsString();
        assertEquals("Payment Paused", actual);
    }


}
