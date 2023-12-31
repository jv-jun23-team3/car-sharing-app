package ua.mate.team3.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.mate.team3.carsharingapp.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
