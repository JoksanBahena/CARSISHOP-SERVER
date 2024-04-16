package mx.edu.utez.carsishop.controllers.clothes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.stock.Stock;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClothesStockUpdateDto {
    @NotNull(message = "El stock es obligatorio")
    private List<Stock> stock;

    @NotNull(message = "El id es obligatorio")
    private Clothes clothes;
}
