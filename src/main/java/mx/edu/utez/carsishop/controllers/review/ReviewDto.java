package mx.edu.utez.carsishop.controllers.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.review.Review;
import mx.edu.utez.carsishop.models.user.User;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReviewDto {

    @NotNull(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El puntaje es obligatorio")
    private int score;

    @NotNull(message = "El id del usuario es obligatorio")
    private User user;

    @NotNull(message = "El id de la prenda es obligatorio")
    private Clothes clothes;


    public Review castToReview() {
        Review review = new Review();
        review.setScore(this.score);
        review.setDescription(this.description);
        review.setUser(this.user);
        review.setClothes(this.clothes);
        return review;
    }
}
