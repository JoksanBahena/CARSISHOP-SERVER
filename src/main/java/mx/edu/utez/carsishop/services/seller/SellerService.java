package mx.edu.utez.carsishop.services.seller;

import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.sellers.SellerRepository;
import mx.edu.utez.carsishop.models.sellers.dtos.SellerDto;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import mx.edu.utez.carsishop.utils.PaginationDto;
import mx.edu.utez.carsishop.utils.UploadImage;
import mx.edu.utez.carsishop.utils.ValidateTypeFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class SellerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto) {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()
        )
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado y paginación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente."), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getFilter().equals("curp") || !paginationDto.getPaginationType().getSortBy().equals("curp") ||
                !paginationDto.getPaginationType().getFilter().equals("rfc") || !paginationDto.getPaginationType().getSortBy().equals("rfc") ||
                !paginationDto.getPaginationType().getFilter().equals("request_status") || !paginationDto.getPaginationType().getSortBy().equals("request_status") ||
                !paginationDto.getPaginationType().getFilter().equals("user_name") || !paginationDto.getPaginationType().getSortBy().equals("user_name") ||
                !paginationDto.getPaginationType().getFilter().equals("user_surname") || !paginationDto.getPaginationType().getSortBy().equals("user_surname"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado u ordenación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente."), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc"))
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El tipo de orden proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente."), HttpStatus.BAD_REQUEST);

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        long count = sellerRepository.searchCount();

        List<Seller> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "curp":
                list = sellerRepository.findAllByCurpPagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "rfc":
                list = sellerRepository.findAllByRfcPagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "request_status":
                list = sellerRepository.findAllByRequestStatusPagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "user_name":
                list = sellerRepository.findAllByUserNamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;

            case "user_surname":
                list = sellerRepository.findAllByUserSurnamePagination(
                        paginationDto.getValue(),
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            default:
                return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El filtro proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente."), HttpStatus.OK);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<Object> register(SellerDto seller) throws IOException {

        if (this.sellerRepository.existsByCurp(seller.getCurp())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El CURP ya se encuentra registrado en el sistema"), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByRfc(seller.getRfc())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El RFC ya se encuentra registrado en el sistema"), HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = this.userRepository.findByUsername(seller.getUser().getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al usuario registrado dentro del sistema"), HttpStatus.BAD_REQUEST);
        }

        Optional<Seller> sellerPending = this.sellerRepository.findSellerPending(user.get().getId());
        if (sellerPending.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "Ya existe una solicitud de vendedor pendiente"), HttpStatus.BAD_REQUEST);
        }

        try {
            ValidateTypeFile validateTypeFile = new ValidateTypeFile();

            if(!validateTypeFile.isImageFile(seller.getImage())) {
                return new ResponseEntity<>(new CustomResponse<>(
                        null, true, 400, "El archivo debe ser de tipo imagen (JPEG, JPG, PNG)"
                ), HttpStatus.BAD_REQUEST);
            }

            UploadImage uploadImage = new UploadImage();
            String urlImage = uploadImage.uploadImage(seller.getImage(), seller.getRfc(), "sellers");

            Seller sellerToSave = new Seller();
            sellerToSave.setCurp(seller.getCurp());
            sellerToSave.setRfc(seller.getRfc());
            sellerToSave.setUser(seller.getUser());
            sellerToSave.setImage(urlImage);
            sellerToSave.setStatus(true);
            sellerToSave.setRequest_status("PENDING");

            sellerToSave = this.sellerRepository.save(sellerToSave);

            return new ResponseEntity<>(new CustomResponse<>(sellerToSave, false, 201, "Vendedor registrado correctamente"), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CustomResponse<>(
                    null, true, 400, "Error al guardar al vendedor"
            ), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<Object> update(SellerDto seller) {

        Optional<Seller> sellerOptional = this.sellerRepository.findById(seller.getId());

        seller.setRequest_status(seller.getRequest_status().toUpperCase());

        if (!sellerOptional.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al vendedor"), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByCurpAndAndIdNot(seller.getCurp(), seller.getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El CURP ya se encuentra registrado en el sistema"), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByRfcAndAndIdNot(seller.getRfc(), seller.getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El RFC ya se encuentra registrado en el sistema"), HttpStatus.BAD_REQUEST);
        }

        if (!seller.getRequest_status().equals("APPROVED") && !seller.getRequest_status().equals("REJECTED") && !seller.getRequest_status().equals("PENDING")) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El estatus de la solicitud es inválido"), HttpStatus.BAD_REQUEST);
        }

        if (!this.userRepository.existsById(seller.getUser().getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al usuario registrado dentro del sistema"), HttpStatus.BAD_REQUEST);
        }

        //Validaciones y subida de imagen
        //
        //

        Seller sellerToUpdate = sellerOptional.get();
        sellerToUpdate.setCurp(seller.getCurp());
        sellerToUpdate.setRfc(seller.getRfc());
        sellerToUpdate.setRequest_status(seller.getRequest_status());
        sellerToUpdate.setUser(seller.getUser());

        sellerToUpdate = this.sellerRepository.save(sellerToUpdate);

        if (seller.getRequest_status().equals("APPROVED")) {
            User user = this.userRepository.findById(seller.getUser().getId()).get();
            user.setRole(Role.SELLER);
            this.userRepository.save(user);
        }

        if (seller.getRequest_status().equals("REJECTED") || seller.getRequest_status().equals("PENDING")) {
            User user = this.userRepository.findById(seller.getUser().getId()).get();
            user.setRole(Role.CUSTOMER);
            this.userRepository.save(user);
        }

        return new ResponseEntity<>(new CustomResponse<>(sellerToUpdate, false, 200, "Vendedor actualizado correctamente"), HttpStatus.OK);
    }

    public ResponseEntity<Object> changeStatus(SellerDto seller) {
        Optional<Seller> sellerOptional = this.sellerRepository.findById(seller.getId());

        if (!sellerOptional.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al vendedor"), HttpStatus.BAD_REQUEST);
        }

        Seller sellerToUpdate = sellerOptional.get();
        sellerToUpdate.setStatus(!sellerToUpdate.isStatus());

        sellerToUpdate = this.sellerRepository.save(sellerToUpdate);

        return new ResponseEntity<>(new CustomResponse<>(sellerToUpdate, false, 200, "Estatus del vendedor actualizado correctamente"), HttpStatus.OK);
    }
}
