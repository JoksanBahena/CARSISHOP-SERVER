package mx.edu.utez.carsishop.services.card;

import mx.edu.utez.carsishop.Jwt.JwtService;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.card.CardRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.text.html.Option;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    private CryptoService cryptoService=new CryptoService();
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<CustomResponse<Card>> register(Card card,String token) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String email=jwtService.getUsernameFromToken(token);
        Optional<User> user=userRepository.findByUsername(email);
        if(user.isEmpty()){
            return ResponseEntity.status(401).body(new CustomResponse<>(
                    null,
                    true,
                    401,
                    "Usuario no encontrado",
                    0
            ));
        }
        card.setUser(user.get());
        String lastFour=cryptoService.decrypt(card.getNumber());
        lastFour=lastFour.substring(lastFour.length()-4);
        card.setLastFour(lastFour);
        return ResponseEntity.ok(new CustomResponse<>(
                cardRepository.save(card),
                false,
                200,
                "OK",
                1
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<CustomResponse<Card>> update(Card card,String token) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Card> cardOptional=cardRepository.findById(card.getId());
        if(cardOptional.isEmpty()){
            return ResponseEntity.status(404).body(new CustomResponse<>(
                    null,
                    true,
                    404,
                    "Tarjeta no encontrada",
                    0
            ));
        }
        String email=jwtService.getUsernameFromToken(token);
        Optional<User> user=userRepository.findByUsername(email);
        if(user.isEmpty()){
            return ResponseEntity.status(401).body(new CustomResponse<>(
                    null,
                    true,
                    401,
                    "Usuario no encontrado",
                    0
            ));
        }
        card.setUser(user.get());
        String lastFour=cryptoService.decrypt(card.getNumber());
        lastFour=lastFour.substring(lastFour.length()-4);
        card.setLastFour(lastFour);
        return ResponseEntity.ok(new CustomResponse<>(
                cardRepository.save(card),
                false,
                200,
                "OK",
                1
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<CustomResponse<String>> delete(Long id) {
        Optional<Card> cardOptional=cardRepository.findById(id);
        if(cardOptional.isEmpty()){
            return ResponseEntity.status(404).body(new CustomResponse<>(
                    null,
                    true,
                    404,
                    "Tarjeta no encontrada",
                    0
            ));
        }
        cardRepository.delete(cardOptional.get());
        return ResponseEntity.ok(new CustomResponse<>(
                "Tarjeta eliminada Correctamente",
                false,
                200,
                "OK",
                1
        ));

    }
    @Transactional(readOnly = true)
    public ResponseEntity<CustomResponse<List<Card>>> getCardsByUser(String token) {
        String username=jwtService.getUsernameFromToken(token);
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isEmpty()){
            return ResponseEntity.status(401).body(new CustomResponse<>(
                    null,
                    true,
                    401,
                    "Usuario no encontrado",
                    0
            ));
        }
        List<Card> cards=cardRepository.findByUser(user.get());
        return ResponseEntity.ok(new CustomResponse<>(
                cards,
                false,
                200,
                "OK",
                1
        ));
    }

}
