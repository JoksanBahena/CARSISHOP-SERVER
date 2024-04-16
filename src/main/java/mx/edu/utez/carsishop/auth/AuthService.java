package mx.edu.utez.carsishop.auth;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.jwt.JwtBlackList;
import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.email.EmailDetails;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.gender.GenderRepository;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.services.email.EmailService;
import mx.edu.utez.carsishop.utils.*;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AuthService {
    private final CryptoService cryptoService = new CryptoService();
    private final EmailTemplate emailTemplate = new EmailTemplate();
    private final GenderRepository genderRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    EmailDetails emailDetails;

    @Autowired
    public AuthService(GenderRepository genderRepository, UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.genderRepository = genderRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public ResponseEntity<Object> login(LoginRequest request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException  {
        request.setEmail(cryptoService.decrypt(request.getEmail()));
        request.setPassword(cryptoService.decrypt(request.getPassword()));
        Optional<User> userOpt = userRepository.findByUsername(request.getEmail());
        if(userOpt.isEmpty()){
            return new ResponseEntity<>(new CustomResponse<>(
                    null,
                    true,
                    404,
                    "Correo incorrecto o usuario inexistente", 0),
                    HttpStatus.NOT_FOUND);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        }catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Contraseña incorrecta", 0),
                    HttpStatus.BAD_REQUEST);
        }
        User user=userOpt.get();
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
            String imgUrl = uploadImage.uploadImage(userDto.getProfilepic(), cryptoService.decrypt(userDto.getUsername()), "users");

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
            String link = getLink(authResponse.token, url, "Confirmar cuenta");

            String body =
                    "<p>" +
                    "Hemos recibido una solicitud para activar tu cuenta. Si no has sido tú puedes ignorar este mensaje." +
                    "</p>" +
                    "<p>" +
                    "Para activar tu cuenta, haz clic en el siguiente enlace:" + link +
                    "</p>";

            String template = emailTemplate.getTemplate(body);

            emailDetails = new EmailDetails(
                    cryptoService.decrypt(userDto.getUsername()),
                    "NoReply. Confirma tu cuenta \n\n",
                    template
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
            String link = getLink(authResponse.token, url, "Reestablecer contraseña");

            String body =
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
                    "</p>";

            String template = emailTemplate.getTemplate(body);

            emailDetails = new EmailDetails(
                    forgotPasswordRequest.getEmail(),
                    "NoReply. Reestablece tu contraseña \n\n",
                    template
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
                        "Token inválido.",
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
    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<Integer> confirm(String token) {
        try {
            if (!userRepository.existsUserByUsername(jwtService.getUsernameFromToken(token))) {
                return new CustomResponse<>(
                        null,
                        true,
                        404,
                        "La cuenta no existe",
                        0
                );
            }

            if (userRepository.getStatusByEmail(jwtService.getUsernameFromToken(token))) {
                return new CustomResponse<>(
                        null,
                        true,
                        400,
                        "La cuenta ya ha sido confirmada",
                        0
                );
            }

            if (JwtBlackList.isTokenBlacklisted(token)) {
                return new CustomResponse<>(
                        null,
                        true,
                        403,
                        "Token inválido. Por favor, solicita un nuevo enlace de confirmación",
                        0
                );
            }
            JwtBlackList.addToBlacklist(token);

            return new CustomResponse<>(
                    this.userRepository.updateStatusByEmail(jwtService.getUsernameFromToken(token)),
                    false,
                    200,
                    "Usuario confirmado correctamente",
                    1
            );

        } catch (Exception e) {
            return new CustomResponse<>(
                    null,
                    true,
                    500,
                    "Error al confirmar usuario",
                    0
            );
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Object> resendconfirm(ResendConfirmRequest resendConfirmRequest)
    {
        try {
            if (!userRepository.existsUserByUsername(resendConfirmRequest.getEmail())) {
                return new ResponseEntity<>(new CustomResponse<>(
                        null,
                        true,
                        404,
                        "La cuenta no existe", 0),
                        HttpStatus.NOT_FOUND);
            }

            if (userRepository.getStatusByEmail(resendConfirmRequest.getEmail())) {
                return new ResponseEntity<>(new CustomResponse<>(
                        null,
                        true,
                        400,
                        "La cuenta ya ha sido confirmada", 0),
                        HttpStatus.BAD_REQUEST);
            }

            Optional<User> userOptional = this.userRepository.findByUsername(resendConfirmRequest.getEmail());

            if (userOptional.isPresent()) {
                User user = User.builder()
                        .username(userOptional.get().getUsername())
                        .name(userOptional.get().getName())
                        .surname(userOptional.get().getSurname())
                        .role(userOptional.get().getRole())
                        .build();

                AuthResponse authResponse = AuthResponse.builder()
                        .token(jwtService.getToken(user))
                        .build();

                String url = "http://localhost:3000/confirm/";
                String link = getLink(authResponse.token, url, "Confirmar cuenta");

                String body =
                        "<p>" +
                        "Hemos recibido una solicitud para activar tu cuenta. Si no has sido tú, puedes ignorar este mensaje." +
                        "</p>" +
                        "<p>" +
                        "Para activar tu cuenta, haz clic en el siguiente enlace:" + link +
                        "</p>";

                String template = emailTemplate.getTemplate(body);

                emailDetails = new EmailDetails(
                        resendConfirmRequest.getEmail(),
                        "NoReply. Confirma tu cuenta \n\n",
                        template
                );
                emailService.sendHtmlMail(emailDetails);

                return new ResponseEntity<>(new CustomResponse<>(
                        null,
                        false,
                        200,
                        "Correo enviado correctamente", 0),
                        HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new CustomResponse<>(
                        null,
                        true,
                        400,
                        "Error al reenviar correo de confirmación", 0),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(
                    null,
                    true,
                    500,
                    "Error al reenviar correo de confirmación", 0),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getLink(String token, String url, String message) {
        return " <a href=\"" + url + token + "\">"+message+"</a> ";
    }
}
