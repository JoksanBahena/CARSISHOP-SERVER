package mx.edu.utez.carsishop.services.subcategory;

import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.category.dtos.CategoryDto;
import mx.edu.utez.carsishop.models.subcategory.SubcaregoryRepository;
import mx.edu.utez.carsishop.models.subcategory.Subcategory;
import mx.edu.utez.carsishop.models.subcategory.dtos.SubcategoryDto;
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
public class SubcategoryService {

    private final SubcaregoryRepository subcategoryRepository;

    @Autowired
    public SubcategoryService(SubcaregoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
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
        int count = subcategoryRepository.searchCount();

        List<Subcategory> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "name":
                list = subcategoryRepository.findAllByNamePagination(
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

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de subcategorias obtenida correctamente.", count), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> register(SubcategoryDto dto) {

        if (dto.getName().isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la subcategoria no puede estar vacío.", 0), HttpStatus.BAD_REQUEST);
        }

        //more than 3 characters
        if (dto.getName().length() < 3) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la subcategoria debe tener al menos 3 caracteres.", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Subcategory> subcategory = subcategoryRepository.findByNameIgnoreCase(dto.getName());

        if (subcategory.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La subcategoría ya existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Subcategory subcategorySave = new Subcategory();
        subcategorySave.setName(dto.getName());
        subcategorySave.setStatus(true);

        subcategorySave = this.subcategoryRepository.save(subcategorySave);

        return new ResponseEntity<>(
                new CustomResponse<>(
                        subcategorySave,
                        false, HttpStatus.CREATED.value(),
                        "Subcategoria registrada correctamente.",
                        1),
                HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(SubcategoryDto dto) {

        Optional<Subcategory> subcategoryExist = subcategoryRepository.findById(dto.getId());
        if (!subcategoryExist.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La subcategoría no existe.", 0), HttpStatus.BAD_REQUEST);
        }

        if (dto.getName().isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la subcategoria no puede estar vacío.", 0), HttpStatus.BAD_REQUEST);
        }

        //more than 3 characters
        if (dto.getName().length() < 3) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El nombre de la subcategoria debe tener al menos 3 caracteres.", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Subcategory> subcategory = subcategoryRepository.findByIdNotAndNameIgnoreCase(dto.getId(), dto.getName());

        if (subcategory.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La subcategoría ya existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Subcategory subcategorySave = subcategoryExist.get();

        subcategorySave.setName(dto.getName());
        subcategorySave.setStatus(subcategoryExist.get().isStatus());

        this.subcategoryRepository.save(subcategorySave);

        return new ResponseEntity<>(new CustomResponse<>(subcategorySave, false, HttpStatus.CREATED.value(), "Subcategoría actualizada correctamente.", 1), HttpStatus.CREATED);

    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(SubcategoryDto dto) {
        Optional<Subcategory> subcategory = subcategoryRepository.findById(dto.getId());

        if (subcategory.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "La subcategoría no existe.", 0), HttpStatus.BAD_REQUEST);
        }

        Subcategory subcategoryUpdate = subcategory.get();
        subcategoryUpdate.setStatus(!subcategoryUpdate.isStatus());

        this.subcategoryRepository.save(subcategoryUpdate);

        return new ResponseEntity<>(new CustomResponse<>(subcategoryUpdate, false, HttpStatus.CREATED.value(), "Subcategoría actualizada correctamente.", 1), HttpStatus.CREATED);
    }


}
