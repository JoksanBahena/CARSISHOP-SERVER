package mx.edu.utez.carsishop.controllers.category;

import mx.edu.utez.carsishop.models.category.dtos.CategoryDto;
import mx.edu.utez.carsishop.services.category.CategoryService;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping(path = "/api/categories")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/find-all")
    public ResponseEntity<Object> findAll(@RequestBody PaginationDto paginationDto) throws SQLException {
        return categoryService.findAll(paginationDto);
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@RequestBody CategoryDto categoryDto) throws SQLException {
        return categoryService.register(categoryDto);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestBody CategoryDto categoryDto) throws SQLException {
        return categoryService.update(categoryDto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@RequestBody CategoryDto categoryDto) throws SQLException {
        return categoryService.changeStatus(categoryDto);
    }





}
