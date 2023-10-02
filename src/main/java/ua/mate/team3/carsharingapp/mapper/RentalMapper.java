package ua.mate.team3.carsharingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.model.Rental;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    ResponseRentalDto toResponseDto(Rental rental);

    Rental toEntity(CreateRentalRequestDto requestDto);
}
