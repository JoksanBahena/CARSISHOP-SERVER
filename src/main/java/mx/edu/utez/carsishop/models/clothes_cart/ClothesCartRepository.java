package mx.edu.utez.carsishop.models.clothes_cart;

import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCart;
import mx.edu.utez.carsishop.models.size.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClothesCartRepository extends JpaRepository<ClothesCart, Long> {
    Optional<ClothesCart> findByShoppingCartAndClothesAndSize(ShoppingCart shoppingCart, Clothes clothes, Size size);
}
