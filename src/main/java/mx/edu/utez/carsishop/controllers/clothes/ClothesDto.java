package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.size.Size;
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
    @NotNull(message = "El nombre es obligatorio")
    private String name;
    @NotNull(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "La talla y stock es obligatoria")
    private List<Stock> stock;
    @NotNull(message = "El email del vendedor es obligatorio")
    private String sellerEmail;
    @NotNull(message = "La talla es obligatoria")
    private Category category;
    @NotNull(message = "La subcategoría es obligatoria")
    private Subcategory subcategory;
    @NotNull(groups = {ChangeIsAccepted.class}, message = "El estado es obligatorio")
    private boolean isAccepted;


    public Clothes castToClothes(){
        Clothes clothe=new Clothes();
        clothe.setName(name);
        clothe.setDescription(description);
        clothe.setCategory(category);
        clothe.setSubcategory(subcategory);
        clothe.setStatus(true);
        clothe.setAccepted(false);
        return clothe;
    }

    public interface ChangeIsAccepted{

    }
}
