package mx.edu.utez.carsishop.models.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(
            value = "SELECT * FROM Review WHERE clothes = :clothes", nativeQuery = true
    )
    Optional<Review> findReviewByClothes(Long clothes);
}
