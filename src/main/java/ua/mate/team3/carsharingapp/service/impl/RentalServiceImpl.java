package ua.mate.team3.carsharingapp.service.impl;

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
import ua.mate.team3.carsharingapp.exception.ActionForbiddenException;
import ua.mate.team3.carsharingapp.exception.EmptyInventoryException;
import ua.mate.team3.carsharingapp.mapper.RentalMapper;
import ua.mate.team3.carsharingapp.model.Car;
import ua.mate.team3.carsharingapp.model.Payment;
import ua.mate.team3.carsharingapp.model.Rental;
import ua.mate.team3.carsharingapp.model.User;
import ua.mate.team3.carsharingapp.repository.CarRepository;
import ua.mate.team3.carsharingapp.repository.PaymentRepository;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.AuthenticationService;
import ua.mate.team3.carsharingapp.service.NotificationService;
import ua.mate.team3.carsharingapp.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String RENTAL_INFO_TEMPLATE = "The rental: "
            + "\n📋 **Rental ID:** %d"
            + "\n🚘 **Car ID:** %d"
            + "\n📆 **Rental Date:** %s"
            + "\n🙆 **User ID:** %d"
            + "\n🫵🔙 **Expected Return Date:** %s";
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public ResponseRentalDto save(CreateRentalRequestDto requestDto) {
        if (paymentRepository.findByStatusAndByUserId(
                authenticationService.getUserId(), Payment.Status.PENDING).isPresent()) {
            throw new ActionForbiddenException(
                    "You can't rent new car with ongoing pending payment");
        }
        ResponseRentalDto responseDto = rentalMapper.toResponseDto(
                rentalRepository.save(createRental(requestDto)));

        notificationService.sendNotification(
                formMessage(responseDto) + " is created successfully");
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
        ResponseRentalDto responseDto = rentalMapper.toResponseDto(
                rentalRepository.save(updatedRental.get()));
        notificationService.sendNotification(formMessage(responseDto) + " is returned");
        return responseDto;
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

    @Scheduled(cron = "0 0 0 * * ?") // This will run the method every day at midnight
    public void handleOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository
                .findAllByActualReturnDateIsNullAndReturnDateBefore(LocalDateTime.now());
        if (overdueRentals.isEmpty()) {
            notificationService.sendNotification("No rentals overdue today🤤");
            return;
        }
        for (Rental rental : overdueRentals) {
            notificationService.sendNotification(formMessage(
                    rentalMapper.toResponseDto(rental)) + " is overdue");
        }
    }

    private Rental createRental(CreateRentalRequestDto rentalRequestDto) {
        Rental rental = new Rental();
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDate(rentalRequestDto.getReturnDate());
        Car car = carRepository.findById(rentalRequestDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));
        if (car.getInventory() < 1) {
            throw new EmptyInventoryException("Inventory cannot be less than 0");
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(carRepository.save(car));
        rental.setUser(authenticationService.getUser());
        return rental;
    }

    private String formMessage(ResponseRentalDto rentalDto) {
        return String.format(RENTAL_INFO_TEMPLATE,
                rentalDto.getId(),
                rentalDto.getCarId(),
                rentalDto.getRentalDate(),
                rentalDto.getUserId(),
                rentalDto.getReturnDate());
    }
}
