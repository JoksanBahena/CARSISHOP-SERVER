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
    @JsonIgnoreProperties({"user","owner","cvv","expirationDate"})
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;

    @Column(name = "status",nullable = false,columnDefinition = "enum('Paid','In_process','On_the_way','Delivered','Canceled','Returned') default 'Paid'")
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "theorder")
    private List<ClothOrder> clothOrders;

    public enum Status{
        Paid,
        In_process,
        On_the_way,
        Delivered,
        Canceled,
        Returned
    }

}
