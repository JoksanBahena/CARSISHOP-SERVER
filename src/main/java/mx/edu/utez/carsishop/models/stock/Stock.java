package mx.edu.utez.carsishop.models.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.size.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity",nullable = false)
    private int quantity;

    @Column(name = "price",nullable = false, columnDefinition = "DECIMAL(10,2)")
    private float price;

    @ManyToOne
    @JoinColumn(name = "size")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "clothes")
    private Clothes clothes;


}
