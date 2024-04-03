package mx.edu.utez.carsishop.models.sellers.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SellerDto {

    @NotNull(groups = {Update.class, ChangeStatus.class}, message = "El id del vendedor no puede ser nulo.")
    private Long id;
    @NotNull(groups = {Register.class, Update.class}, message = "El RFC del vendedor no puede ser nulo.")
    private String rfc;
    @NotNull(groups = {Register.class, Update.class}, message = "El CURP del vendedor no puede ser nulo.")
    private String curp;
    @NotNull(groups = {Register.class, Update.class}, message = "El estado de la solicitud del vendedor no puede ser nulo.")
    private String request_status;
    @NotNull(groups = {Register.class, Update.class}, message = "La imagen del vendedor no puede ser nula.")
    private MultipartFile image;
    @NotNull(groups = {Register.class, Update.class}, message = "El usuario del vendedor no puede ser nulo.")
    private User user;


    public SellerDto() {
    }

    public SellerDto(Long id, String rfc, String curp, String request_status, MultipartFile image, User user) {
        this.id = id;
        this.rfc = rfc;
        this.curp = curp;
        this.request_status = request_status;
        this.image = image;
        this.user = user;
    }

    public interface Register {
    }

    public interface Update {
    }

    public interface ChangeStatus {
    }

}
