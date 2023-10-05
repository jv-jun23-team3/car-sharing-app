package ua.mate.team3.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import ua.mate.team3.carsharingapp.dto.payment.PaymentDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentRequestDto;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Payment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static PaymentDto paymentDto = new PaymentDto();
    private static PaymentRequestDto requestDto = new PaymentRequestDto();
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        requestDto.setType(Payment.Type.PAYMENT);
        requestDto.setRentalId(1L);
        paymentDto.setId(1L);
        paymentDto.setStatus(Payment.Status.PENDING);
        paymentDto.setType(Payment.Type.PAYMENT);
        paymentDto.setRentalId(1L);
        paymentDto.setSessionUrl("https://example.com/session");
        paymentDto.setSessionId("123456789");
        paymentDto.setAmount(new BigDecimal("30000.0"));
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
    public void createPaymentSession_validDto_returnsPaymentResponseDto() {
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
    public void getAllPayments_validUserId_returnsPaymentResponseDto() {
        MvcResult result = mockMvc.perform(get("/payments/?userId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        ArrayList actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ArrayList.class);
        List<PaymentDto> actualDtos = objectMapper.convertValue(actual,
                new TypeReference<List<PaymentDto>>() {
                });
        PaymentDto actualDto = actualDtos.get(0);
        assertEquals(paymentDto.getId(), actualDto.getId());
        assertEquals(paymentDto.getRentalId(), actualDto.getRentalId());
        assertEquals(paymentDto.getAmount(), actualDto.getAmount());
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
    public void getSuccessPaymentMessage_validSessionId_returnsPaymentMessage() {
        MvcResult result = mockMvc.perform(get("/payments/success?sessionId="
                        + "cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7")
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
    public void getCancelledPaymentMessage_validSessionId_returnsPaymentMessage() {
        MvcResult result = mockMvc.perform(get("/payments/cancel?sessionId="
                        + "cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        String actual = result.getResponse().getContentAsString();
        assertEquals("Payment Paused", actual);
    }
}
