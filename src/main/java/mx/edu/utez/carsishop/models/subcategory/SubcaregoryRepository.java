package mx.edu.utez.carsishop.models.subcategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubcaregoryRepository extends JpaRepository<Subcategory, Long> {
    Optional<Subcategory> findByName(String subcategory);
}
