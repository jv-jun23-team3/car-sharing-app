package ua.mate.team3.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.mapper.RentalMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.security.AuthenticationService;
import ua.mate.team3.carsharingapp.service.impl.RentalServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static Rental testRental1;
    private static ResponseRentalDto testRentalDto;
    private static CreateRentalRequestDto createRentalRequestDto;
    private static Car testCar;
    private static User testUser;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.RoleName.ROLE_MANAGER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
        testUser.setFirstName("admin");
        testUser.setLastName("admin");
        testUser.setRoles(Set.of(role));
        testUser.setPassword("$2a$10$9RuGtrR4xjbMm8hu29hgXO6Tcmgh8qaV9hlxz7p5gDsf29vpAo.oO");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setModel("Model S");
        testCar.setBrand("Tesla");
        testCar.setType(Car.TypeName.SEDAN);
        testCar.setInventory(10);
        testCar.setDailyFee(new BigDecimal("100.00"));

        LocalDateTime fixedDateTime = LocalDateTime.of(2023, Month.OCTOBER, 10, 10, 10);
        testRental1 = new Rental();
        testRental1.setId(1L);
        testRental1.setRentalDate(fixedDateTime);
        testRental1.setReturnDate(fixedDateTime.plusDays(7));
        testRental1.setUser(testUser);
        testRental1.setCar(testCar);

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

    @Test
    @DisplayName("Save rental - Valid rental")
    public void save_ValidRental_ReturnSavedRentalDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.ofNullable(testCar));
        when(authenticationService.getUser()).thenReturn(testUser);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental1);
        when(rentalMapper.toResponseDto(testRental1)).thenReturn(testRentalDto);
        ResponseRentalDto actual = rentalService.save(createRentalRequestDto);
        assertNotNull(actual);
        assertEquals(testRentalDto, actual);
    }

    @Test
    @DisplayName("Get rental by id - Rental exists")
    public void getById_RentalExists_ReturnRentalDto() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental1));
        when(rentalMapper.toResponseDto(testRental1)).thenReturn(testRentalDto);
        when(authenticationService.getUserId()).thenReturn(1L);
        ResponseRentalDto actual = rentalService.getById(1L);
        assertNotNull(actual);
        assertEquals(testRentalDto, actual);
    }

    @Test
    @DisplayName("Get rental by id - Rental doesn't exist")
    public void getById_RentalIdIsInvalid_ThrowEntityNotFoundException() {
        Long nonExistentId = 10L;
        when(rentalRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> rentalService.getById(nonExistentId));
    }

    @Test
    @DisplayName("Update rental - Rental exists")
    public void update_RentalExists_ReturnUpdatedRentalDto() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental1));
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental1);
        when(rentalMapper.toResponseDto(testRental1)).thenReturn(testRentalDto);

        ResponseRentalDto actual = rentalService.update(1L);
        testRental1.setActualReturnDate(testRental1.getReturnDate());
        assertNotNull(actual);
        assertEquals(testRentalDto, actual);
    }

    @Test
    @DisplayName("Find all rentals")
    public void findAll_ReturnAllRentals() {
        Pageable pageable = PageRequest.of(1, 2);
        when(rentalRepository.findByUserIdAndIsActive(1L, true, pageable))
                .thenReturn(List.of(testRental1));
        when(rentalMapper.toResponseDto(testRental1)).thenReturn(testRentalDto);
        List<ResponseRentalDto> actual = rentalService
                .getAllRentalsByUserIdAndState(1L, true, pageable);
        assertEquals(List.of(testRentalDto), actual);
    }
}
