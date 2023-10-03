package ua.mate.team3.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Car;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private Car.TypeName type;
    private Integer inventory;
    private BigDecimal dailyFee;
}
