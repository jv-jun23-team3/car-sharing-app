package ua.mate.team3.carsharingapp.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Payment;

@Data
public class PaymentRequestDto {
    @NotNull
    @Min(1)
    private Long rentalId;

    @NotNull
    private Payment.Type type;
}
