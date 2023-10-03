package ua.mate.team3.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.mapper.RentalMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.security.AuthenticationService;
import ua.mate.team3.carsharingapp.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public ResponseRentalDto save(CreateRentalRequestDto requestDto) {
        return rentalMapper.toResponseDto(rentalRepository.save(createRental(requestDto)));
    }

    @Override
    public List<ResponseRentalDto> getAllOfCurrentUserByState(Boolean isActive, Pageable pageable) {
        return getAllRentalsByUserIdAndState(authenticationService
                .getUser().getId(), isActive, pageable);
    }

    @Override
    public List<ResponseRentalDto> getAllOfCurrentUser(Pageable pageable) {
        return rentalRepository.findAllByUserId(
                authenticationService.getUser().getId(), pageable).stream()
                .map(rentalMapper::toResponseDto)
                .toList();
    }

    @Override
    public ResponseRentalDto getById(Long id) {
        return rentalMapper.toResponseDto(rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: " + id)
        ));
    }

    @Override
    @Transactional
    public ResponseRentalDto update(Long id) {
        Rental updatedRental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: " + id)
        );
        updatedRental.setActualReturnDate(LocalDateTime.now());
        Car updateCar = updatedRental.getCar();
        updateCar.setInventory(updateCar.getInventory() + 1);
        carRepository.save(updateCar);
        return rentalMapper.toResponseDto(rentalRepository.save(updatedRental));
    }

    @Override
    public void deleteById(Long id) {
        rentalRepository.deleteById(id);
    }

    @Override
    public List<ResponseRentalDto> getAllRentalsByUserIdAndState(
            Long userId, Boolean isActive, Pageable pageable) {
        return rentalRepository.findByUserIdAndIsActive(userId, isActive, pageable).stream()
                .map(rentalMapper::toResponseDto)
                .toList();
    }

    private Rental createRental(CreateRentalRequestDto rentalRequestDto) {
        Rental rental = new Rental();
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDate(rentalRequestDto.getReturnDate());
        Car car = carRepository.findById(rentalRequestDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));
        car.setInventory(car.getInventory() - 1);
        rental.setCar(carRepository.save(car));
        rental.setUser(authenticationService.getUser());
        return rental;
    }
}
