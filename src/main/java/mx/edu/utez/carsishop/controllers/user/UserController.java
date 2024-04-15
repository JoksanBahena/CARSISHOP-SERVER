package mx.edu.utez.carsishop.controllers.user;

import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.services.user.UserService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin({"*"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/find-all")
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) throws SQLException {
        return userService.findAll(paginationDto);
    }

    @PostMapping("/info")
    public ResponseEntity<CustomResponse<User>> getInfo(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return userService.getUserInfo(jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<CustomResponse<User>> updateInfo(@RequestHeader("Authorization") String authorizationHeader,@Validated({UserDto.Update.class}) @RequestBody UserDto userDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return userService.updateUserInfo(userDto,jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PutMapping("/updateProfilePic")
    public ResponseEntity<CustomResponse<User>> updateProfilePic(@RequestHeader("Authorization") String authorizationHeader,@Validated({UserDto.UpdateProfilePic.class}) @ModelAttribute UserDto userDto) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return userService.updateProfilePic(userDto,jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<CustomResponse<User>> registerAdmin(@Validated({UserDto.RegisterAdmin.class}) @RequestBody UserDto userDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return userService.registerAdmin(userDto);
    }
}
