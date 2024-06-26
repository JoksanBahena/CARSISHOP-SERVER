package mx.edu.utez.carsishop.controllers.images;

import jakarta.validation.Valid;
import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import mx.edu.utez.carsishop.models.images.Image;
import mx.edu.utez.carsishop.services.images.ImageService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/images", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"*"})
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse<String>> deleteImage(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.deleteImage(id));
    }

    @PutMapping("/addImages")
    public ResponseEntity<CustomResponse<List<Image>>> addImages(@Valid @ModelAttribute ClothesImagesDto clothesImagesDto) throws IOException {
        return ResponseEntity.ok(imageService.addImages(clothesImagesDto));
    }


}
