package ua.mate.team3.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserInfoRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserRoleRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserInfoResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;
import ua.mate.team3.carsharingapp.mapper.UserMapper;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.RoleRepository;
import ua.mate.team3.carsharingapp.repository.UserRepository;
import ua.mate.team3.carsharingapp.security.AuthenticationService;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;

    /**
     *
     */
    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + request.getEmail()
                    + "is already exists");
        }
        User user = setUserFromRequest(request);
        User savedUser = userRepository.save(user);
        notificationService.sendNotification(
                "The user " + savedUser.getFirstName() + savedUser.getLastName()
                        + "with email: " + savedUser.getEmail() + " has registered");
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional
    public void updateUserRole(Long id, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Can`t find user by id: " + id));
        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.getRoleByName(requestDto.getRole()));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public UserInfoResponseDto getUserInfo() {
        User user = authenticationService.getUser();
        return userMapper.toInfoDto(user);
    }

    @Override
    public UserInfoResponseDto updateUserInfo(UpdateUserInfoRequestDto requestDto) {
        User user = authenticationService.getUser();
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toInfoDto(user);
    }

    private User setUserFromRequest(UserRegistrationRequestDto request) {
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(roleRepository.getRoleByName(Role.RoleName.ROLE_CUSTOMER)));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return user;
    }
}
