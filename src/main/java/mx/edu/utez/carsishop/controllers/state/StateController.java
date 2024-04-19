package mx.edu.utez.carsishop.controllers.state;

import mx.edu.utez.carsishop.models.state.State;
import mx.edu.utez.carsishop.services.state.StateService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/state", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin({"*"})
public class StateController {

    private final StateService stateService;

    @Autowired
    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<CustomResponse<List<State>>> findAll(){
        return new ResponseEntity<>(stateService.findAll(), HttpStatus.OK);
    }
}
