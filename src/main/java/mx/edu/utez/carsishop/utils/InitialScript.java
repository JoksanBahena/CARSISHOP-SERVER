package mx.edu.utez.carsishop.utils;

import mx.edu.utez.carsishop.Auth.AuthService;
import mx.edu.utez.carsishop.Auth.RegisterRequest;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialScript implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitialScript(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args)  {

        Optional<User> userExist = userRepository.findByUsername("admin@gmail.com");
        if (!userExist.isPresent()) {
            User user = User.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode( "admin"))
                    .name("admin")
                    .surname("admin")
                    .phone("1234567890")
                    .birthdate("2021-01-01")
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(user);
        }


    }

}
