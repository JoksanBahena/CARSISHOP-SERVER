package mx.edu.utez.carsishop.controllers.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.user.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CardDto {


    @NotNull(groups = {CardDto.Update.class,CardDto.Delete.class}, message = "El número de tarjeta es obligatorio")
    private Long id;
    @NotNull(groups = {CardDto.Register.class, CardDto.Update.class}, message = "El número de tarjeta es obligatorio")
    private String number;
    @NotNull(groups = {CardDto.Register.class, CardDto.Update.class}, message = "La fecha de expiración es obligatoria")
    private String expirationDate;
    @NotNull(groups = {CardDto.Register.class, CardDto.Update.class}, message = "El cvv es obligatorio")
    private String cvv;
    @NotNull(groups = {CardDto.Register.class, CardDto.Update.class}, message = "El propietario es obligatorio")
    private String owner;
    @NotNull(groups = {CardDto.Update.class}, message = "El estado de la tarjeta es obligatorio")
    private boolean enable;

    public Card castToCardtoInsert() {
        Card card= new Card();
        card.setNumber(getNumber());
        card.setExpirationDate(getExpirationDate());
        card.setCvv(getCvv());
        card.setOwner(getOwner());
        return card;
    }

    public Card castToCardtoUpdate() {
        Card card= new Card();
        card.setId(getId());
        card.setNumber(getNumber());
        card.setExpirationDate(getExpirationDate());
        card.setCvv(getCvv());
        card.setOwner(getOwner());
        card.setEnable(enable);
        return card;
    }

    public interface Register {
    }

    public interface Update {
    }

    public interface Delete {
    }
}
