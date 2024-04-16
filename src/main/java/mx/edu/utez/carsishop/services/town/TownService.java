package mx.edu.utez.carsishop.services.town;

import mx.edu.utez.carsishop.models.town.Town;
import mx.edu.utez.carsishop.models.town.TownRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TownService {
    private final TownRepository townRepository;

    @Autowired
    public TownService(TownRepository townRepository) {
        this.townRepository = townRepository;
    }

    @Transactional(readOnly = true)
    public CustomResponse<List<Town>> findAll(){

        List<Town> towns = townRepository.findAll();

        return new CustomResponse<>(towns, false, 200, "OK", towns.size());
    }
}
