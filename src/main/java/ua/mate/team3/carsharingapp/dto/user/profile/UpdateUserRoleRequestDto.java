package ua.mate.team3.carsharingapp.dto.user.profile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Role;

@Data
public class UpdateUserRoleRequestDto {
    @NotNull
    private Role role;
}
