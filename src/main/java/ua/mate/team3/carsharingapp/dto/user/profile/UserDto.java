package ua.mate.team3.carsharingapp.dto.user.profile;

import java.util.Set;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Role;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
