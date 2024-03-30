package mx.edu.utez.carsishop.controllers.clothesCart;

import mx.edu.utez.carsishop.models.clothesCart.ClothesCart;
import mx.edu.utez.carsishop.models.shoppingCart.ShoppingCart;
import mx.edu.utez.carsishop.services.clothesCart.ClothesCartService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/clothesCart")
@CrossOrigin({"*"})
public class ClothesCartController {

    @Autowired
    private ClothesCartService clothesCartService;

    @GetMapping("/findByUser")
    public ResponseEntity<CustomResponse<ShoppingCart>> getClothesCartByUser(@RequestBody String email) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        return ResponseEntity.ok(clothesCartService.getClothesCartByUser(email));
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ClothesCart>> addClothesCart(@RequestBody ClothesCartDto request){
        return ResponseEntity.ok(clothesCartService.addClothesCart(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> deleteClothesCart(@RequestBody Long id){
        return ResponseEntity.ok(clothesCartService.deleteClothesCart(id));
    }

    @PutMapping("/update/{id}/{amont}")
    public ResponseEntity<CustomResponse<ClothesCart>> updateClothesCart(@PathVariable Long id, @PathVariable int amount){
        return ResponseEntity.ok(clothesCartService.updateClothesCart(id, amount));
    }
}
