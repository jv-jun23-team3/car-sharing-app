package ua.mate.team3.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;

public interface RentalService {
    ResponseRentalDto save(CreateRentalRequestDto requestDto);

    List<ResponseRentalDto> getAllOfCurrentUserByState(Boolean isActive, Pageable pageable);

    List<ResponseRentalDto> getAllOfCurrentUser(Pageable pageable);

    ResponseRentalDto getById(Long id);

    ResponseRentalDto update(Long id);

    void deleteById(Long id);

    List<ResponseRentalDto> getAllRentalsByUserIdAndState(
            Long userId, Boolean isActive, Pageable pageable);
}
