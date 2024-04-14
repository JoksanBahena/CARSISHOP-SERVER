package mx.edu.utez.carsishop.Auth;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping(value = "login")
    public ResponseEntity<CustomResponse<AuthResponse>> login(@Validated({LoginRequest.Login.class}) @RequestBody LoginRequest request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<CustomResponse<AuthResponse>> register(@Validated({UserDto.Register.class}) @ModelAttribute UserDto userDto)
    {
        return ResponseEntity.ok(authService.register(userDto));
    }

    @GetMapping(value = "encode/{wordToEncode}")
    public String  register(@PathVariable String wordToEncode) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        return new CryptoService().encrypt(wordToEncode);
    }

    @PostMapping(value = "forgotPassword")
    public ResponseEntity<CustomResponse<AuthResponse>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequest));
    }

    @PostMapping(value = "resetPassword/{token}")
    public ResponseEntity<CustomResponse<Integer>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, @PathVariable String token) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest, token));
    }

}
