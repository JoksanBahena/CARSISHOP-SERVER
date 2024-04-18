package mx.edu.utez.carsishop.controllers.clothes_cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.controllers.card.CardDto;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.size.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClothesCartDto {
    @NotNull(groups = {CardDto.Update.class, CardDto.Delete.class}, message = "El id es obligatorio")
    private Long id;
    @NotNull(groups = {CardDto.Register.class},message = "El id de la prenda es obligatorio")
    private Clothes cloth;
    @NotNull(groups = {CardDto.Update.class,CardDto.Register.class},message = "La cantidad es obligatoria")
    private int amount;
    @NotNull(groups = {CardDto.Register.class},message = "La talla es obligatorio")
    private Size size;


    public interface Register {
    }

    public interface Update {
    }

    public interface Delete {
    }

}
