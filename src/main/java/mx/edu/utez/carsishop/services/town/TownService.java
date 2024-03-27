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
    @Autowired
    private TownRepository townRepository;

    @Transactional(readOnly = true)
    public CustomResponse<List<Town>> findAll(){
        return new CustomResponse<>(townRepository.findAll(), false, 200, "OK");
    }
}
