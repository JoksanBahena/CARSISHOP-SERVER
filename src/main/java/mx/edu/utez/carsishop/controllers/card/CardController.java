package mx.edu.utez.carsishop.controllers.card;

import jakarta.persistence.Entity;
import mx.edu.utez.carsishop.controllers.user.UserDto;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.services.card.CardService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/card")
@CrossOrigin(origins = {"*"})
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<Card>> insertCard(@Validated({CardDto.Register.class}) @RequestBody CardDto cardDto,@RequestHeader("Authorization") String authorizationHeader) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.register(cardDto.castToCardtoInsert(), jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"No se pudo acceder al token",1));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<Card>> updateCard(@Validated({CardDto.Update.class}) @RequestBody CardDto cardDto,@RequestHeader("Authorization") String authorizationHeader) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.update(cardDto.castToCardtoUpdate(), jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"No se pudo acceder al token",1));
        }
    }
    @GetMapping("/get")
    public ResponseEntity<CustomResponse<List<Card>>> getCard(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.getCardsByUser(jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"No se pudo acceder al token",1));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> delete(@RequestBody CardDto cardDto) {
            return cardService.delete(cardDto.getId());
    }
}
