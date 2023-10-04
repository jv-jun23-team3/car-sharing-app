package ua.mate.team3.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;
import ua.mate.team3.carsharingapp.service.CarService;

@Tag(name = "Car Controller", description = "Controller for managing cars")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Create a new car",
            description = "This endpoint is used to create a new car.")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    public CarDto saveCar(@RequestBody @Valid CreateCarRequestDto car) {
        return carService.save(car);
    }

    @Operation(summary = "Get all cars", description = "This endpoint is used to get all cars.")
    @GetMapping
    public List<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @Operation(summary = "Get car by ID",
            description = "This endpoint is used to get a specific car by its ID.")
    @GetMapping("/{id}")
    public CarDto findById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @Operation(summary = "Delete car by ID",
            description = "This endpoint is used to delete a specific car by its ID.")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        carService.deleteById(id);
    }

    @Operation(summary = "Update a car",
            description = "This endpoint is used to "
                    + "update the details of a specific car by its ID.")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id,
                         @RequestBody @Valid CreateCarRequestDto carRequestDto) {
        return carService.update(id, carRequestDto);
    }
}
