package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClothesImagesDto {
    @NotNull(message = "El producto es obligatorio")
    private Long clothesId;

    @NotNull(message = "Las imágenes son obligatorias")
    private List<MultipartFile> images;

    public boolean isValid(){
        return clothesId != null && images != null && !images.isEmpty()  && images.size()<=5;
    }
}
