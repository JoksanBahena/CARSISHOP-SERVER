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
    @NotNull(groups = {Register.class, Update.class}, message = "El nombre es obligatorio")
    private String name;
    @NotNull(groups = {Register.class, Update.class}, message ="El apellido es obligatorio")
    private String surname;
    @NotNull(groups = {GetInfo.class, Register.class, Update.class, UpdateProfilePic.class}, message ="El nombre de usuario es obligatorio")
    private String username;
    @NotNull(groups = {Register.class, Update.class}, message ="El teléfono es obligatorio")
    private String phone;
    @NotNull(groups = {Register.class}, message ="La fecha de nacimiento es obligatoria")
    private String birthdate;
    @NotNull(groups = {Register.class}, message ="La contraseña es obligatoria")
    private String password;
    @NotNull(groups = {Register.class, UpdateProfilePic.class}, message ="La imagen de perfil es obligatoria")
    private MultipartFile profilepic;
    @NotNull(groups = {Register.class, Update.class}, message ="El género es obligatorio")
    private Long gender;

    public User castToUser() {
        return new User(getId(), getName(), getSurname(), getUsername(), getPhone(), getBirthdate(), null, getPassword(), null, null, null, null, null, null, null);
    }

    public interface Register {}

    public interface GetInfo {}

    public interface Update {}

    public interface UpdateProfilePic {}
}
