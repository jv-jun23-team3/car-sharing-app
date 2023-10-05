package ua.mate.team3.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.Month;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.model.Rental;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static Rental testRental1;
    private static ResponseRentalDto testRentalDto;
    private static CreateRentalRequestDto createRentalRequestDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() {

        LocalDateTime fixedDateTime = LocalDateTime.of(2023, Month.OCTOBER,
                10, 10, 10);
        testRental1 = new Rental();
        testRental1.setId(1L);
        testRental1.setRentalDate(fixedDateTime);
        testRental1.setReturnDate(fixedDateTime.plusDays(7));

        createRentalRequestDto = new CreateRentalRequestDto();
        createRentalRequestDto.setCarId(1L);
        createRentalRequestDto.setReturnDate(testRental1.getReturnDate());

        testRentalDto = new ResponseRentalDto();
        testRentalDto.setId(1L);
        testRentalDto.setRentalDate(testRental1.getRentalDate());
        testRentalDto.setReturnDate(testRental1.getReturnDate());
        testRentalDto.setUserId(1L);
        testRentalDto.setCarId(1L);
    }

    @SneakyThrows
    @WithMockUser(username = "MANAGER", roles = "MANAGER")
    @Sql(scripts = "classpath:database/create/add-car-data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/create/add-rental-and-all-necessary-data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete/delete-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void update_validRequest_returnRentalDto() {
        MvcResult result = mockMvc.perform(
                        put("/rentals/1/return")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ResponseRentalDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ResponseRentalDto.class);
        boolean equals = EqualsBuilder.reflectionEquals(testRentalDto, actual, "actualReturnDate");
        Assertions.assertTrue(equals);
    }
}
