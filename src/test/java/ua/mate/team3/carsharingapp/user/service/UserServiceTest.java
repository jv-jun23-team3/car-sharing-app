package ua.mate.team3.carsharingapp.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserInfoRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserRoleRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserInfoResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;
import ua.mate.team3.carsharingapp.mapper.UserMapper;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.RoleRepository;
import ua.mate.team3.carsharingapp.repository.UserRepository;
import ua.mate.team3.carsharingapp.service.AuthenticationService;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Long MANAGER_ID = 2L;
    private static final Long SPARE_ID = 2L;
    private static UserRegistrationRequestDto requestDto;
    private static UserRegistrationRequestDto requestExistingDto;
    private static User existingUser;
    private static UserDto existingUserDto;
    private static User user;
    private static Role roleCustomer;
    private static Role roleAdmin;
    private static UserRegistrationResponseDto responseDto;
    private static UpdateUserRoleRequestDto updateUserRoleRequestDto;
    private static UpdateUserInfoRequestDto updateUserInfoRequestDto;
    private static UserInfoResponseDto userInfoResponseDto;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private NotificationService notificationService;

    @BeforeAll
    static void beforeAll() {
        roleCustomer = new Role();
        roleAdmin = new Role();
        roleAdmin.setId(MANAGER_ID);
        roleAdmin.setName(Role.RoleName.ROLE_MANAGER);

        updateUserRoleRequestDto = new UpdateUserRoleRequestDto();
        updateUserRoleRequestDto.setRole(Role.RoleName.ROLE_CUSTOMER);

        requestDto = new UserRegistrationRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@example.com");
        requestDto.setPassword("password");
        requestDto.setRepeatPassword("password");

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");

        responseDto = new UserRegistrationResponseDto();
        responseDto.setId(SPARE_ID);
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setEmail("john.doe@example.com");

        requestExistingDto = new UserRegistrationRequestDto();
        requestExistingDto.setEmail("admin@example.com");
        requestExistingDto.setFirstName("admin");
        requestExistingDto.setLastName("admin");
        requestExistingDto.setPassword("securePassword123");
        requestExistingDto.setRepeatPassword("securePassword123");

        existingUser = new User();
        existingUser.setId(SPARE_ID);
        existingUser.setRoles(Set.of(roleAdmin));
        existingUser.setPassword("securePassword123");
        existingUser.setEmail("adminEmail");
        existingUser.setFirstName("admin");
        existingUser.setLastName("admin");

        existingUserDto = new UserDto();
        existingUserDto.setEmail("adminEmail");
        existingUserDto.setId(SPARE_ID);
        existingUserDto.setPassword("securePassword123");
        existingUserDto.setRoles(Set.of(roleAdmin));
        existingUserDto.setFirstName("admin");
        existingUserDto.setLastName("admin");

        userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setId(SPARE_ID);
        userInfoResponseDto.setEmail("admin@example.com");
        userInfoResponseDto.setFirstName("admin");
        userInfoResponseDto.setLastName("admin");

        updateUserInfoRequestDto = new UpdateUserInfoRequestDto();
        updateUserInfoRequestDto.setFirstName("admin");
        updateUserInfoRequestDto.setLastName("admin");
    }

    @Test
    @DisplayName("Test register with not registered user")
    public void register_notExistingUser_success() throws RegistrationException {
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("hashedPassword");
        when(roleRepository.getRoleByName(Role.RoleName.ROLE_CUSTOMER)).thenReturn(roleCustomer);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });
        when(userMapper.toResponseDto(any())).thenReturn(responseDto);

        UserRegistrationResponseDto response = userService.register(requestDto);

        assertNotNull(response);
        assertEquals(responseDto, response);
    }

    @Test
    @DisplayName("Update register with not registered user")
    public void update_userRole_success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(userRepository.save(any())).thenReturn(existingUser);
        when(userMapper.toDto(any())).thenReturn(existingUserDto);
        assertEquals(existingUserDto,
                userService.updateUserRole(SPARE_ID, updateUserRoleRequestDto));
    }

    @Test
    @DisplayName("Get user info")
    public void getUserInfo_success() {
        when(authenticationService.getUser()).thenReturn(existingUser);

        when(userMapper.toInfoDto(existingUser)).thenReturn(userInfoResponseDto);
        assertEquals(userInfoResponseDto, userService.getUserInfo());
    }

    @Test
    @DisplayName("Update user first and last name")
    public void updateUserInfo_existingUser_success() {
        when(authenticationService.getUser()).thenReturn(existingUser);
        when(userMapper.toInfoDto(any())).thenReturn(userInfoResponseDto);
        assertEquals(userInfoResponseDto, userService.updateUserInfo(updateUserInfoRequestDto));
    }
}
