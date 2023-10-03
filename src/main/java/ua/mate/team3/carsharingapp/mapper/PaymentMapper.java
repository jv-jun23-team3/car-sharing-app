package ua.mate.team3.carsharingapp.mapper;

import com.stripe.model.checkout.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.mate.team3.carsharingapp.config.MapperConfig;
import ua.mate.team3.carsharingapp.dto.payment.PaymentResponseDto;
import ua.mate.team3.carsharingapp.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "sessionId", source = "id")
    @Mapping(target = "sessionUrl", source = "url")
    PaymentResponseDto toDtoFromSession(Session session);

    PaymentResponseDto toDto(Payment payment);
}
