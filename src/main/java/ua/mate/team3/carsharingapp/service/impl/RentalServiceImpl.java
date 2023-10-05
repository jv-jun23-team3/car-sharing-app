package ua.mate.team3.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import ua.mate.team3.carsharingapp.service.AuthenticationService;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ResponseRentalDto save(CreateRentalRequestDto requestDto) {
        ResponseRentalDto responseDto =
                rentalMapper.toResponseDto(rentalRepository.save(createRental(requestDto)));
        notificationService.sendNotification(
                "New rental " + responseDto + " is created successfully");
        return responseDto;
    }

    @Override
    public ResponseRentalDto getById(Long id) {
        Optional<Rental> rental = rentalRepository.findById(id);
        if (rental.isEmpty() || !Objects.equals(rental.get().getUser().getId(),
                authenticationService.getUserId())) {
            throw new EntityNotFoundException("Can't find rental by id: " + id);
        }
        return rentalMapper.toResponseDto(rental.get());
    }

    @Override
    @Transactional
    public ResponseRentalDto update(Long id) {
        Optional<Rental> updatedRental = rentalRepository.findById(id);
        if (updatedRental.isEmpty() || updatedRental.get().getActualReturnDate() != null) {
            throw new EntityNotFoundException("Can't find rental by id: " + id);
        }
        updatedRental.get().setActualReturnDate(LocalDateTime.now());
        Car updateCar = updatedRental.get().getCar();
        updateCar.setInventory(updateCar.getInventory() + 1);
        carRepository.save(updateCar);
        notificationService.sendNotification("The rental "
                + updatedRental.get() + " is returned");
        return rentalMapper.toResponseDto(rentalRepository.save(updatedRental.get()));
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
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));
        if (car.getInventory() < 1) {
            throw new IllegalArgumentException("Inventory cannot be less than 0");
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(carRepository.save(car));
        rental.setUser(authenticationService.getUser());
        return rental;
    }
}
