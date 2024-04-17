package mx.edu.utez.carsishop.models.sellers.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class SellerDto {

    @NotNull(groups = {Update.class, ChangeStatus.class}, message = "El id del vendedor no puede ser nulo.")
    private Long id;
    @NotNull(groups = {Register.class, Update.class}, message = "El RFC del vendedor no puede ser nulo.")
    private String rfc;
    @NotNull(groups = {Register.class, Update.class}, message = "El CURP del vendedor no puede ser nulo.")
    private String curp;
    @NotNull(groups = {Update.class}, message = "El estado de la solicitud del vendedor no puede ser nulo.")
    private String request_status;
    @NotNull(groups = {Register.class}, message = "La imagen del vendedor no puede ser nula.")
    private MultipartFile image;


    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }

}
