package ua.mate.team3.carsharingapp.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Car;

@Data
public class ResponseRentalDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Car car;
}
