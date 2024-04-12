package mx.edu.utez.carsishop.services.user;

import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.gender.GenderRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.UploadImage;
import mx.edu.utez.carsishop.utils.ValidateTypeFile;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<CustomResponse<User>> getUserInfo(UserDto dto) {
        Optional<User> user = userRepository.findByUsername(dto.getUsername());

        if (!user.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", 0), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new CustomResponse<>(user.get(), false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente.", 1), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CustomResponse<User>> updateUserInfo(UserDto userDto) {
        Optional<User> user = userRepository.findByUsername(userDto.getUsername());

        if(!user.isPresent()) {
            return new ResponseEntity<>(
                    new CustomResponse<>(null, true, HttpStatus.NOT_FOUND.value(), "No se encontró al usuario", 0), HttpStatus.NOT_FOUND
            );
        }

        if(this.userRepository.existsUserByPhoneAndIdNot(userDto.getPhone(), user.get().getId())) {
            return new ResponseEntity<>(
                    new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El número de teléfono ya se encuentra registrado", 0), HttpStatus.BAD_REQUEST
            );
        }

        User userToUpdate = user.get();
        userToUpdate.setName(userDto.getName());
        userToUpdate.setSurname(userDto.getSurname());
        userToUpdate.setPhone(userDto.getPhone());
        userToUpdate.setGender(this.genderRepository.findById(userDto.getGender()).get());

        this.userRepository.save(userToUpdate);

        return new ResponseEntity<>(
                new CustomResponse<>(userToUpdate, false, HttpStatus.OK.value(), "Se acutalizó la información", 1), HttpStatus.OK
        );
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CustomResponse<User>> updateProfilePic(UserDto userDto) {
        Optional<User> user = this.userRepository.findByUsername(userDto.getUsername());

        if(!user.isPresent()) {
            return new ResponseEntity<>(
                    new CustomResponse<>(null, true, HttpStatus.NOT_FOUND.value(), "No se encontró al usuario", 0), HttpStatus.NOT_FOUND
            );
        }

        ValidateTypeFile validateTypeFile = new ValidateTypeFile();

        try {
            if (!validateTypeFile.isImageFile(userDto.getProfilepic())) {
                return new ResponseEntity<>(
                        new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El archivo debe ser de tipo imagen (JPEG, JPG, PNG)", 0), HttpStatus.BAD_REQUEST
                );
            }

            UploadImage uploadImage = new UploadImage();
            String imgUrl = uploadImage.uploadImage(userDto.getProfilepic(), userDto.getUsername(), "users");

            User userToUpdate = user.get();
            userToUpdate.setProfilepic(imgUrl);

            this.userRepository.save(userToUpdate);

            return new ResponseEntity<>(
                    new CustomResponse<>(userToUpdate, false, HttpStatus.OK.value(), "Foto de perfil actualizada correctamente", 1), HttpStatus.OK
            );
        }catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Error al actualizar la foto de perfil", 0), HttpStatus.BAD_REQUEST
            );
        }
    }
}
