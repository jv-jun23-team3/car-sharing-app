package ua.mate.team3.carsharingapp.service;

import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RentalService {
    ResponseRentalDto save(CreateRentalRequestDto requestDto);

    ResponseRentalDto setActualReturnDate(LocalDateTime returnDate);

    Set<ResponseRentalDto> getAllActiveRentalsOfCurrentUser();

    List<ResponseRentalDto> getAll();

    ResponseRentalDto getByIdOfCurrentUser(Long id);

    ResponseRentalDto getById(Long id);

    ResponseRentalDto update(Long id, CreateRentalRequestDto requestDto);

    void deleteById(Long id);
}
