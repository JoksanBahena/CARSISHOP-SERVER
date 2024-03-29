package mx.edu.utez.carsishop.models.order;

import mx.edu.utez.carsishop.models.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByAddress(Address address);
}
