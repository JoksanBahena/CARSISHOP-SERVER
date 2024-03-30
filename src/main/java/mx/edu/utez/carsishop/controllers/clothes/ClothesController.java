package mx.edu.utez.carsishop.controllers.clothes;

import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.services.clothes.ClothesService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clothes")
@CrossOrigin({"*"})
public class ClothesController {
    @Autowired
    private ClothesService clothesService;

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
}
