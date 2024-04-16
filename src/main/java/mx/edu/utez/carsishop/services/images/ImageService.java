package mx.edu.utez.carsishop.services.images;

import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.models.images.Image;
import mx.edu.utez.carsishop.models.images.ImageRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.UploadImage;
import mx.edu.utez.carsishop.utils.ValidateTypeFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ClothesRepository clothesRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, ClothesRepository clothesRepository) {
        this.imageRepository = imageRepository;
        this.clothesRepository = clothesRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<List<Image>> addImages(ClothesImagesDto clothesImagesDto) throws IOException {
        Optional<Clothes> clothesOptional = clothesRepository.findById(clothesImagesDto.getClothesId());

        ValidateTypeFile validateTypeFile = new ValidateTypeFile();

        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found", 0);
        }
        if(!clothesImagesDto.isValid()){
            return new CustomResponse<>(null, true, 400, "Invalid data", 0);

        }
        if(!validateTypeFile.isImagesFiles(clothesImagesDto.getImages())){
            return new CustomResponse<>(null, true, 400, "El archivo debe ser de tipo imagen (JPEG, JPG, PNG)", 0);
        }
        List<Image> imagesList = new ArrayList<>();
        //se suben las imagenes a cloudinary
        UploadImage uploadImage = new UploadImage();
        List<ClothesImagesDto.ImagesAndIndex> images = clothesImagesDto.getImages();
        for (int i = 0; i < images.size() && imagesList.size() < 5; i++) {
            if (images.get(i).getIndex() > 4 || images.get(i).getIndex() < 0) {
                continue;
            }

            Image image = new Image();
            image.setUrl(uploadImage.uploadImage(images.get(i).getImage(), clothesOptional.get().getName() + "-" + images.get(i).getIndex(), "clothes"));
            image.setClothes(clothesOptional.get());

            Optional<Image> imageOptional = imageRepository.findByUrl(image.getUrl());
            imagesList.add(imageOptional.orElse(image));
        }

        return  new CustomResponse<>(imageRepository.saveAll(imagesList), false, 200, "Images uploaded", imagesList.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<String> deleteImage(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isPresent()){
            imageRepository.deleteById(id);
            return new CustomResponse<>("Image has being deleted", false,200,"OK", 0);
        }
        return new CustomResponse<>("Image not found", true,400,"NOT_FOUND", 0);
    }
}
