package ua.mate.team3.carsharingapp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.mate.team3.carsharingapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u LEFT JOIN FETCH u.roles where u.email = :email")
    Optional<User> findByEmail(String email);
}
