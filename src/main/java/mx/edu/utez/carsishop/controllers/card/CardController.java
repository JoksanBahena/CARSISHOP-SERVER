package mx.edu.utez.carsishop.controllers.card;

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

    private static final  String MSG_ERROR = "No se pudo acceder al token";
    private static final String BEARER = "Bearer ";
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<Card>> insertCard(@Validated({CardDto.Register.class}) @RequestBody CardDto cardDto,@RequestHeader("Authorization") String authorizationHeader) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.register(cardDto.castToCardtoInsert(), jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400, MSG_ERROR,1));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<Card>> updateCard(@Validated({CardDto.Update.class}) @RequestBody CardDto cardDto,@RequestHeader("Authorization") String authorizationHeader) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.update(cardDto.castToCardtoUpdate(), jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400, MSG_ERROR,1));
        }
    }
    @GetMapping("/get")
    public ResponseEntity<CustomResponse<List<Card>>> getCard(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.getCardsByUser(jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400, MSG_ERROR,1));
        }
    }
    @PostMapping("/getById")
    public ResponseEntity<CustomResponse<Card>> getCardByID(@RequestHeader("Authorization") String authorizationHeader,@Validated({CardDto.Delete.class}) @RequestBody CardDto cardDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwtToken = authorizationHeader.substring(7);
            return cardService.getCardById(cardDto, jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400, MSG_ERROR,1));
        }

    }

    @PostMapping("/delete")
    public ResponseEntity<CustomResponse<String>> delete(@Validated({CardDto.Delete.class}) @RequestBody CardDto cardDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
            return cardService.delete(cardDto);
    }
}
