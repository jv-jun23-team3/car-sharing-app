package ua.mate.team3.carsharingapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.rental.CreateRentalRequestDto;
import ua.mate.team3.carsharingapp.dto.rental.ResponseRentalDto;
import ua.mate.team3.carsharingapp.service.RentalService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseRentalDto createRental(
            @RequestBody @Valid CreateRentalRequestDto rentalRequestDto) {
        return rentalService.save(rentalRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    public List<ResponseRentalDto> getRentalsByUserIdAndIsActive(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return rentalService.getAllRentalsByUserIdAndState(userId, isActive, pageable);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseRentalDto getById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/{id}/return")
    public ResponseRentalDto update(@PathVariable Long id) {
        return rentalService.update(id);
    }
}
