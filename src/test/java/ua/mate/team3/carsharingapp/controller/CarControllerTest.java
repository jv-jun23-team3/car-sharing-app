package ua.mate.team3.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

@Sql(scripts = "classpath:database/create/add-car-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    private static CreateCarRequestDto createCarRequestDto1;
    private static CarDto carDto1;
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
    }

    @BeforeEach
    void setUpBeforeEach() {

        createCarRequestDto1 = new CreateCarRequestDto();
        createCarRequestDto1.setModel("Model S");
        createCarRequestDto1.setBrand("Tesla");
        createCarRequestDto1.setType(Car.TypeName.SEDAN);
        createCarRequestDto1.setInventory(10);
        createCarRequestDto1.setDailyFee(new BigDecimal("100.00"));

        carDto1 = new CarDto();
        carDto1.setId(1L);
        carDto1.setModel(createCarRequestDto1.getModel());
        carDto1.setBrand(createCarRequestDto1.getBrand());
        carDto1.setType(createCarRequestDto1.getType());
        carDto1.setInventory(createCarRequestDto1.getInventory());
        carDto1.setDailyFee(createCarRequestDto1.getDailyFee());

        carDto2 = new CarDto();
        carDto2.setId(2L);
        carDto2.setModel("Model X");
        carDto2.setBrand("Tesla");
        carDto2.setType(Car.TypeName.SUV);
        carDto2.setInventory(5);
        carDto2.setDailyFee(new BigDecimal("150.00"));
    }

    @Test
    @WithMockUser(username = "MANAGER", roles = {"MANAGER"})
    @DisplayName("Create a new car")
    void createCar_validRequestDto_returnCarDto() throws Exception {
        createCarRequestDto1.setModel("Model Y");
        carDto1.setModel("Model Y");
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

    @Test
    @DisplayName("Test getAll endpoint for car")
    @WithMockUser(username = "CUSTOMER", roles = {"CUSTOMER"})
    void getAll_validCars_returnResponse() throws Exception {
        List<CarDto> expected = List.of(carDto1, carDto2);
        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        List<CarDto> actual = List.of(objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto[].class));
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test get endpoint for cars")
    @WithMockUser(username = "CUSTOMER", roles = {"CUSTOMER"})
    void getById_validCar_returnResponse() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/cars/1")
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

    @Test
    @WithMockUser(username = "MANAGER", roles = {"MANAGER"})
    @DisplayName("Delete car by valid id")
    void delete_validId_successful() throws Exception {
        mockMvc.perform(delete("/cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "MANAGER", roles = {"MANAGER"})
    @DisplayName("Update car by id")
    void update_validRequest_returnsExpectedCar() throws Exception {
        createCarRequestDto1.setModel("Model Y");
        carDto1.setModel("Model Y");
        MvcResult mvcResult = mockMvc.perform(put("/cars/1")
                        .content(objectMapper.writeValueAsString(createCarRequestDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), CarDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(carDto1, actual, "id"));
    }
}
