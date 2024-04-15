package mx.edu.utez.carsishop.models.town;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.state.State;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "towns")
public class Town {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="name",nullable = false,length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name="state")
    @JsonIgnoreProperties("towns")
    private State state;

    @OneToMany(mappedBy = "town")
    @JsonIgnore
    private List<Address> addresses;
}
