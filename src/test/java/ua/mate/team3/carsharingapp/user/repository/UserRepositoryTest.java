package ua.mate.team3.carsharingapp.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ua.mate.team3.carsharingapp.config.SqlFilePaths.USER_ROLE_DELETE;
import static ua.mate.team3.carsharingapp.config.SqlFilePaths.USER_ROLE_INSERT;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private static final Long SPARE_ID = 3L;
    private static final Long CUSTOMER_ID = 1L;
    private static User expectedUser;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        expectedUser = new User();
        expectedUser.setId(SPARE_ID);
        expectedUser.setFirstName("UserName 1");
        expectedUser.setLastName("LastName 1");
        expectedUser.setPassword("Password 1");
        expectedUser.setEmail("email@gmail.com");
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_CUSTOMER);
        role.setId(CUSTOMER_ID);
        expectedUser.setRoles(Collections.singleton(role));
    }

    @Sql(scripts = USER_ROLE_INSERT, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = USER_ROLE_DELETE, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void findByEmail_validEmail_ReturnsUser() {
        User user = userRepository.findByEmail("email@gmail.com").orElseThrow();
        assertEquals(expectedUser,user);
    }

    @Sql(scripts = USER_ROLE_INSERT, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = USER_ROLE_DELETE, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void findByEmail_nonValidEmail_ThrowsException() {
        String expectedMessage = "Can't find user with email: nonValidEmail";
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userRepository.findByEmail("nonValidEmail")
                        .orElseThrow(() -> new EntityNotFoundException(expectedMessage)));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
