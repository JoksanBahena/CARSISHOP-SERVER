package mx.edu.utez.carsishop.controllers.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import jakarta.validation.Valid;
import mx.edu.utez.carsishop.models.Payment.Payment;
import mx.edu.utez.carsishop.models.Payment.PaymentDto;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/payment", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"*"})
public class PaymentController {
    @PostMapping("/")
    public ResponseEntity<Object> checkout(@Valid @RequestBody PaymentDto paymentDto) throws StripeException {

        Stripe.apiKey = "sk_test_51P6n2NKp3ij94G7IEn8TXmQ5zK4CkoPR53KHlPPhampiVk4gLZsiT6SPDbdpKI6l7K98neKQWodFSjepviw3Xv7o00W7Wrd2O4";
        ProductCreateParams params = ProductCreateParams.builder().setName(paymentDto.getProductName()).build();
        Product product = Product.create(params);

        PriceCreateParams paramsPrice =
                PriceCreateParams.builder()
                        .setProduct(product.getId())
                        .setUnitAmount(paymentDto.getAmount())
                        .setCurrency("mxn")
                        .build();

        Price price = Price.create(paramsPrice);

        CustomResponse customResponse = new CustomResponse(price.getId(), false, 200, "Precio creado exitosamente", 0);

        return ResponseEntity.ok(customResponse);

    }
}
