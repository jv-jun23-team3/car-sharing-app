package ua.mate.team3.carsharingapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.mapper.RentalMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.security.AuthenticationService;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

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
            Long userId, Boolean isActive, Pageable pageable,
            Authentication authentication) {
        if (userId == null) {
            return getAllRentals(isActive, pageable, authentication);
        } else {
            checkValidIdIfNotManager(userId, authentication);
            return rentalRepository.findByUserIdAndIsActive(userId, isActive, pageable).stream()
                    .map(rentalMapper::toResponseDto)
                    .toList();
        }
    }

    private void checkValidIdIfNotManager(Long userId, Authentication authentication) {
        GrantedAuthority grantedAuthority = getGrantedAuthority(authentication);
        if (grantedAuthority.getAuthority().equals(ROLE_MANAGER)) {
            return;
        }
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getId(), userId)) {
            throw new AccessDeniedException("Only Manager can get other user information");
        }
    }

    private List<ResponseRentalDto> getAllRentals(Boolean isActive, Pageable pageable,
                                                  Authentication authentication) {
        GrantedAuthority grantedAuthority = getGrantedAuthority(authentication);
        if (grantedAuthority.getAuthority().equals(ROLE_MANAGER)) {
            return rentalRepository.findByIsActive(isActive, pageable).stream()
                    .map(rentalMapper::toResponseDto)
                    .toList();
        }
        throw new AccessDeniedException("Only Manager can get all rentals");
    }

    private GrantedAuthority getGrantedAuthority(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        GrantedAuthority grantedAuthority = authorities.stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("Can't find role"));
        return grantedAuthority;
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

    @Scheduled(cron = "0 0 0 * * ?") // This will run the method every day at midnight
    public void handleOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository
                .findAllByActualReturnDateIsNullAndReturnDateBefore(LocalDateTime.now());
        if (overdueRentals.isEmpty()) {
            notificationService.sendNotification("No rentals overdue today!");
            return;
        }
        for (Rental rental : overdueRentals) {
            notificationService.sendNotification("Rental: " + rental + " is overdue");
        }
    }
}
