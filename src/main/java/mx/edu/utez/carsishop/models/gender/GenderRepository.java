package mx.edu.utez.carsishop.models.gender;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenderRepository extends JpaRepository<Gender,Long> {
    Optional<Gender> findById (Long id);

    Optional<Gender> findByGender(String gender);
}
