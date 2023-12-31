package ua.mate.team3.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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

@Tag(name = "Rental controller", description = "Endpoints for managing user`s rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create new rental", description = "Add a new rental to available car")
    @PostMapping
    public ResponseRentalDto createRental(
            @RequestBody @Valid CreateRentalRequestDto rentalRequestDto) {
        return rentalService.save(rentalRequestDto);
    }

    @Operation(summary = "Get rentals by user is and it`s status", description = "Get list of all"
            + "user rentals and status of them by user id ")
    @GetMapping
    public List<ResponseRentalDto> getRentalsByUserIdAndIsActive(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable, Authentication authentication) {
        return rentalService.getAllRentalsByUserIdAndState(userId, isActive, pageable,
                authentication);
    }

    @Operation(summary = "Get rental by id", description = "Get existing rental by id")
    @GetMapping("/{id}")
    public ResponseRentalDto getById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @Operation(summary = "Update car rental", description = "Set actual return date")
    @PutMapping("/{id}/return")
    public ResponseRentalDto update(@PathVariable Long id) {
        return rentalService.update(id);
    }
}
