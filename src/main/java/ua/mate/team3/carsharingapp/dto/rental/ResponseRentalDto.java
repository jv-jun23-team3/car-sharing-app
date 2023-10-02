package ua.mate.team3.carsharingapp.dto.rental;

import jakarta.persistence.Column;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Car;

import java.time.LocalDateTime;

@Data
public class ResponseRentalDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Car car;
}
