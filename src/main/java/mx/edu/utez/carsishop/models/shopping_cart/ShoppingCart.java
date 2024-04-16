package mx.edu.utez.carsishop.models.shopping_cart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCart;
import mx.edu.utez.carsishop.models.user.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user",referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "shoppingCart")
    private List<ClothesCart> clothesCarts;
}
