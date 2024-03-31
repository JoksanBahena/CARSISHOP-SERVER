package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.Valid;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.services.clothes.ClothesService;
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
@RequestMapping(path = "/api/clothes")
@CrossOrigin(origins = {"*"})
public class ClothesController {
    @Autowired
    private ClothesService clothesService;

    @PostMapping(path = "/create")
    public ResponseEntity<CustomResponse<Clothes>> createClothes(@Valid @RequestBody ClothesDto clothes) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(clothesService.createClothes(clothes));
    }
}
