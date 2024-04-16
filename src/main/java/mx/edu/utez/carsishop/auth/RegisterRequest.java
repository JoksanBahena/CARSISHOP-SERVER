package mx.edu.utez.carsishop.auth;

import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String phone;
}
