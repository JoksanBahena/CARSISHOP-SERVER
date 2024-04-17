package mx.edu.utez.carsishop.models.order;

import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByAddress(Address address);
    Optional<Order> findByCard(Card card);

    //List<Order> findByUser(User user);

    @Query(
            value = "SELECT * FROM theorder WHERE user = ? ORDER BY at DESC LIMIT 1",
            nativeQuery = true
    )
    Order findByUser(Long idUser);
}
