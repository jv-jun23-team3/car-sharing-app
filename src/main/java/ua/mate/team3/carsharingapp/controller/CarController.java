package ua.mate.team3.carsharingapp.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.car.CarDto;
import ua.mate.team3.carsharingapp.dto.car.CreateCarRequestDto;
import ua.mate.team3.carsharingapp.service.CarService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    public CarDto saveCar(@RequestBody CreateCarRequestDto car) {
        return carService.save(car);
    }

    @GetMapping
    public List<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CarDto findById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        carService.deleteById(id);
    }

    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id, @RequestBody CreateCarRequestDto carRequestDto) {
        return carService.update(id, carRequestDto);
    }
}
