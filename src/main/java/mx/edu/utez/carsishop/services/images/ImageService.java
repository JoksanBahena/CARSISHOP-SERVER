package mx.edu.utez.carsishop.services.images;

import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.models.images.Image;
import mx.edu.utez.carsishop.models.images.ImageRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.UploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ClothesRepository clothesRepository;

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<List<Image>> addImages(ClothesImagesDto clothesImagesDto) throws IOException {
        Optional<Clothes> clothesOptional = clothesRepository.findById(clothesImagesDto.getClothesId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found");
        }
        if(!clothesImagesDto.isValid()){
            return new CustomResponse<>(null, true, 400, "Invalid data");

        }
        List<Image> imagesList = new ArrayList<>();
        //se suben las imagenes a cloudinary
        UploadImage uploadImage = new UploadImage();
        List<ClothesImagesDto.ImagesAndIndex> images = clothesImagesDto.getImages();
        for (int i = 0; i < images.size(); i++){
            if(imagesList.size()>=5){
                break;
            }
            Image image = new Image();
            if(images.get(i).getIndex()>4 || images.get(i).getIndex()<0){
                continue;
            }
            image.setUrl(uploadImage.uploadImage(images.get(i).getImage(), clothesOptional.get().getName()+"-"+images.get(i).getIndex()));
            image.setClothes(clothesOptional.get());
            Optional<Image> imageOptional = imageRepository.findByUrl(image.getUrl());
            if(imageOptional.isEmpty()){
                imagesList.add(image);
            }else {
                imagesList.add(imageOptional.get());
            }
        }
        return  new CustomResponse<>(imageRepository.saveAll(imagesList), false, 200, "Images uploaded");
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<String> deleteImage(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isPresent()){
            imageRepository.deleteById(id);
            return new CustomResponse<>("Image has being deleted", false,200,"OK");
        }
        return new CustomResponse<>("Image not found", true,400,"NOT_FOUND");
    }
}
