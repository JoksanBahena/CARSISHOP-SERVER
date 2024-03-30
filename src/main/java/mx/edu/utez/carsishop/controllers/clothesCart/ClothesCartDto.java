package mx.edu.utez.carsishop.controllers.clothesCart;

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
    @NotNull
    private String email;
    @NotNull
    private Clothes cloth;
    @NotNull
    private int amount;
    @NotNull
    private Size size;


    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }
}
