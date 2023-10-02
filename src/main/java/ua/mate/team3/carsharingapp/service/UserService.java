package ua.mate.team3.carsharingapp.service;

import ua.mate.team3.carsharingapp.dto.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
