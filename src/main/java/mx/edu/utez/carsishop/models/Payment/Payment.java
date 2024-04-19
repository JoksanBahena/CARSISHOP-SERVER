package mx.edu.utez.carsishop.models.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    private String paymentIntent;
    private String ephemeralKey;
    private String customer;
    private String publishableKey;
}
