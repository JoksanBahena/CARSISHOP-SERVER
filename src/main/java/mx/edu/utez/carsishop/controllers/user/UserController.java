package mx.edu.utez.carsishop.controllers.user;

import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.services.user.UserService;
import mx.edu.utez.carsishop.utils.CustomResponse;
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

}
