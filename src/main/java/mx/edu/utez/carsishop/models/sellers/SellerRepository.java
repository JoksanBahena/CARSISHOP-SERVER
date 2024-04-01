package mx.edu.utez.carsishop.models.sellers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByRfc (String rfc);
    boolean existsByCurp (String curp);
    Optional<Seller> findByUserId (Long idUser);
}
