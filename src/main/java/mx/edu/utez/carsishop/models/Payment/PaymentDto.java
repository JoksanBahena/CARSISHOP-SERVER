package mx.edu.utez.carsishop.models.Payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {

    @NotNull(message = "Campo obligatorio")
    @Min(value = 0, message = "El monto debe ser mayor a 0")
    private long amount;

    @NotNull(message = "Campo obligatorio")
    private String productName;
}
