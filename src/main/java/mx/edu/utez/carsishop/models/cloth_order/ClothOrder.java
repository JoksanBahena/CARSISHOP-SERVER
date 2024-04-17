package mx.edu.utez.carsishop.models.cloth_order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.size.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "cloth_order")
public class ClothOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "theorder")
    @JsonIgnore
    private Order theorder;

    @ManyToOne
    @JoinColumn(name = "clothes")
    private Clothes clothes;

    @Column
    private int amount;

    @ManyToOne
    @JoinColumn(name = "size")
    private Size size;
}
