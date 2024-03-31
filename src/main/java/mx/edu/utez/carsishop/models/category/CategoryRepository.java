package mx.edu.utez.carsishop.models.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE UPPER(c.name) LIKE UPPER(?1)")
    List<Category> findAllByNamePagination(String value, Pageable offset);

    @Query(value = "SELECT COUNT(id) FROM Category ", nativeQuery = true)
    long searchCount();

    Optional<Category> findByNameIgnoreCase(String name);

    Optional<Category> findByIdNotAndNameIgnoreCase(Long id, String name);

    Optional<Category> findByName(String category);
}
