package mx.edu.utez.carsishop.controllers.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.carsishop.utils.CryptoService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderDto {
    @NotNull
    private String address;
    @NotNull
    private String card;

    private CryptoService cryptoService = new CryptoService();

    public void uncrypt() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.address = cryptoService.decrypt(this.address);
        this.card = cryptoService.decrypt(this.card);
    }
}
