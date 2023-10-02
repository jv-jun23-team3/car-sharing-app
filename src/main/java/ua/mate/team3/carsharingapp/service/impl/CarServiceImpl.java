package ua.mate.team3.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;
import ua.mate.team3.carsharingapp.mapper.CarMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.service.CarService;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto carRequestDto) {
        return carMapper.toDto(carRepository.save(carMapper.toEntity(carRequestDto)));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto getById(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id)
        ));
    }

    @Override
    public CarDto update(Long id, CreateCarRequestDto carRequestDto) {
        Car updatedCar = carMapper.toEntity(carRequestDto);
        updatedCar.setId(id);
        return carMapper.toDto(carRepository.save(updatedCar));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
