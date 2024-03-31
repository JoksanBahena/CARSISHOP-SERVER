package mx.edu.utez.carsishop.models.sellers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.user.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "sellers")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfc",nullable = false)
    private String rfc;

    @Column(name = "curp",nullable = false)
    private String curp;

    @Column(name = "id_image",nullable = false)
    private String id_image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user",referencedColumnName = "id")
    @JsonIgnoreProperties("seller")
    private User user;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<Clothes> clothes;
}
