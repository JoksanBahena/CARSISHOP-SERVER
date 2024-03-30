package mx.edu.utez.carsishop.services.clothes;

import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClothesService {
    @Autowired
    private ClothesRepository clothesRepository;

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
}
