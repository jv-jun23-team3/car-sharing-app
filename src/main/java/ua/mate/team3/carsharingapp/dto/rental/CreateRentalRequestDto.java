package ua.mate.team3.carsharingapp.dto.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull
    @Min(1)
    private Long carId;
}
