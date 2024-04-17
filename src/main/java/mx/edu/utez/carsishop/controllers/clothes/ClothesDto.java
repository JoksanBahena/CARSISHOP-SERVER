package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.stock.Stock;
import mx.edu.utez.carsishop.models.subcategory.Subcategory;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClothesDto {
    @NotNull(groups = {ChangeIsAccepted.class}, message = "El id es obligatorio")
    private long id;
    @NotNull(groups = {FindAllByCategoryAndName.class}, message = "El nombre es obligatorio")
    private String name;
    @NotNull(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "La talla y stock es obligatoria")
    private List<Stock> stock;
    @NotNull(message = "El email del vendedor es obligatorio")
    private String sellerEmail;
    @NotNull(groups = {FindAllByCategoryAndName.class}, message = "La talla es obligatoria")
    private Category category;
    @NotNull(message = "La subcategoría es obligatoria")
    private Subcategory subcategory;
    @NotNull(groups = {ChangeIsAccepted.class}, message = "El estado es obligatorio")
    private String request_status;


    public Clothes castToClothes(){
        Clothes clothe=new Clothes();
        clothe.setName(name);
        clothe.setDescription(description);
        clothe.setCategory(category);
        clothe.setSubcategory(subcategory);
        clothe.setStatus(true);
        clothe.setRequest_status(request_status);
        return clothe;
    }

    public interface ChangeIsAccepted{

    }

    public interface FindAllByCategoryAndName{

    }
}
