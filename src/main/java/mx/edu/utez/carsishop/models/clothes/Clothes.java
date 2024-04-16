package mx.edu.utez.carsishop.models.clothes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.clothOrder.ClothOrder;
import mx.edu.utez.carsishop.models.clothesCart.ClothesCart;
import mx.edu.utez.carsishop.models.images.Image;
import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.stock.Stock;
import mx.edu.utez.carsishop.models.subcategory.Subcategory;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "clothes")
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false,length = 100)
    private String name;

    @Column(name = "description",nullable = false)
    private String description;

    //1. PENDING 2. APPROVED 3. REJECTED
    @Column(name = "request_status", columnDefinition = "varchar(20) DEFAULT 'PENDING'")
    private String request_status;

    @Column(name = "status", columnDefinition = "boolean DEFAULT true")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "seller")
    @JsonIgnoreProperties({"user"})
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory")
    private Subcategory subcategory;

    @OneToMany(mappedBy = "clothes")
    private List<Image> images;

    @OneToMany(mappedBy = "clothes")
    private List<Stock> stock;

    @OneToMany(mappedBy = "clothes")
    @JsonIgnore
    private List<ClothOrder> clothOrders;

    @OneToMany(mappedBy = "clothes")
    @JsonIgnore
    private List<ClothesCart> clothesCarts;


}
    