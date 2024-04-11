package mx.edu.utez.carsishop.controllers.user;

import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.services.user.UserService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin({"*"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/info")
    public ResponseEntity<CustomResponse<User>> getInfo(@Validated({UserDto.GetInfo.class}) @RequestBody UserDto userDto) {
        return userService.getUserInfo(userDto);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<CustomResponse<User>> updateInfo(@Validated({UserDto.Update.class}) @RequestBody UserDto userDto) {
        return userService.updateUserInfo(userDto);
    }

    @GetMapping("/getBearer")
    public ResponseEntity<CustomResponse<String>> updateInfo(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extraemos el token JWT eliminando el prefijo "Bearer "
            String jwtToken = authorizationHeader.substring(7);

            // Ahora puedes utilizar el token JWT de la forma que necesites
            // Por ejemplo, puedes validar el token, extraer información del usuario, etc.

            return ResponseEntity.ok(new CustomResponse<>(jwtToken,false,200,"hola",1));
        } else {
            // Si no se proporciona el token en el encabezado de autorización, puedes manejar el caso aquí
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"ctm",1));

        }
    }
    @PutMapping("/updateProfilePic")
    public ResponseEntity<CustomResponse<User>> updateProfilePic(@Validated({UserDto.UpdateProfilePic.class}) @ModelAttribute UserDto userDto) {
        return userService.updateProfilePic(userDto);
    }
}
