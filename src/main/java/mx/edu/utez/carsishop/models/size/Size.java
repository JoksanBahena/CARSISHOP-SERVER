package mx.edu.utez.carsishop.models.size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothOrder.ClothOrder;
import mx.edu.utez.carsishop.models.clothesCart.ClothesCart;
import mx.edu.utez.carsishop.models.stock.Stock;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "size")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false,length = 30)
    private String name;

    @OneToMany(mappedBy = "size")
    private List<Stock> stock;

    @OneToMany(mappedBy = "size")
    @JsonIgnore
    private List<ClothesCart> clothesCarts;

    @OneToMany(mappedBy = "size")
    @JsonIgnore
    private List<ClothOrder> clothOrders;
}
