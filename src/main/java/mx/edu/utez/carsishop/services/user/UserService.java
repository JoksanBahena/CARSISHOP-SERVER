package mx.edu.utez.carsishop.services.user;

import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.gender.GenderRepository;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.*;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private GenderRepository genderRepository;
    private CryptoService cryptoService = new CryptoService();

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CustomResponse<User>> registerAdmin(UserDto userDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        userDto.setName(cryptoService.decrypt(userDto.getName()));
        userDto.setSurname(cryptoService.decrypt(userDto.getSurname()));
        userDto.setUsername(cryptoService.decrypt(userDto.getUsername()));
        userDto.setPassword(cryptoService.decrypt(userDto.getPassword()));
        userDto.setPhone(cryptoService.decrypt(userDto.getPhone()));
        userDto.setBirthdate(cryptoService.decrypt(userDto.getBirthdate()));

        if (this.userRepository.existsUserByUsername(userDto.getUsername())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de usuario ya se encuentra registrado.", 0), HttpStatus.BAD_REQUEST);
        }

        if (this.userRepository.existsUserByPhone(userDto.getPhone())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El número de teléfono ya se encuentra registrado.", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Gender> gender = genderRepository.findById(userDto.getGender());

        if (!gender.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.NOT_FOUND.value(), "El género ingresado no se encuentra registrado.", 0), HttpStatus.NOT_FOUND);
        }

        if (userDto.getPhone().length() != 10) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El número de teléfono debe contener 10 dígitos.", 0), HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());
        user.setPassword(encodedPassword);
        user.setPhone(userDto.getPhone());
        user.setGender(gender.get());
        user.setBirthdate(userDto.getBirthdate());
        user.setRole(Role.ADMIN);
        user.setStatus(false);

        user = this.userRepository.save(user);

        return new ResponseEntity<>(new CustomResponse<>(user, false, HttpStatus.CREATED.value(), "Administrador registrado correctamente.", 1), HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto) {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()
        )
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado y paginación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        if (!(paginationDto.getPaginationType().getFilter().equals("name") && paginationDto.getPaginationType().getSortBy().equals("user_name")) &&
            !(paginationDto.getPaginationType().getFilter().equals("surname") && paginationDto.getPaginationType().getSortBy().equals("user_surname")) &&
            !(paginationDto.getPaginationType().getFilter().equals("username") && paginationDto.getPaginationType().getSortBy().equals("username")) &&
            !(paginationDto.getPaginationType().getFilter().equals("role") && paginationDto.getPaginationType().getSortBy().equals("role"))
        ) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado u ordenación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El tipo de orden proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        int count = userRepository.searchCount();

        List<User> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "name":
                list = userRepository.findAllByNamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "surname":
                list = userRepository.findAllBySurnamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "username":
                list = userRepository.findAllByUsernamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "role":
                list = userRepository.findAllByRolePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            default:
                return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El filtro proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente.", count), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
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
