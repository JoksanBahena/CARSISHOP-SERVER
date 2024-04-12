package mx.edu.utez.carsishop.models.clothesCart;

import mx.edu.utez.carsishop.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothesCartRepository extends JpaRepository<ClothesCart, Long> {
    List<ClothesCart> findByUser(User user);
}
