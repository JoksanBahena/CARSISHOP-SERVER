package mx.edu.utez.carsishop.services.state;

import mx.edu.utez.carsishop.models.state.State;
import mx.edu.utez.carsishop.models.state.StateRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StateService {
    @Autowired
    private StateRepository stateRepository;

    @Transactional(readOnly = true)
    public CustomResponse<List<State>> findAll(){
        return new CustomResponse<>(stateRepository.findAll(), false, 200, "OK");
    }
}
