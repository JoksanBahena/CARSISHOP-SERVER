package mx.edu.utez.carsishop.Auth;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.Jwt.JwtBlackList;
import mx.edu.utez.carsishop.Jwt.JwtService;
import mx.edu.utez.carsishop.models.email.EmailDetails;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.services.email.EmailService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    EmailDetails emailDetails;

    public CustomResponse<AuthResponse> login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user=userRepository.findByUsername(request.getEmail()).orElseThrow();
        String token=jwtService.getToken(user);
        AuthResponse authResponse= AuthResponse.builder()
                .token(token)
                .build();
        return new CustomResponse<>(
                authResponse,
                false,
                200,
                "OK"
        );
    }

    public CustomResponse<AuthResponse> register(RegisterRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getEmail());
        if (userOptional.isPresent()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "User already exists"
            );
        }
        User user = User.builder()
            .username(request.getEmail())
            .password(passwordEncoder.encode( request.getPassword()))
            .name(request.getName())
            .surname(request.getSurname())
            .phone(request.getPhone())
            .role(Role.CUSTOMER)
            .build();

        userRepository.save(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
        return new CustomResponse<>(
                authResponse,
                false,
                200,
                "OK"
        );
    }

    @Transactional(readOnly = true)
    public CustomResponse<AuthResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> userOptional = this.userRepository.findByUsername(forgotPasswordRequest.getEmail());
        if(userOptional.isPresent()){
            User user = User.builder()
                    .username(userOptional.get().getUsername())
                    .name(userOptional.get().getName())
                    .surname(userOptional.get().getSurname())
                    .role(userOptional.get().getRole())
                    .build();

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwtService.getToken(user))
                    .build();

            String url = "http://44.216.6.63:3000/reset-pass/";
            String link = " <a href=\"" + url + authResponse.token + "\">Reestablecer contraseña</a> ";
            String img = "https://res.cloudinary.com/sigsa/image/upload/v1681795223/sccul/logo/logo_ydzl8i.png";

            String firma = "<div style=\"display: flex; align-items: center;\">" +
                    "<img src=\"" + img + "\" alt=\"Logo SIOCU\" width=\"100\" height=\"100\" style=\"margin-right: 20px;\">" +
                    "<div>" +
                    "<h3 style=\"font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60;\">SIOCU Academy</h3>" +
                    "<p style=\"font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60;\">sccul.academy@gmail.com</p>" +
                    "</div>" +
                    "</div>";


            String body = "<html>" +
                    "<head>" +
                    "<style>" +
                    "h2 { font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60; }" +
                    "p { font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60; }" +
                    "a { color: #002e60; text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h2>Hola, estimado usuario.</h2>" +
                    "<p>" +
                    "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en SIOCU. Si no has solicitado el restablecimiento de contraseña, puedes ignorar este mensaje." +
                    "</p>" +
                    authResponse.token +
                    "<p>" +
                    "Para restablecer tu contraseña, haz clic en el siguiente enlace:" + link +
                    "</p>" +
                    "<p>" +
                    "Este enlace es válido por 30 minutos. Si no realizas el restablecimiento de contraseña dentro de este plazo, deberás solicitar un nuevo enlace." +
                    "</p>" +
                    "<p>" +
                    "Por motivos de seguridad, te recomendamos que elijas una contraseña segura que no hayas utilizado antes y que no compartas tu contraseña con nadie. Si tienes alguna pregunta o necesitas ayuda para restablecer tu contraseña, no dudes en contactarnos." +
                    "</p>" +
                    firma +
                    "</body>" +
                    "</html>";

            emailDetails = new EmailDetails(
                    forgotPasswordRequest.getEmail(),
                    "NoReply. Reestablece tu contraseña " + " \n\n",
                    body
            );
            emailService.sendHtmlMail(emailDetails);

            return new CustomResponse<>(
                    null,
                    false,
                    200,
                    "Correo enviado correctamente"
            );
        }

        return new CustomResponse<>(
                null,
                true,
                400,
                "Correo no encontrado"
        );
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<Integer> resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        try {
            String email = jwtService.getUsernameFromToken(token);

            String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getNewPassword());

            if(JwtBlackList.isTokenBlacklisted(token)){
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "Token inválido"
                );
            }

            if(!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())) {
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "Las contraseñas no coinciden"
                );
            }

            JwtBlackList.addToBlacklist(token);

            return new CustomResponse<>(
                    this.userRepository.updatePasswordById(encodedPassword, userRepository.findByUsername(email).get().getId()),
                    false,
                    200,
                    "Contraseña actualizada correctamente"
            );
        } catch (JwtException | ExecutionException e) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Token inválido"
            );
        }
    }

}
