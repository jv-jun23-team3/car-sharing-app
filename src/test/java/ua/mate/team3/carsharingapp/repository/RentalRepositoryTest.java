package ua.mate.team3.carsharingapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/create/add-car-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/create/add-rental-and-all-necessary-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Find all rentals")
    public void findAllWithParams_returnAllRentals() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.RoleName.ROLE_MANAGER);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
        testUser.setFirstName("admin");
        testUser.setLastName("admin");
        testUser.setRoles(Set.of(role));
        testUser.setPassword("$2a$10$9RuGtrR4xjbMm8hu29hgXO6Tcmgh8qaV9hlxz7p5gDsf29vpAo.oO");

        Car testCar = new Car();
        testCar.setId(1L);
        testCar.setModel("Model S");
        testCar.setBrand("Tesla");
        testCar.setType(Car.TypeName.SEDAN);
        testCar.setInventory(10);
        testCar.setDailyFee(new BigDecimal("100.00"));

        LocalDateTime fixedDateTime = LocalDateTime.of(2023, Month.OCTOBER, 10, 10, 10);
        Rental testRental1 = new Rental();
        testRental1.setId(1L);
        testRental1.setRentalDate(fixedDateTime);
        testRental1.setReturnDate(fixedDateTime.plusDays(7));
        testRental1.setUser(testUser);
        testRental1.setCar(testCar);

        Rental testRental2 = new Rental();
        testRental2.setId(2L);
        testRental2.setRentalDate(fixedDateTime);
        testRental2.setReturnDate(fixedDateTime.plusDays(7));
        testRental2.setActualReturnDate(fixedDateTime.plusDays(7));
        testRental2.setUser(testUser);
        testRental2.setCar(testCar);

        Pageable pageable = PageRequest.of(0, 5);
        List<Rental> rentals = rentalRepository.findByUserIdAndIsActive(1L, true, pageable);
        assertEquals(List.of(testRental1), rentals);
        rentals = rentalRepository.findByUserIdAndIsActive(1L, false, pageable);
        assertEquals(List.of(testRental2), rentals);
    }
}
