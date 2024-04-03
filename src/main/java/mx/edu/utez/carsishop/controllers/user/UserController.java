//package mx.edu.utez.carsishop.controllers.user;
//
//import mx.edu.utez.carsishop.models.sellers.Seller;
//import mx.edu.utez.carsishop.services.seller.SellerService;
//import mx.edu.utez.carsishop.utils.CustomResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin({"*"})
//public class UserController {
//    @Autowired
//    private SellerService sellerService;
//
//    @PostMapping("/saveSeller")
//    public ResponseEntity<CustomResponse<Seller>> register(@RequestBody Seller seller) {
//        return new ResponseEntity<>(this.sellerService.register(seller), HttpStatus.OK);
//    }
//
//    @PutMapping("/updateSeller")
//    public ResponseEntity<CustomResponse<Seller>> update(@RequestBody Seller seller) {
//        return new ResponseEntity<>(this.sellerService.update(seller), HttpStatus.OK);
//    }
//}
