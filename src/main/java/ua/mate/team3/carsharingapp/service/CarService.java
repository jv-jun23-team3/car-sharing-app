package ua.mate.team3.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;

public interface CarService {
    CarDto save(CreateCarRequestDto carRequestDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto carRequestDto);

    void deleteById(Long id);
}
