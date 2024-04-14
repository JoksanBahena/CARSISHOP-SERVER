package mx.edu.utez.carsishop.models.clothes;

import mx.edu.utez.carsishop.models.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {
    @Query(
            value = "SELECT * FROM Clothes WHERE id = :id", nativeQuery = true
    )
    Optional<Clothes> findById(Long id);

    @Query(
            value = "SELECT c.* FROM Clothes c JOIN Category cat ON c.category = cat.id WHERE cat.status = 1 AND cat.name = :category ORDER BY desc", nativeQuery = true
    )
    List<Clothes> findClothesByCategory(@Param("category") String category);

    @Query(
            value = "SELECT c.* FROM Clothes c JOIN Category cat ON c.category = cat.id JOIN Subcategory sub ON c.subcategory = sub.id WHERE cat.status = 1 AND cat.name = :category AND sub.name = :subcategory ORDER BY desc", nativeQuery = true
    )
    List<Clothes> findClothesByCategoryAndSubcategory(@Param("category") String category, @Param("subcategory") String subcategory);

    @Query(
            value = "SELECT * FROM Clothes ORDER BY price ASC", nativeQuery = true
    )
    List<Clothes> findAllClothesOrderedByPrice();

    @Query(value = "SELECT COUNT(id) FROM clothes", nativeQuery = true)
    int searchCount();

    @Query(value = "SELECT c FROM Clothes c WHERE UPPER(c.name) LIKE UPPER(?1)")
    List<Clothes> findAllByNamePagination(String value, Pageable offset);

    //isAccepted
    @Query(value = "SELECT c FROM Clothes c WHERE c.request_status = ?1")
    List<Clothes> findAllByRequestStatus(String value, Pageable offset);
}
