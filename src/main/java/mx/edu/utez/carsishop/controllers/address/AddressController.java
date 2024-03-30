package mx.edu.utez.carsishop.controllers.address;

import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.services.address.AddressService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin({"*"})
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/getByUser/{email}")
    public ResponseEntity<CustomResponse<List<Address>>> getByUser(@PathVariable String email) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        /*
        email = desecnptar(email);
         */
        return new ResponseEntity<>(addressService.getByUser(email), HttpStatus.OK);
    }

    @PostMapping("/register")
    private ResponseEntity<CustomResponse<Address>> register(@RequestBody Address address){
        return new ResponseEntity<>(addressService.register(address), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    private ResponseEntity<CustomResponse<Address>> update(@RequestBody Address updatedAddress,@PathVariable String id){
           /*
        long idlong = desecnptar(id);
         */
        long idLong = Long.parseLong(id);
        return new ResponseEntity<>(addressService.update(updatedAddress,idLong), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<CustomResponse<String>> delete(@PathVariable String id){
        /*
        long idlong = desecnptar(id);
         */
        long idLong = Long.parseLong(id);
        return new ResponseEntity<>(addressService.delete(idLong), HttpStatus.OK);
    }

}
