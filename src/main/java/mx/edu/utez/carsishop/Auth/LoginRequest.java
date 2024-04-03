package mx.edu.utez.carsishop.Auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(groups = {Login.class}, message = "El email es obligatorio")
    String email;
    @NotNull(groups = {Login.class}, message = "La contraseña es obligatoria")
    String password;

    public interface Login {}

}
