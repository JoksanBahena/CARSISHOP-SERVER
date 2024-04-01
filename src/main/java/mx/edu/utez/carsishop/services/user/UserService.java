package mx.edu.utez.carsishop.services.user;

import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.sellers.SellerRepository;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public CustomResponse<Seller> register(Seller seller) {
        if(this.sellerRepository.existsByCurp(seller.getCurp()) || this.sellerRepository.existsByRfc(seller.getCurp())) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "El RFC o CURP ya se encuentra registrado en el sistema"
            );
        }
        this.userRepository.findById(seller.getUser().getId()).get().setRole(Role.SELLER);
        return new CustomResponse<>(
                this.sellerRepository.save(seller),
                false,
                200,
                "Ok"
        );
    }

    public CustomResponse<Seller> update(Seller seller) {
        if(!this.sellerRepository.existsById(seller.getUser().getId())) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "No se encontró al vendedor"
            );
        }

        if(this.sellerRepository.existsByCurp(seller.getCurp()) || this.sellerRepository.existsByRfc(seller.getCurp())) {
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "El RFC o CURP ya se encuentra registrado en el sistema"
            );
        }

        return new CustomResponse<>(
                this.sellerRepository.save(seller),
                false,
                200,
                "Vendedor actualizado"
        );
    }
}
