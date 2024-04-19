package mx.edu.utez.carsishop.controllers.town;

import mx.edu.utez.carsishop.models.town.Town;
import mx.edu.utez.carsishop.services.town.TownService;
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
@RequestMapping(path="/api/town", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin({"*"})
public class TownController {
    private final TownService townService;

    @Autowired
    public TownController(TownService townService) {
        this.townService = townService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<CustomResponse<List<Town>>> findAll(){
        return new ResponseEntity<>(townService.findAll(), HttpStatus.OK);
    }
}
