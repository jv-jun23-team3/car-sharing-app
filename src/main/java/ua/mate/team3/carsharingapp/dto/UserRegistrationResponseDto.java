package ua.mate.team3.carsharingapp.dto;

import lombok.Data;

@Data
public class UserRegistrationResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
