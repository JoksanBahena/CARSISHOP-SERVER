package mx.edu.utez.carsishop.controllers.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;

    @NotNull(message = "El nombre es obligatorio")
    private String name;
    @NotNull(message = "El apellido es obligatorio")
    private String surname;
    @NotNull(message = "El email es obligatorio")
    private String username;
    @NotNull(message = "El teléfono es obligatorio")
    private String phone;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private String birthdate;
    @NotNull(message = "La contraseña es obligatoria")
    private String password;
    @NotNull(message = "Foto de perfil es obligatoria")
    private MultipartFile profilepic;

    private Long gender;

    public User castToUser() {
        return new User(getId(), getName(), getSurname(), getUsername(), getPhone(), getBirthdate(), null, getPassword(), null, null, null, null, null, null, null);
    }
}
