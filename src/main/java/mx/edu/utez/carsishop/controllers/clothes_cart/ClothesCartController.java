package mx.edu.utez.carsishop.controllers.clothes_cart;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCart;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCart;
import mx.edu.utez.carsishop.services.clothes_cart.ClothesCartService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clothesCart")
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
    public ResponseEntity<CustomResponse<ClothesCart>> addClothesCart(@RequestHeader("Authorization") String authorizationHeader,@RequestBody ClothesCartDto request){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return ResponseEntity.ok(clothesCartService.addClothesCart(request,jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> deleteClothesCart(@RequestBody Long id){
        return ResponseEntity.ok(clothesCartService.deleteClothesCart(id));
    }

    @PutMapping("/update/{id}/{amount}")
    public ResponseEntity<CustomResponse<ClothesCart>> updateClothesCart(@PathVariable Long id, @PathVariable int amount){
        return ResponseEntity.ok(clothesCartService.updateClothesCart(id, amount));
    }
}
