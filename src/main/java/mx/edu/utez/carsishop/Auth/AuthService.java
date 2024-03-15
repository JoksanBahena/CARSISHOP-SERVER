package mx.edu.utez.carsishop.Auth;


import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.Jwt.JwtService;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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
        User user = User.builder()
            .username(request.getEmail())
            .password(passwordEncoder.encode( request.getPassword()))
            .name(request.getName())
            .surname(request.getSurname())
            .phone(request.getPhone())
            .role(Role.CUSTOMER)
            .build();

        userRepository.save(user);

        AuthResponse authResponse= AuthResponse.builder()
            .token(jwtService.getToken(user))
            .build();
        return new CustomResponse<>(
                authResponse,
                false,
                200,
                "OK"
        );
        
    }

}
