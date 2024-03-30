package mx.edu.utez.carsishop.models.subcategory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false,length = 40,unique = true)
    private String name;

    @OneToMany(mappedBy = "subcategory")
    @JsonIgnore
    private List<Clothes> clothes;
}
