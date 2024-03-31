package mx.edu.utez.carsishop.models.subcategory;

import mx.edu.utez.carsishop.models.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubcaregoryRepository extends JpaRepository<Subcategory, Long> {

    @Query(value = "SELECT s FROM Subcategory s WHERE UPPER(s.name) LIKE UPPER(?1)")
    List<Subcategory> findAllByNamePagination(String value, Pageable offset);

    @Query(value = "SELECT COUNT(id) FROM subcategory", nativeQuery = true)
    long searchCount();

    Optional<Subcategory> findByNameIgnoreCase(String name);

    Optional<Subcategory> findByIdNotAndNameIgnoreCase(Long id, String name);
}
