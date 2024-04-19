package mx.edu.utez.carsishop.controllers.subcategory;

import mx.edu.utez.carsishop.models.subcategory.dtos.SubcategoryDto;
import mx.edu.utez.carsishop.services.subcategory.SubcategoryService;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/subcategories", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @PostMapping("/find-all")
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) {
        return subcategoryService.findAll(paginationDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        return subcategoryService.findAllWithoutPagination();
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@Validated({SubcategoryDto.Register.class}) @RequestBody SubcategoryDto subcategoryDto) {
        return subcategoryService.register(subcategoryDto);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@Validated({SubcategoryDto.Update.class}) @RequestBody SubcategoryDto subcategoryDto) {
        return subcategoryService.update(subcategoryDto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@Validated({SubcategoryDto.ChangeStatus.class}) @RequestBody SubcategoryDto subcategoryDto) {
        return subcategoryService.changeStatus(subcategoryDto);
    }

}
