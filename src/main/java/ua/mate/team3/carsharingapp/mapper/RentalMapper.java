package ua.mate.team3.carsharingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.mate.team3.carsharingapp.config.MapperConfig;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.model.Rental;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    ResponseRentalDto toResponseDto(Rental rental);
}
