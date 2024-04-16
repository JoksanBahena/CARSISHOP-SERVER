package mx.edu.utez.carsishop.controllers.clothes;

import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.services.clothes.ClothesService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import jakarta.validation.Valid;
import mx.edu.utez.carsishop.models.stock.Stock;
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
    private final ClothesService clothesService;

    @Autowired
    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }


    @PostMapping(path = "/find-all")
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) {
        return clothesService.findAll(paginationDto);
    }

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
    @GetMapping("/getOne/{id:[0-9]+}")
    public ResponseEntity<CustomResponse<Clothes>> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(clothesService.getOne(id), HttpStatus.OK);
    }

    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<CustomResponse<List<Clothes>>> getByCategory(@PathVariable String category) {
        return new ResponseEntity<>(clothesService.findByCategory(category), HttpStatus.OK);
    }

    @GetMapping("/getByCategoryAndSubcategory/{category}/{subcategory}")
    public ResponseEntity<CustomResponse<List<Clothes>>> getByCategoryAndSubcategory(@PathVariable String category, @PathVariable String subcategory) {
        return new ResponseEntity<>(clothesService.findByCategoryAndSubcategory(category, subcategory), HttpStatus.OK);
    }

    @GetMapping("/getAllClothesOrderedByPrice")
    public ResponseEntity<CustomResponse<List<Clothes>>> getAllClothesOrderedByPrice() {
        return new ResponseEntity<>(clothesService.findAllClothesOrderedByPrice(), HttpStatus.OK);
    }

    @PostMapping("/isAccepted")
    public ResponseEntity<Object> changeIsAccepted(@Validated({ClothesDto.ChangeIsAccepted.class}) @RequestBody ClothesDto clothesDto) {
        return clothesService.changeIsAccepted(clothesDto);
    }


}
