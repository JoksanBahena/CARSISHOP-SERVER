package mx.edu.utez.carsishop.controllers.seller;


import mx.edu.utez.carsishop.models.sellers.dtos.SellerDto;
import mx.edu.utez.carsishop.services.seller.SellerService;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/sellers")
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
    public ResponseEntity<Object> register(@Validated({SellerDto.Register.class}) @ModelAttribute SellerDto sellerDto) throws IOException {
        return sellerService.register(sellerDto);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@Validated({SellerDto.Update.class}) @RequestBody SellerDto sellerDto) {
        return sellerService.update(sellerDto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@Validated({SellerDto.ChangeStatus.class}) @RequestBody SellerDto sellerDto) {
        return sellerService.changeStatus(sellerDto);
    }

}
