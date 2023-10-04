package ua.mate.team3.carsharingapp.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ResponseRentalDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Long carId;
    private Long userId;
}
