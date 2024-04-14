package mx.edu.utez.carsishop.Auth;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.Jwt.JwtBlackList;
import mx.edu.utez.carsishop.Jwt.JwtService;
import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.email.EmailDetails;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.gender.GenderRepository;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.services.email.EmailService;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.UploadImage;
import mx.edu.utez.carsishop.utils.ValidateTypeFile;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private GenderRepository genderRepository;

    private CryptoService cryptoService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    EmailDetails emailDetails;

    public ResponseEntity<Object> login(LoginRequest request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException  {
        request.setEmail(cryptoService.decrypt(request.getEmail()));
        request.setPassword(cryptoService.decrypt(request.getPassword()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByUsername(request.getEmail()).orElseThrow();
        if (!userRepository.getStatusByEmail(request.getEmail())) {
            return new ResponseEntity<>(new CustomResponse<>(
                    null,
                    true,
                    400,
                    "La cuenta no ha sido confirmada", 0),
                    HttpStatus.BAD_REQUEST);
        }

        String token = jwtService.getToken(user);
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        return new ResponseEntity<>(new CustomResponse<>(
                authResponse,
                false,
                200,
                "OK", 1),
                HttpStatus.OK);

    }

    public CustomResponse<AuthResponse> register(UserDto userDto) {
        Optional<User> userOptional = userRepository.findByUsername(userDto.getUsername());

        ValidateTypeFile validateTypeFile = new ValidateTypeFile();

        if (userOptional.isPresent()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "El correo que intentas registrar ya se encuentra en uso",
                    0
            );
        }

        try {
            if(!validateTypeFile.isImageFile(userDto.getProfilepic())) {
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "El archivo debe ser de tipo imagen (JPEG, JPG, PNG)",
                        0
                );
            }

            UploadImage uploadImage = new UploadImage();
            String imgUrl = uploadImage.uploadImage(userDto.getProfilepic(), userDto.getUsername(), "users");

            User userCasted = userDto.castToUser();
            userCasted.setProfilepic(imgUrl);
            Optional<Gender> gender = genderRepository.findById(userDto.getGender());

            if (!gender.isPresent()) {
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "No existe dicho genero registrado dentro del sistema",
                        0
                );
            }

            User user = User.builder()
                    .name(cryptoService.decrypt(userDto.getName()))
                    .surname(cryptoService.decrypt(userDto.getSurname()))
                    .username(cryptoService.decrypt(userDto.getUsername()))
                    .phone(cryptoService.decrypt(userDto.getPhone()))
                    .birthdate(cryptoService.decrypt(userDto.getBirthdate()))
                    .gender(gender.get())
                    .password(passwordEncoder.encode(cryptoService.decrypt(userDto.getPassword())))
                    .profilepic(imgUrl)
                    .role(Role.CUSTOMER)
                    .build();

            userRepository.save(user);

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwtService.getToken(user))
                    .build();

            String url = "http://localhost:3000/confirm/";
            String link = " <a href=\"" + url + authResponse.token + "\">Confirmar cuenta</a> ";
            String img = "https://res.cloudinary.com/sigsa/image/upload/v1681795223/sccul/logo/logo_ydzl8i.png";

            String firma = "<div style=\"display: flex; align-items: center;\">" +
                    "<img src=\"" + img + "\" alt=\"Logo Carishop\" width=\"100\" height=\"100\" style=\"margin-right: 20px;\">" +
                    "<div>" +
                    "<h3 style=\"font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60;\">CarsiShop</h3>" +
                    "<p style=\"font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60;\">carsi.shop24@gmail.com</p>" +
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
                    "Hemos recibido una solicitud para activar tu cuenta. Si no has sido tú puedes ignorar este mensaje." +
                    "</p>" +
                    "<p>" +
                    "Para activar tu cuenta, haz clic en el siguiente enlace:" + link +
                    "</p>" +
                    firma +
                    "</body>" +
                    "</html>";

            emailDetails = new EmailDetails(
                    userDto.getUsername(),
                    "NoReply. Confirma tu cuenta " + " \n\n",
                    body
            );

            emailService.sendHtmlMail(emailDetails);

            return new CustomResponse<>(
                    authResponse,
                    false,
                    200,
                    "OK",
                    1
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Error al registrar al usuario",
                    0
            );
        }

    }

    @Transactional(readOnly = true)
    public CustomResponse<AuthResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        forgotPasswordRequest.setEmail(cryptoService.decrypt(forgotPasswordRequest.getEmail()));
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

            String url = "http://localhost:3000/reset-pass/";
            String link = " <a href=\"" + url + authResponse.token + "\">Reestablecer contraseña</a> ";
            String img = "https://res.cloudinary.com/sigsa/image/upload/v1681795223/sccul/logo/logo_ydzl8i.png";

            String firma = "<div style=\"display: flex; align-items: center;\">" +
                    "<img src=\"" + img + "\" alt=\"Logo Carishop\" width=\"100\" height=\"100\" style=\"margin-right: 20px;\">" +
                    "<div>" +
                    "<h3 style=\"font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60;\">CarsiShop</h3>" +
                    "<p style=\"font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60;\">carsi.shop24@gmail.com</p>" +
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
                    "Correo enviado correctamente",
                    0
            );
        }

        return new CustomResponse<>(
                null,
                true,
                400,
                "Correo no encontrado",
                0
        );
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<Integer> resetPassword(ResetPasswordRequest resetPasswordRequest, String token) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
        try {
            resetPasswordRequest.setNewPassword(cryptoService.decrypt(resetPasswordRequest.getNewPassword()));
            resetPasswordRequest.setConfirmNewPassword(cryptoService.decrypt(resetPasswordRequest.getConfirmNewPassword()));
            String email = jwtService.getUsernameFromToken(token);

            String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getNewPassword());

            if(JwtBlackList.isTokenBlacklisted(token)){
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "Token inválido",
                        0
                );
            }

            if(!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())) {
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "Las contraseñas no coinciden",
                        0
                );
            }

            JwtBlackList.addToBlacklist(token);

            return new CustomResponse<>(
                    this.userRepository.updatePasswordById(encodedPassword, userRepository.findByUsername(email).get().getId()),
                    false,
                    200,
                    "Contraseña actualizada correctamente",
                    1
            );
        } catch (JwtException | ExecutionException e) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Token inválido",
                    0
            );
        }
    }

}
