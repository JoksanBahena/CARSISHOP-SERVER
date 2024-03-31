package mx.edu.utez.carsishop.services.clothes;

import mx.edu.utez.carsishop.controllers.clothes.ClothesDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesStockUpdateDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesUpdateDto;
import mx.edu.utez.carsishop.models.category.CategoryRepository;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.models.images.Image;
import mx.edu.utez.carsishop.models.images.ImageRepository;
import mx.edu.utez.carsishop.models.stock.Stock;
import mx.edu.utez.carsishop.models.stock.StockRepository;
import mx.edu.utez.carsishop.models.subcategory.SubcaregoryRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.UploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ClothesService {
    @Autowired
    private ClothesRepository clothesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcaregoryRepository subcaregoryRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ImageRepository imageRepository;

    private CryptoService cryptoService = new CryptoService();

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> createClothes(ClothesDto clothes) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Clothes newClothes = clothes.castToClothes();
        String emaildecoded=cryptoService.decrypt(clothes.getSellerEmail());
        Optional<User> user = userRepository.findByUsername(emaildecoded);
        if(user.isPresent()){
            newClothes.setSeller(user.get().getSeller());
        }else {
            return new CustomResponse<>(null, true, 400, "User not found");
        }
        Clothes clothesSaved = clothesRepository.save(newClothes);
        for (Stock stock:clothes.getStock()) {
            stock.setClothes(clothesSaved);
        }
        stockRepository.saveAll(clothes.getStock());
        return new CustomResponse<>(clothesSaved, false, 200, "Clothes created");
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> updateClothesInformation(ClothesUpdateDto clothes) {
        Optional <Clothes> clothesOptional = clothesRepository.findById(clothes.getId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found");
        }else {
            Clothes clothesToUpdate = clothesOptional.get();
            clothesToUpdate.setName(clothes.getName());
            clothesToUpdate.setDescription(clothes.getDescription());
            clothesToUpdate.setPrice(clothes.getPrice());
            clothesToUpdate.setCategory(clothes.getCategory());
            clothesToUpdate.setSubcategory(clothes.getSubcategory());
            clothesToUpdate.setStock(clothesToUpdate.getStock());
            return new CustomResponse<>(clothesRepository.save(clothesToUpdate), false, 200, "Clothes updated");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<List<Stock>> updateStock(ClothesStockUpdateDto clothesStockUpdateDto){
        Optional<Clothes> clothesOptional = clothesRepository.findById(clothesStockUpdateDto.getStock().get(0).getId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found");
        }else {
            return new CustomResponse<>(stockRepository.saveAll(clothesStockUpdateDto.getStock()), false, 200, "Stock updated");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> disableCloth(Long id){
        Optional<Clothes> clothesOptional = clothesRepository.findById(id);
        if(clothesOptional.isEmpty()) {
            return new CustomResponse<>(null, true, 400, "Clothes not found");
        }
        clothesOptional.get().setEnabled(false);
        return new CustomResponse<>(clothesRepository.save(clothesOptional.get()), false, 200, "Clothes found");
    }



    public CustomResponse<Clothes> getOne(Long id) {
        if(!this.clothesRepository.existsById(id)) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontro alguna prenda"
            );
        }

        return new CustomResponse<Clothes>(
                this.clothesRepository.findById(id).get(),
                false,
                200,
                "Ok"
        );
    }

    public CustomResponse<List<Clothes>> findByCategory(String category) {
        List<Clothes> clothes = this.clothesRepository.findClothesByCategory(category);

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontró ropa en esa categoria"
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok"
        );
    }

    public CustomResponse<List<Clothes>> findByCategoryAndSubcategory(String category, String subcategory) {
        List<Clothes> clothes = this.clothesRepository.findClothesByCategoryAndSubcategory(category, subcategory);

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontró ropa en esa categoria o subcategoria"
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok"
        );
    }

    public CustomResponse<List<Clothes>> findAllClothesOrderedByPrice() {
        List<Clothes> clothes = this.clothesRepository.findAllClothesOrderedByPrice();

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No hay datos"
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok"
        );
    }

    public CustomResponse<List<Image>> addImages(ClothesImagesDto clothesImagesDto) throws IOException {
        Optional<Clothes> clothesOptional = clothesRepository.findById(clothesImagesDto.getClothesId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found");
        }
        if(!clothesImagesDto.isValid()){
            return new CustomResponse<>(null, true, 400, "Invalid data");

        }
        List<Image> imagesList = imageRepository.findByClothes(clothesOptional.get());
        if(imagesList.size()>=5){
            return new CustomResponse<>(null, true, 400, "Max images reached");
        }
        //se suben las imagenes a cloudinary
        UploadImage uploadImage = new UploadImage();
        List<ClothesImagesDto.ImagesAndIndex> images = clothesImagesDto.getImages();
        for (int i = 0; i < images.size(); i++){
            if(imagesList.size()>=5){
                break;
            }
            Image image = new Image();
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
}
