package ua.mate.team3.carsharingapp.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.user.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.user.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;
import ua.mate.team3.carsharingapp.mapper.UserMapper;
import ua.mate.team3.carsharingapp.model.Role;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.RoleRepository;
import ua.mate.team3.carsharingapp.repository.UserRepository;
import ua.mate.team3.carsharingapp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + request.getEmail()
                    + "is already exists");
        }
        User user = setUserFromRequest(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
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
