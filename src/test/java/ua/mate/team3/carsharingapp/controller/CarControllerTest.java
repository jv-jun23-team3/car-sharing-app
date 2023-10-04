package ua.mate.team3.carsharingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;
import ua.mate.team3.carsharingapp.model.Car;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "classpath:database/create/add-car-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-cars-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    private static CreateCarRequestDto createCarRequestDto1;
    private static CarDto carDto1;
    private static CreateCarRequestDto createCarRequestDto2;
    private static CarDto carDto2;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        createCarRequestDto1 = new CreateCarRequestDto();
        createCarRequestDto1.setModel("Model S");
        createCarRequestDto1.setBrand("Tesla");
        createCarRequestDto1.setType(Car.TypeName.SEDAN);
        createCarRequestDto1.setInventory(10);
        createCarRequestDto1.setDailyFee(BigDecimal.valueOf(100));

        carDto1 = new CarDto();
        carDto1.setId(1L);
        carDto1.setModel(createCarRequestDto1.getModel());
        carDto1.setBrand(createCarRequestDto1.getBrand());
        carDto1.setType(createCarRequestDto1.getType());
        carDto1.setInventory(createCarRequestDto1.getInventory());
        carDto1.setDailyFee(createCarRequestDto1.getDailyFee());

        createCarRequestDto2 = new CreateCarRequestDto();
        createCarRequestDto2.setModel("Model X");
        createCarRequestDto2.setBrand("Tesla");
        createCarRequestDto2.setType(Car.TypeName.SUV);
        createCarRequestDto2.setInventory(5);
        createCarRequestDto2.setDailyFee(BigDecimal.valueOf(150));

        carDto2 = new CarDto();
        carDto2.setId(2L);
        carDto2.setModel(createCarRequestDto2.getModel());
        carDto2.setBrand(createCarRequestDto2.getBrand());
        carDto2.setType(createCarRequestDto2.getType());
        carDto2.setInventory(createCarRequestDto2.getInventory());
        carDto2.setDailyFee(createCarRequestDto2.getDailyFee());
    }

    @Test
    @WithMockUser(username = "MANAGER", roles = {"ADMIN"})
    @DisplayName("Create a new car")
    void createCar_validRequestDto_returnCarDto() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(createCarRequestDto1);
        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(carDto1, actual, "id"));
    }
}
