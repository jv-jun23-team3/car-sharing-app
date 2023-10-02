package ua.mate.team3.carsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.mapper.RentalMapper;
import ua.mate.team3.carsharingapp.repository.RentalRepository;
import ua.mate.team3.carsharingapp.service.RentalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    @Override
    public ResponseRentalDto save(CreateRentalRequestDto requestDto) {
        return null;
    }

    @Override
    public ResponseRentalDto setActualReturnDate(LocalDateTime returnDate) {
        return null;
    }

    @Override
    public Set<ResponseRentalDto> getAllActiveRentalsOfCurrentUser() {
        return null;
    }

    @Override
    public List<ResponseRentalDto> getAll() {
        return null;
    }

    @Override
    public ResponseRentalDto getByIdOfCurrentUser(Long id) {
        return null;
    }

    @Override
    public ResponseRentalDto getById(Long id) {
        return null;
    }

    @Override
    public ResponseRentalDto update(Long id, CreateRentalRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
