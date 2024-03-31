package mx.edu.utez.carsishop.utils;

import mx.edu.utez.carsishop.Auth.AuthService;
import mx.edu.utez.carsishop.Auth.RegisterRequest;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialScript implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public InitialScript(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args)  {

        Optional<User> user = userRepository.findByUsername("admin@gmail.com");
        if (!user.isPresent()) {
            RegisterRequest registerRequest = new RegisterRequest();

            registerRequest.setEmail("admin@gmail.com");
            registerRequest.setPassword("admin");
            registerRequest.setName("admin");
            registerRequest.setSurname("admin");
            registerRequest.setPhone("1234567890");

            authService.register(registerRequest);
        }


    }

}
