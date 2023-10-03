package ua.mate.team3.carsharingapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.mate.team3.carsharingapp.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStatus(Payment.Status status);

    Payment getBySessionId(String sessionId);

    @Query("FROM Payment p JOIN FETCH p.rental r JOIN FETCH r.user u WHERE u.id = :userId")
    List<Payment> findAllByUserId(Long userId);
}
