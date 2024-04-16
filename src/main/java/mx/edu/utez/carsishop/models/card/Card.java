package mx.edu.utez.carsishop.models.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "number",nullable = false,length = 100)
    private String number;

    @Column(name = "lastFour",nullable = false,length = 4)
    private String lastFour;

    @Column(name = "expiration_date",nullable = false,length = 50)
    private String expirationDate;

    @Column(name = "cvv",nullable = false,length = 50)
    private String cvv;

    @Column(name = "owner",nullable = false,length = 60)
    private String owner;

    //enable
    @Column(name = "enable",nullable = false, columnDefinition = "tinyint(1) default 1")
    private boolean enable;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(mappedBy = "card")
    @JsonIgnore
    private List<Order> orders;
}
