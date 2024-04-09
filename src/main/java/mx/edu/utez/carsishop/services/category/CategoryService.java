package mx.edu.utez.carsishop.services.category;

import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.category.CategoryRepository;
import mx.edu.utez.carsishop.models.category.dtos.CategoryDto;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto) throws SQLException {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()
        )
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado y paginación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getFilter().equals("name") || !paginationDto.getPaginationType().getSortBy().equals("name"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado u ordenación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El tipo de orden proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        int count = categoryRepository.searchCount();

        List<Category> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "name":
                list = categoryRepository.findAllByNamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            default:
                return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El filtro proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente.", count), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> register(CategoryDto dto) {

        if (dto.getName().isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la categoría no puede estar vacío.", 0), HttpStatus.BAD_REQUEST);
        }

        //more than 3 characters
        if (dto.getName().length() < 3) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la categoría debe tener al menos 3 caracteres.", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Category> category = categoryRepository.findByNameIgnoreCase(dto.getName());

        if (category.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La categoría ya existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Category categorySave = new Category();
        categorySave.setName(dto.getName());
        categorySave.setStatus(true);

        this.categoryRepository.save(categorySave);

        return new ResponseEntity<>(new CustomResponse<>(categorySave, false, HttpStatus.CREATED.value(), "Categoría registrada correctamente.", 1), HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(CategoryDto dto) {

        Optional<Category> categoryExists = categoryRepository.findById(dto.getId());

        if (dto.getName().isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la categoría no puede estar vacío.", 0), HttpStatus.BAD_REQUEST);
        }

        //more than 3 characters
        if (dto.getName().length() < 3) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la categoría debe tener al menos 3 caracteres.", 0), HttpStatus.BAD_REQUEST);
        }

        if (categoryExists.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La categoría no existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Category> category = categoryRepository.findByIdNotAndNameIgnoreCase(dto.getId(), dto.getName());

        if (category.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La categoría ya existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Category categoryUpdate = categoryExists.get();
        categoryUpdate.setName(dto.getName());

        this.categoryRepository.save(categoryUpdate);

        return new ResponseEntity<>(new CustomResponse<>(categoryUpdate, false, HttpStatus.CREATED.value(), "Categoría actualizada correctamente.", 1), HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(CategoryDto dto) {
        Optional<Category> category = categoryRepository.findById(dto.getId());

        if (category.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La categoría no existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Category categoryUpdate = category.get();
        categoryUpdate.setStatus(!categoryUpdate.isStatus());

        this.categoryRepository.save(categoryUpdate);

        return new ResponseEntity<>(new CustomResponse<>(categoryUpdate, false, HttpStatus.OK.value(), "Categoría actualizada correctamente.", 1), HttpStatus.OK);
    }


}
