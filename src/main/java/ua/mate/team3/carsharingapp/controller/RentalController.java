package ua.mate.team3.carsharingapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public ResponseRentalDto createRental(
            @RequestBody @Valid CreateRentalRequestDto rentalRequestDto) {
        return rentalService.save(rentalRequestDto);
    }

    @GetMapping("/user")
    public List<ResponseRentalDto> getRentalsByUserIdAndIsActive(
            @RequestParam Long userId, @RequestParam Boolean isActive, Pageable pageable) {
        return rentalService.getAllRentalsByUserIdAndState(userId, isActive, pageable);
    }

    @GetMapping("/{id}")
    public ResponseRentalDto getById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PutMapping("/{id}/return")
    public ResponseRentalDto update(@PathVariable Long id) {
        return rentalService.update(id);
    }

    @GetMapping
    public List<ResponseRentalDto> getAll(Pageable pageable) {
        return rentalService.getAllOfCurrentUser(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        rentalService.deleteById(id);
    }

    @GetMapping("/state")
    public List<ResponseRentalDto> getAllByState(
            @RequestParam Boolean isActive, Pageable pageable) {
        return rentalService.getAllOfCurrentUserByState(isActive, pageable);
    }
}
