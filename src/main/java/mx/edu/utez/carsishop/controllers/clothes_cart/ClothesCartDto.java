package mx.edu.utez.carsishop.controllers.clothes_cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.size.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClothesCartDto {

    @NotNull(message = "El id de la prenda es obligatorio")
    private Clothes cloth;
    @NotNull(message = "La cantidad es obligatoria")
    private int amount;
    @NotNull(message = "El tamaño es obligatorio")
    private Size size;


    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }
}
