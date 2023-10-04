package ua.mate.team3.carsharingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import ua.mate.team3.carsharingapp.model.Payment;

@Data
public class PaymentDto {
    private Long id;
    private Payment.Status status;
    private Payment.Type type;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;
}
