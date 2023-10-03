package ua.mate.team3.carsharingapp.service;

import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserInfoRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UpdateUserRoleRequestDto;
import ua.mate.team3.carsharingapp.dto.user.profile.UserInfoResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    void updateUserRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserInfoResponseDto getUserInfo();

    UserInfoResponseDto updateUserInfo(UpdateUserInfoRequestDto requestDto);
}
