package mx.edu.utez.carsishop.models.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(name = "name",nullable = false,length = 30)
    private String name;

    @ManyToOne
    @JoinColumn(name="state")
    @JsonIgnoreProperties("towns")
    private State state;

    @ManyToOne
    @JoinColumn(name="town")
    @JsonIgnoreProperties("state")
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

    @Column(name="enable",nullable = false, columnDefinition = "tinyint(1) default 1")
    private boolean enable;

    @ManyToOne
    @JoinColumn(name="user")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private List<Order> orders;

    @Transient
    @JsonIgnore
    private CryptoService cryptoService = new CryptoService();


    public void encryptData() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.name= cryptoService.encrypt(this.name);
        this.cp = cryptoService.encrypt(this.cp);
        this.suburb = cryptoService.encrypt(this.suburb);
        this.street = cryptoService.encrypt(this.street);
        this.intnumber = cryptoService.encrypt(this.intnumber);
        this.extnumber = cryptoService.encrypt(this.extnumber);
    }

    public void decryptData() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.name= cryptoService.decrypt(this.name);
        this.cp = cryptoService.decrypt(this.cp);
        this.suburb = cryptoService.decrypt(this.suburb);
        this.street = cryptoService.decrypt(this.street);
        this.intnumber = cryptoService.decrypt(this.intnumber);
        this.extnumber = cryptoService.decrypt(this.extnumber);
    }
}
