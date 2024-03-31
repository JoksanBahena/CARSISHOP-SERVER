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
public class ClothesUpdateDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
    @NotNull(message = "El nombre es obligatorio")
    private String name;
    @NotNull(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "La talla es obligatoria")
    private Category category;
    @NotNull(message = "La subcategoría es obligatoria")
    private Subcategory subcategory;
    @NotNull(message = "El precio es obligatorio")
    private float price;

    public Clothes castToClothes(){
        Clothes clothe=new Clothes();
        clothe.setId(id);
        clothe.setName(name);
        clothe.setDescription(description);
        clothe.setPrice(price);
        clothe.setCategory(category);
        clothe.setSubcategory(subcategory);
        return clothe;
    }
}
