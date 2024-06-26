package mx.edu.utez.carsishop.models.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.cloth_order.ClothOrder;
import mx.edu.utez.carsishop.models.user.User;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "theorder")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="at",nullable = false)
    private Date at;

    @ManyToOne
    @JoinColumn(name = "card")
    @JsonIgnoreProperties({"user","number","cvv","expirationDate"})
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;

    @Column(name = "status",nullable = false,columnDefinition = "enum('PAID','IN_PROCESS','ON_THE_WAY','DELIVERED','CANCELED','RETURNED') default 'PAID'")
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "theorder")
    private List<ClothOrder> clothOrders;

    @Column(name = "paid",nullable = false,columnDefinition = "boolean default false")
    private boolean paid;

    public enum Status{
        PAID,
        IN_PROCESS,
        ON_THE_WAY,
        DELIVERED,
        CANCELED,
        RETURNED
    }
}
