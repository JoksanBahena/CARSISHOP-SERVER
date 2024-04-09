package mx.edu.utez.carsishop.services.user;

import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<CustomResponse<User>> getUserInfo(UserDto dto) {
        Optional<User> user = userRepository.findById(dto.getId());

        if (!user.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", 0), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new CustomResponse<>(user.get(), false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente.", 1), HttpStatus.OK);


    }
}
