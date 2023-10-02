package ua.mate.team3.carsharingapp.mapper;

import org.mapstruct.Mapper;
import ua.mate.team3.carsharingapp.config.MapperConfig;
import ua.mate.team3.carsharingapp.dto.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toResponseDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}