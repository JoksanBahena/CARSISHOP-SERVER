package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.Valid;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.stock.Stock;
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
import java.util.List;

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

    @PutMapping(path = "/update")
    public ResponseEntity<CustomResponse<Clothes>> updateClothesInfo(@Valid @RequestBody ClothesUpdateDto clothes) {
        return ResponseEntity.ok(clothesService.updateClothesInformation(clothes));
    }

    @PutMapping(path = "/update/stock")
    public ResponseEntity<CustomResponse<List<Stock>>> updateClothesStock(@Valid @RequestBody ClothesStockUpdateDto clothes) {
        return ResponseEntity.ok(clothesService.updateStock(clothes));
    }

    @PutMapping(path = "/disable/{id}")
    public ResponseEntity<CustomResponse<Clothes>> disableCloth(@PathVariable long id) {
        return ResponseEntity.ok(clothesService.disableCloth(id));
    }
}
