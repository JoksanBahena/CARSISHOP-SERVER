package mx.edu.utez.carsishop.services.card;

import mx.edu.utez.carsishop.controllers.card.CardDto;
import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.card.CardRepository;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final CryptoService cryptoService=new CryptoService();
    private final OrderRepository orderRepository;

    private static final String USER_NOT_FOUND="Usuario no encontrado";
    private static final String CARD_NOT_FOUND="Tarjeta no encontrada";


    public CardService(CardRepository cardRepository, JwtService jwtService, UserRepository userRepository, OrderRepository orderRepository) {
        this.cardRepository = cardRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<CustomResponse<Card>> register(Card card,String token) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String email=jwtService.getUsernameFromToken(token);
        Optional<User> user=userRepository.findByUsername(email);
        if(user.isEmpty()){
            return ResponseEntity.status(401).body(new CustomResponse<>(
                    null,
                    true,
                    401,
                    USER_NOT_FOUND,
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
                    CARD_NOT_FOUND,
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
                    USER_NOT_FOUND,
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
    public ResponseEntity<CustomResponse<String>> delete(CardDto cardDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Card> cardOptional = cardRepository.findById(Long.parseLong(cryptoService.decrypt(cardDto.getId())));
        if(cardOptional.isEmpty()){
            return ResponseEntity.status(404).body(new CustomResponse<>(
                    null,
                    true,
                    404,
                    CARD_NOT_FOUND,
                    0
            ));
        }
        Optional<Order> order=orderRepository.findByCard(cardOptional.get());
        if (order.isPresent()){
            cardOptional.get().setEnable(false);
            cardRepository.save(cardOptional.get());
            return ResponseEntity.status(400).body(new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Tarjeta inabilitada por que tiene ordenes asociadas",
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
                    USER_NOT_FOUND,
                    0
            ));
        }
        List<Card> cards=cardRepository.findByUserAndEnable(user.get(),true);
        return ResponseEntity.ok(new CustomResponse<>(
                cards,
                false,
                200,
                "OK",
                1
        ));
    }

    public ResponseEntity<CustomResponse<Card>> getCardById(CardDto cardDto, String jwtToken) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<User> user=userRepository.findByUsername(jwtService.getUsernameFromToken(jwtToken));
        if(user.isEmpty()){
            return ResponseEntity.status(401).body(new CustomResponse<>(
                    null,
                    true,
                    401,
                    USER_NOT_FOUND,
                    0
            ));
        }
        Optional<Card> cardOptional = cardRepository.findByIdAndUser(Long.parseLong(cryptoService.decrypt(cardDto.getId())),user.get());
        if(cardOptional.isEmpty()){
            return ResponseEntity.status(404).body(new CustomResponse<>(
                    null,
                    true,
                    404,
                    CARD_NOT_FOUND,
                    0
            ));
        }

        return ResponseEntity.ok(new CustomResponse<>(
                cardOptional.get(),
                false,
                200,
                "OK",
                1
        ));
    }
}
