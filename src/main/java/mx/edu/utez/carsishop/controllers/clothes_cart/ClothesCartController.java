package mx.edu.utez.carsishop.controllers.clothes_cart;
import mx.edu.utez.carsishop.controllers.card.CardDto;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCart;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCart;
import mx.edu.utez.carsishop.services.clothes_cart.ClothesCartService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/clothesCart", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin({"*"})
public class ClothesCartController {

    private final ClothesCartService clothesCartService;

    @Autowired
    public ClothesCartController(ClothesCartService clothesCartService) {
        this.clothesCartService = clothesCartService;
    }


    @GetMapping("/findByUser")
    public ResponseEntity<CustomResponse<ShoppingCart>> getClothesCartByUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return ResponseEntity.ok(clothesCartService.getClothesCartByUser(jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ClothesCart>> addClothesCart(@RequestHeader("Authorization") String authorizationHeader,@Validated({ClothesCartDto.Register.class})@RequestBody ClothesCartDto request){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return ResponseEntity.ok(clothesCartService.addClothesCart(request,jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PostMapping("/delete")
    public ResponseEntity<CustomResponse<String>> deleteClothesCart(@Validated({ClothesCartDto.Delete.class})@RequestBody ClothesCartDto request){
        return ResponseEntity.ok(clothesCartService.deleteClothesCart(request));
    }

    @PostMapping("/update")
    public ResponseEntity<CustomResponse<ClothesCart>> updateClothesCart(@Validated({ClothesCartDto.Update.class}) @RequestBody ClothesCartDto request){
        return ResponseEntity.ok(clothesCartService.updateClothesCart(request));
    }
}
