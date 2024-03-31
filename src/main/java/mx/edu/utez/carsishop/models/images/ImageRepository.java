package mx.edu.utez.carsishop.models.images;

import mx.edu.utez.carsishop.models.clothes.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUrl(String url);

    List<Image> findByClothes(Clothes clothes);
}
