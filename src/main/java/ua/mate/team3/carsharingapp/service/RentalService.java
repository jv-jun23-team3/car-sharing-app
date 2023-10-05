package ua.mate.team3.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;

public interface RentalService {
    ResponseRentalDto save(CreateRentalRequestDto requestDto);

    ResponseRentalDto getById(Long id);

    ResponseRentalDto update(Long id);

    List<ResponseRentalDto> getAllRentalsByUserIdAndState(
            Long userId, Boolean isActive, Pageable pageable,
            Authentication authentication);
}
