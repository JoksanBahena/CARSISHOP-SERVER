package mx.edu.utez.carsishop.models.sellers;

import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByRfc (String rfc);
    boolean existsByCurp (String curp);
    boolean existsByCurpAndAndIdNot(String curp, long id);
    boolean existsByRfcAndAndIdNot(String rfc, long id);

    @Query("SELECT s FROM Seller s WHERE s.user.id = ?1 AND s.request_status = 'PENDING'")
    Optional<Seller> findSellerPending(long userId);

    @Query("SELECT s FROM Seller s WHERE s.user.id = ?1 AND s.request_status = 'REJECTED'")
    Optional<Seller> findSellerRejected(long userId);

    @Query(value = "SELECT s FROM Seller s WHERE UPPER(s.curp) LIKE UPPER(?1)")
    List<Seller> findAllByCurpPagination(String value, Pageable offset);

    @Query(value = "SELECT s FROM Seller s WHERE UPPER(s.rfc) LIKE UPPER(?1)")
    List<Seller> findAllByRfcPagination(String value, Pageable offset);

    @Query(value = "SELECT s FROM Seller s WHERE UPPER(s.request_status) LIKE UPPER(?1)")
    List<Seller> findAllByRequestStatusPagination(String value, Pageable offset);

    @Query(value = "SELECT s FROM Seller s WHERE UPPER(s.user.name) LIKE UPPER(?1)")
    List<Seller> findAllByUserNamePagination(String value, Pageable offset);

    @Query(value = "SELECT s FROM Seller s WHERE UPPER(s.user.surname) LIKE UPPER(?1)")
    List<Seller> findAllByUserSurnamePagination(String value, Pageable offset);

    @Query(value = "SELECT COUNT(id) FROM sellers", nativeQuery = true)
    int searchCount();

}
