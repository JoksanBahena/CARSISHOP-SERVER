package mx.edu.utez.carsishop.controllers.subcategory;

import mx.edu.utez.carsishop.models.subcategory.dtos.SubcategoryDto;
import mx.edu.utez.carsishop.services.subcategory.SubcategoryService;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping(path = "/api/subcategories")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @PostMapping("/find-all")
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) throws SQLException {
        return subcategoryService.findAll(paginationDto);
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@Validated({SubcategoryDto.Register.class}) @RequestBody SubcategoryDto subcategoryDto) throws SQLException {
        return subcategoryService.register(subcategoryDto);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@Validated({SubcategoryDto.Update.class}) @RequestBody SubcategoryDto subcategoryDto) throws SQLException {
        return subcategoryService.update(subcategoryDto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@Validated({SubcategoryDto.ChangeStatus.class}) @RequestBody SubcategoryDto subcategoryDto) throws SQLException {
        return subcategoryService.changeStatus(subcategoryDto);
    }

}
