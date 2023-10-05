package ua.mate.team3.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;
import ua.mate.team3.carsharingapp.mapper.CarMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    private static Car testCar;
    private static CarDto testCarDto;
    private static CreateCarRequestDto createCarRequestDto;
    private static final Long VALID_ID = 1L;

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    public void setUp() {
        testCar = new Car();
        testCar.setId(VALID_ID);
        testCar.setModel("testModel");
        testCar.setBrand("testBrand");
        testCar.setType(Car.TypeName.SEDAN);
        testCar.setInventory(VALID_ID.intValue());
        testCar.setDailyFee(BigDecimal.ONE);

        testCarDto = new CarDto();
        testCarDto.setId(testCar.getId());
        testCarDto.setModel(testCar.getModel());
        testCarDto.setBrand(testCar.getBrand());
        testCarDto.setType(testCar.getType());
        testCarDto.setInventory(testCar.getInventory());
        testCarDto.setDailyFee(testCar.getDailyFee());

        createCarRequestDto = new CreateCarRequestDto();
        createCarRequestDto.setModel(testCar.getModel());
        createCarRequestDto.setBrand(testCar.getBrand());
        createCarRequestDto.setType(testCar.getType());
        createCarRequestDto.setInventory(testCar.getInventory());
        createCarRequestDto.setDailyFee(testCar.getDailyFee());
    }

    @Test
    @DisplayName("Save car - Valid car")
    public void save_ValidCar_ReturnSavedCarDto() {
        when(carMapper.toEntity(createCarRequestDto)).thenReturn(testCar);
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(carMapper.toDto(testCar)).thenReturn(testCarDto);
        CarDto actual = carService.save(createCarRequestDto);
        assertNotNull(actual);
        assertEquals(testCarDto, actual);
    }

    @Test
    @DisplayName("Find all cars")
    public void findAll_ReturnAllCars() {
        Pageable pageable = PageRequest.of(VALID_ID.intValue(), 2);
        Page<Car> carPage =
                new PageImpl<>(List.of(testCar), pageable, VALID_ID.intValue());
        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(testCar)).thenReturn(testCarDto);
        List<CarDto> actual = carService.findAll(pageable);
        assertEquals(List.of(testCarDto), actual);
    }

    @Test
    @DisplayName("Get car by id - Car exists")
    public void getById_CarExists_ReturnCarDto() {
        when(carRepository.findById(VALID_ID)).thenReturn(Optional.of(testCar));
        when(carMapper.toDto(testCar)).thenReturn(testCarDto);
        CarDto actual = carService.getById(VALID_ID);
        assertNotNull(actual);
        assertEquals(testCarDto, actual);
    }

    @Test
    @DisplayName("Get car by id - Car not found")
    public void getById_CarNotFound_ThrowEntityNotFoundException() {
        Long nonExistentId = 10L;
        when(carRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> carService.getById(nonExistentId));
    }

    @Test
    @DisplayName("Delete car by id")
    public void deleteById_CarExists_DeleteSuccessfully() {
        doNothing().when(carRepository).deleteById(VALID_ID);
        carService.deleteById(VALID_ID);
        verify(carRepository, times(VALID_ID.intValue())).deleteById(VALID_ID);
    }

    @Test
    @DisplayName("Update car - Car exists")
    public void update_CarExists_ReturnUpdatedCarDto() {
        when(carMapper.toEntity(createCarRequestDto)).thenReturn(testCar);
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(carMapper.toDto(testCar)).thenReturn(testCarDto);

        CarDto actual = carService.update(VALID_ID, createCarRequestDto);
        assertNotNull(actual);
        assertEquals(testCarDto, actual);
    }
}
