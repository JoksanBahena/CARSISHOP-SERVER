package mx.edu.utez.carsishop.controllers.seller;


import mx.edu.utez.carsishop.models.sellers.dtos.SellerDto;
import mx.edu.utez.carsishop.services.seller.SellerService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(path = "/api/sellers", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
public class SellerController {

    private final SellerService sellerService;

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/find-all")
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) {
        return sellerService.findAll(paginationDto);
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@RequestHeader("Authorization") String authorizationHeader,@Validated({SellerDto.Register.class}) @ModelAttribute SellerDto sellerDto) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return sellerService.register(sellerDto,jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestHeader("Authorization") String authorizationHeader,@Validated({SellerDto.Update.class}) @ModelAttribute SellerDto sellerDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return sellerService.update(sellerDto,jwtToken);
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@Validated({SellerDto.ChangeStatus.class}) @RequestBody SellerDto sellerDto) {
        return sellerService.changeStatus(sellerDto);
    }

}
