package mx.edu.utez.carsishop.services.clothes;

import mx.edu.utez.carsishop.controllers.clothes.ClothesDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesImagesDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesStockUpdateDto;
import mx.edu.utez.carsishop.controllers.clothes.ClothesUpdateDto;
import mx.edu.utez.carsishop.models.category.Category;
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
import mx.edu.utez.carsishop.utils.PaginationDto;
import mx.edu.utez.carsishop.utils.UploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.sql.SQLException;
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

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto) throws SQLException {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()
        )
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado y paginación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getFilter().equals("name") || !paginationDto.getPaginationType().getSortBy().equals("name"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado u ordenación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El tipo de orden proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        int count = clothesRepository.searchCount();

        List<Clothes> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "name":
                list = clothesRepository.findAllByNamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            default:
                return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El filtro proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de productos obtenida correctamente.", count), HttpStatus.OK);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> createClothes(ClothesDto clothes) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Clothes newClothes = clothes.castToClothes();
        String emaildecoded=cryptoService.decrypt(clothes.getSellerEmail());
        Optional<User> user = userRepository.findByUsername(emaildecoded);
        if(user.isPresent()){
            newClothes.setSeller(user.get().getSeller());
        }else {
            return new CustomResponse<>(null, true, 400, "User not found", 0);
        }
        Clothes clothesSaved = clothesRepository.save(newClothes);
        for (Stock stock:clothes.getStock()) {
            stock.setClothes(clothesSaved);
        }
        stockRepository.saveAll(clothes.getStock());
        return new CustomResponse<>(clothesSaved, false, 200, "Clothes created", 1);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> updateClothesInformation(ClothesUpdateDto clothes) {
        Optional <Clothes> clothesOptional = clothesRepository.findById(clothes.getId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found", 0);
        }else {
            Clothes clothesToUpdate = clothesOptional.get();
            clothesToUpdate.setName(clothes.getName());
            clothesToUpdate.setDescription(clothes.getDescription());
            clothesToUpdate.setCategory(clothes.getCategory());
            clothesToUpdate.setSubcategory(clothes.getSubcategory());
            clothesToUpdate.setStock(clothesToUpdate.getStock());
            return new CustomResponse<>(clothesRepository.save(clothesToUpdate), false, 200, "Clothes updated", 1);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<List<Stock>> updateStock(ClothesStockUpdateDto clothesStockUpdateDto){
        Optional<Clothes> clothesOptional = clothesRepository.findById(clothesStockUpdateDto.getStock().get(0).getId());
        if(clothesOptional.isEmpty()){
            return new CustomResponse<>(null, true, 400, "Clothes not found", 0);
        }else {
            return new CustomResponse<>(stockRepository.saveAll(clothesStockUpdateDto.getStock()), false, 200, "Stock updated", 0);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Clothes> disableCloth(Long id){
        Optional<Clothes> clothesOptional = clothesRepository.findById(id);
        if(clothesOptional.isEmpty()) {
            return new CustomResponse<>(null, true, 400, "Clothes not found", 0);
        }
        clothesOptional.get().setEnabled(false);
        return new CustomResponse<>(clothesRepository.save(clothesOptional.get()), false, 200, "Clothes found", 0);
    }



    public CustomResponse<Clothes> getOne(Long id) {
        if(!this.clothesRepository.existsById(id)) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontro alguna prenda",
                    0
            );
        }

        return new CustomResponse<Clothes>(
                this.clothesRepository.findById(id).get(),
                false,
                200,
                "Ok",
                1
        );
    }

    public CustomResponse<List<Clothes>> findByCategory(String category) {
        List<Clothes> clothes = this.clothesRepository.findClothesByCategory(category);

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontró ropa en esa categoria",
                    0
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok",
                clothes.size()
        );
    }

    public CustomResponse<List<Clothes>> findByCategoryAndSubcategory(String category, String subcategory) {
        List<Clothes> clothes = this.clothesRepository.findClothesByCategoryAndSubcategory(category, subcategory);

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontró ropa en esa categoria o subcategoria",
                    0
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok",
                clothes.size()
        );
    }

    public CustomResponse<List<Clothes>> findAllClothesOrderedByPrice() {
        List<Clothes> clothes = this.clothesRepository.findAllClothesOrderedByPrice();

        if(clothes.isEmpty()) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No hay datos",
                    0
            );
        }

        return new CustomResponse<>(
                clothes,
                false,
                200,
                "Ok",
                clothes.size()
        );
    }


}
