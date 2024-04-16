package mx.edu.utez.carsishop.controllers.category;

import mx.edu.utez.carsishop.models.category.dtos.CategoryDto;
import mx.edu.utez.carsishop.services.category.CategoryService;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) {
        return categoryService.findAll(paginationDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        return categoryService.findAllWithoutPagination();
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@Validated({CategoryDto.Register.class}) @RequestBody CategoryDto categoryDto) {
        return categoryService.register(categoryDto);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@Validated({CategoryDto.Update.class}) @RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatus(@Validated({CategoryDto.ChangeStatus.class}) @RequestBody CategoryDto categoryDto) {
        return categoryService.changeStatus(categoryDto);
    }





}
