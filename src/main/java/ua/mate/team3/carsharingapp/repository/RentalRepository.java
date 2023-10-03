package ua.mate.team3.carsharingapp.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.mate.team3.carsharingapp.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r "
            + "LEFT JOIN FETCH r.car "
            + "LEFT JOIN FETCH r.user "
            + "WHERE r.user.id = :userId "
            + "AND ((r.actualReturnDate IS NULL AND :isActive = true) "
            + "OR (r.actualReturnDate IS NOT NULL AND :isActive = false))")
    List<Rental> findByUserIdAndIsActive(Long userId, Boolean isActive, Pageable pageable);
}