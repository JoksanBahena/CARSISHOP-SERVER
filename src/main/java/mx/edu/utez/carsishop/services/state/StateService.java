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
    private final StateRepository stateRepository;

    @Autowired
    public StateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Transactional(readOnly = true)
    public CustomResponse<List<State>> findAll(){

        List<State> states = stateRepository.findAll();

        return new CustomResponse<>(states, false, 200, "OK", states.size());
    }
}
