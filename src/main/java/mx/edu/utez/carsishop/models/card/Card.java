package mx.edu.utez.carsishop.models.card;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.user.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number",nullable = false)
    private String number;

    @Column(name = "lastFour",nullable = false,length = 4)
    private String lastFour;

    @Column(name = "expiration_date",nullable = false,length = 5)
    private String expirationDate;

    @Column(name = "cvv",nullable = false,length = 3)
    private String cvv;

    @Column(name = "owner",nullable = false,length = 60)
    private String owner;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(mappedBy = "card")
    private List<Order> orders;
}
