package mx.edu.utez.carsishop.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.shoppingCart.ShoppingCart;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",nullable = false,length = 40)
    private String name;
    @Column(name = "surname",nullable = false,length = 60)
    private String surname;
    @Column(name = "email",unique = true,nullable = false)
    private String username;
    @Column(name = "phone",unique = true,length = 10)
    private String phone;
    @Column(name = "birthdate",nullable = false)
    private String birthdate;
    @Column(name = "status", columnDefinition = "boolean default true")
    private boolean status;

    @ManyToOne
    @JoinColumn(name="gender")
    private Gender gender;

    @Column(name = "password",nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "profilepic")
    private String profilepic;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private Seller seller;

    @OneToMany(mappedBy = "user")
    private List<Card> cards;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Address> addresses;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    @OneToOne(mappedBy = "user")
    private ShoppingCart shoppingCart;

    @Enumerated(EnumType.STRING) 
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority((role.name())));
    }
    @Override
    public boolean isAccountNonExpired() {
       return true;
    }
    @Override
    public boolean isAccountNonLocked() {
       return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
