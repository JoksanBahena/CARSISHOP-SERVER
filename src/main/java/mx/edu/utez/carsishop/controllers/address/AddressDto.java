package mx.edu.utez.carsishop.controllers.address;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDto {
    @NotNull(groups = {Delete.class}, message = "El id es obligatorio")
    String id;
    @NotNull(groups = {Register.class, Update.class}, message = "El nombre es obligatorio")
    String name;
    @NotNull(groups = {Register.class, Update.class}, message = "El estado es obligatorio")
    Long state;
    @NotNull(groups = {Register.class, Update.class}, message = "El municipio es obligatorio")
    Long town;
    @NotNull(groups = {Register.class, Update.class}, message = "El código postal es obligatorio")
    String cp;
    @NotNull(groups = {Register.class, Update.class}, message = "La colonia es obligatoria")
    String suburb;
    @NotNull(groups = {Register.class, Update.class}, message = "La calle es obligatoria")
    String street;
    @NotNull(groups = {Register.class, Update.class}, message = "El número interior es obligatorio")
    String intnumber;
    @NotNull(groups = {Register.class, Update.class}, message = "El número exterior es obligatorio")
    String extnumber;
    @NotNull(groups = {Register.class, Update.class}, message = "El id del usuario es obligatorio")
    Long user;
    Long order;

    public interface Register {}
    public interface Update {}
    public interface Delete {}
}
