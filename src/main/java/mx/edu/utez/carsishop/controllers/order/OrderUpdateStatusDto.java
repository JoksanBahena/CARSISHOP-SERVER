package mx.edu.utez.carsishop.controllers.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderUpdateStatusDto {
    @NotNull
    private String id;
    @NotNull
    private String status;
}
