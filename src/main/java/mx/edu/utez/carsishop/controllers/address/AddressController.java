package mx.edu.utez.carsishop.controllers.address;

import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.services.address.AddressService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = {"*"})
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/getByUser")
    public ResponseEntity<CustomResponse<List<Address>>> getByUser(@RequestHeader("Authorization") String authorizationHeader) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return new ResponseEntity<>(addressService.getByUser(jwtToken), HttpStatus.OK);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<Address>> register(@RequestHeader("Authorization") String authorizationHeader, @Validated({AddressDto.Register.class}) @RequestBody AddressDto addressDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return new ResponseEntity<>(addressService.register(addressDto, jwtToken), HttpStatus.OK);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Address>> update(@RequestBody Address updatedAddress,@PathVariable String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
           /*
        long idlong = desecnptar(id);
         */
        long idLong = Long.parseLong(id);
        return new ResponseEntity<>(addressService.update(updatedAddress,idLong), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> delete(@Validated({AddressDto.Delete.class}) @RequestBody AddressDto addressDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
        /*
        long idlong = desecnptar(id);
         */
        return new ResponseEntity<>(addressService.delete(addressDto), HttpStatus.OK);
    }

}
