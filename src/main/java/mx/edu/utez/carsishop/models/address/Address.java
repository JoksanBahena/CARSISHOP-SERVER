package mx.edu.utez.carsishop.models.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.state.State;
import mx.edu.utez.carsishop.models.town.Town;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.utils.CryptoService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="state")
    private State state;

    @ManyToOne
    @JoinColumn(name="town")
    private Town town;

    @Column(name="cp",nullable = false, length = 5)
    private String cp;

    @Column(name="suburb",nullable = false, length = 100)
    private String suburb;

    @Column(name="street",nullable = false, length = 100)
    private String street;

    @Column(name="intnumber",nullable = false, length = 10)
    private String intnumber;

    @Column(name="extnumber",nullable = false, length = 10)
    private String extnumber;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private List<Order> orders;

    @Transient
    private CryptoService cryptoService = new CryptoService();


    public void encryptData() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.cp = cryptoService.encrypt(this.cp);
        this.suburb = cryptoService.encrypt(this.suburb);
        this.street = cryptoService.encrypt(this.street);
        this.intnumber = cryptoService.encrypt(this.intnumber);
        this.extnumber = cryptoService.encrypt(this.extnumber);
    }
}
