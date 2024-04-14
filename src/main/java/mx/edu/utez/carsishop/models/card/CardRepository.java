package mx.edu.utez.carsishop.models.card;

import mx.edu.utez.carsishop.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long aLong);
    List<Card> findByUser(User user);
}
