package mx.edu.utez.carsishop.controllers.review;

import mx.edu.utez.carsishop.models.review.Review;
import mx.edu.utez.carsishop.services.review.ReviewService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/api/reviews")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET})
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/")
    public ResponseEntity<CustomResponse<Review>> createReview(@Validated  @RequestBody ReviewDto review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }
}
