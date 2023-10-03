package ua.mate.team3.carsharingapp.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Car;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    private Car.TypeName type;
    @NotNull
    @Min(1)
    private Integer inventory;
    @NotNull
    @Min(0)
    private BigDecimal dailyFee;
}
