package mx.edu.utez.carsishop.models.gender;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mx.edu.utez.carsishop.models.user.User;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="gender")
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "gender", length = 20, nullable = false)
    private String gender;

    @OneToMany(mappedBy = "gender")
    @JsonIgnore
    private List<User> users;
}
