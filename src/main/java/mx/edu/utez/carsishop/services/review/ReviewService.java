package mx.edu.utez.carsishop.services.review;

import mx.edu.utez.carsishop.controllers.review.ReviewDto;
import org.springframework.transaction.annotation.Transactional;
import mx.edu.utez.carsishop.models.review.Review;
import mx.edu.utez.carsishop.models.review.ReviewRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional(rollbackFor = {SQLException.class})
    public CustomResponse<Review> createReview(ReviewDto review) {
        Review newReview = review.castToReview();
        Review reviewSaved = reviewRepository.save(newReview);
        return new CustomResponse<>(reviewSaved, false, 200, "Reseña creada correctamente", 1);
    }
}
