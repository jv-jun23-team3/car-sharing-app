package ua.mate.team3.carsharingapp.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.mate.team3.carsharingapp.validation.FieldMatch;

@Data
@FieldMatch(field = "password",
        fieldMatch = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 55)
    private String password;
    @NotBlank
    @Size(min = 8, max = 55)
    private String repeatPassword;
    @NotBlank
    @Size(min = 1)
    private String firstName;
    @NotBlank
    @Size(min = 1)
    private String lastName;
}
