package ua.mate.team3.carsharingapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.mate.team3.carsharingapp.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("FROM Rental r "
            + "LEFT JOIN FETCH r.car "
            + "LEFT JOIN FETCH r.user "
            + "WHERE (:userId is null OR r.user.id = :userId) "
            + "AND (:isActive is null OR (r.actualReturnDate IS NULL AND :isActive = true) "
            + "OR (r.actualReturnDate IS NOT NULL AND :isActive = false))")
    List<Rental> findByUserIdAndIsActive(Long userId, Boolean isActive, Pageable pageable);

    @Query("FROM Rental r "
            + "LEFT JOIN FETCH r.car "
            + "WHERE (:isActive IS NULL OR (r.actualReturnDate IS NULL AND :isActive = true) "
            + "OR (r.actualReturnDate IS NOT NULL AND :isActive = false))")
    List<Rental> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("FROM Rental r "
            + "LEFT JOIN FETCH r.car "
            + "LEFT JOIN FETCH r.user "
            + "WHERE ((r.actualReturnDate IS NULL AND r.returnDate < :currentDate) "
            + "OR (r.actualReturnDate IS NOT NULL AND r.actualReturnDate > r.returnDate))")
    List<Rental> findAllByActualReturnDateIsNullAndReturnDateBefore(LocalDateTime currentDate);
}
