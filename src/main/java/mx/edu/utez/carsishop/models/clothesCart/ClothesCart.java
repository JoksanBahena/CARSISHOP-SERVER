package mx.edu.utez.carsishop.models.clothesCart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.shoppingCart.ShoppingCart;
import mx.edu.utez.carsishop.models.size.Size;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "clothes_cart")
public class ClothesCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shoppingCart")
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "clothes")
    private Clothes clothes;

    @Column
    private int amount;

    @ManyToOne
    @JoinColumn(name = "size")
    private Size size;
}
