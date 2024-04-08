package mx.edu.utez.carsishop.utils;

import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

public class ValidateTypeFile {
    public boolean isImageFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "image/jpeg") ||
                Objects.equals(file.getContentType(), "image/png") ||
                Objects.equals(file.getContentType(), "image/jpg");
    }

    public boolean isImagesFiles(List<ClothesImagesDto.ImagesAndIndex> clothesImagesDtos) {
        for(ClothesImagesDto.ImagesAndIndex clothe : clothesImagesDtos) {
            if(Objects.equals(clothe.getImage().getContentType(), "image/jpeg") ||
                    Objects.equals(clothe.getImage().getContentType(), "image/png") ||
                    Objects.equals(clothe.getImage().getContentType(), "image/jpg")){
                return true;
            }
        }

        return false;
    }
}
