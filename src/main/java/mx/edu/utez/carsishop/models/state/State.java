package mx.edu.utez.carsishop.models.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.town.Town;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="states")

public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false, length = 40)
    private String name;

    @OneToMany(mappedBy = "state")

    private List<Town> towns;

    @OneToMany(mappedBy = "state")
    @JsonIgnore
    private List<Address> addresses;
}
