package ua.mate.team3.carsharingapp.dto.user.profile;

import lombok.Data;

@Data
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
