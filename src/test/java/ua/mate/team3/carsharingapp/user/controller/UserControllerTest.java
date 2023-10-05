package ua.mate.team3.carsharingapp.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mate.team3.carsharingapp.config.SqlFilePaths;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserRoleRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserDto;
import ua.mate.team3.carsharingapp.model.Role;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String ENDPOINT_CUSTOMER_ROLE = "/users/15/role";
    private static final Role.RoleName CUSTOMER_ROLE = Role.RoleName.ROLE_CUSTOMER;
    private static final Role.RoleName MANAGER_ROLE = Role.RoleName.ROLE_MANAGER;

    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test update user role from customer to manager")
    @Sql(
            scripts = SqlFilePaths.PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = SqlFilePaths.PATH_FOR_DELETE_ALL_USERS_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateUserRoleById_CustomerToManager_ReturnsUserInfo() throws Exception {
        UpdateUserRoleRequestDto userRole = new UpdateUserRoleRequestDto();
        userRole.setRole(MANAGER_ROLE);
        Role managerRole = new Role();
        managerRole.setName(MANAGER_ROLE);
        managerRole.setId(2L);

        String jsonRequest = objectMapper.writeValueAsString(userRole);

        MvcResult result = mockMvc.perform(put(ENDPOINT_CUSTOMER_ROLE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(Set.of(managerRole), actual.getRoles());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test update user role from customer to manager")
    @Sql(
            scripts = SqlFilePaths.PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = SqlFilePaths.PATH_FOR_DELETE_ALL_USERS_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateUserRoleById_ManagerToCustomer_ReturnsUserInfo() throws Exception {
        UpdateUserRoleRequestDto userRole = new UpdateUserRoleRequestDto();
        userRole.setRole(CUSTOMER_ROLE);
        Role customerRole = new Role();
        customerRole.setName(CUSTOMER_ROLE);
        customerRole.setId(1L);

        String jsonRequest = objectMapper.writeValueAsString(userRole);

        MvcResult result = mockMvc.perform(put(ENDPOINT_CUSTOMER_ROLE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(Set.of(customerRole), actual.getRoles());
    }
}
