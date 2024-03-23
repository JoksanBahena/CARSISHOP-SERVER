package mx.edu.utez.carsishop.models.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.clothOrder.ClothOrder;
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
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;

    @Column(name = "status",nullable = false,length = 10,columnDefinition = "enum('Paid','In process','On the way','Delivered','Canceled','Returned') default 'Paid'")
    private String status;

    @OneToMany(mappedBy = "theorder")
    private List<ClothOrder> clothOrders;

}
