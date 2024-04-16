package mx.edu.utez.carsishop.models.town;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TownRepository extends JpaRepository<Town, Long> {
    Optional<Town> findTownByName(String name);
}
