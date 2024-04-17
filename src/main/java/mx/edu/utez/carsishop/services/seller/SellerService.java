package mx.edu.utez.carsishop.services.seller;

import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.sellers.SellerRepository;
import mx.edu.utez.carsishop.models.sellers.dtos.SellerDto;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.*;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class SellerService {

    private static final String PENDING = "PENDING";
    private static final String REJECTED = "REJECTED";
    private static final String APPROVED = "APPROVED";
    private static final String REQUEST_STATUS = "request_status";
    private static final String USER_NAME = "user_name";
    private static final String USER_SURNAME = "user_surname";
    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;
    private final JwtService jwtService;
    private CryptoService cryptoService = new CryptoService();

    @Autowired
    public SellerService(UserRepository userRepository, SellerRepository sellerRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto) {
        ResponseEntity<Object> validationResponse = validatePaginationDto(paginationDto);
        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Function<PaginationDto, List<Seller>>> queryMap = initializeQueryMap();
        Function<PaginationDto, List<Seller>> queryFunction = queryMap.get(paginationDto.getPaginationType().getFilter());
        if (queryFunction == null) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El filtro proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        int count = sellerRepository.searchCount();
        List<Seller> list = queryFunction.apply(paginationDto);

        return new ResponseEntity<>(new CustomResponse<>(list, false, HttpStatus.OK.value(), "Lista de categorías obtenida correctamente.", count), HttpStatus.OK);
    }

    private ResponseEntity<Object> validatePaginationDto(PaginationDto paginationDto) {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado y paginación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        if (!(paginationDto.getPaginationType().getFilter().equals("curp") && paginationDto.getPaginationType().getSortBy().equals("curp")) &&
                !(paginationDto.getPaginationType().getFilter().equals("rfc") && paginationDto.getPaginationType().getSortBy().equals("rfc")) &&
                !(paginationDto.getPaginationType().getFilter().equals(REQUEST_STATUS) && paginationDto.getPaginationType().getSortBy().equals(REQUEST_STATUS)) &&
                !(paginationDto.getPaginationType().getFilter().equals(USER_NAME) && paginationDto.getPaginationType().getSortBy().equals(USER_NAME)) &&
                !(paginationDto.getPaginationType().getFilter().equals(USER_SURNAME) && paginationDto.getPaginationType().getSortBy().equals(USER_SURNAME))) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "Los datos de filtrado u ordenación proporcionados son inválidos. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc")) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, HttpStatus.BAD_REQUEST.value(), "El tipo de orden proporcionado es inválido. Por favor, verifica y envía la solicitud nuevamente.", 0), HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private Map<String, Function<PaginationDto, List<Seller>>> initializeQueryMap() {
        Map<String, Function<PaginationDto, List<Seller>>> queryMap = new HashMap<>();
        queryMap.put("curp", this::findAllByCurpPagination);
        queryMap.put("rfc", this::findAllByRfcPagination);
        queryMap.put(REQUEST_STATUS, this::findAllByRequestStatusPagination);
        queryMap.put(USER_NAME, this::findAllByUserNamePagination);
        queryMap.put(USER_SURNAME, this::findAllByUserSurnamePagination);
        return queryMap;
    }

    private List<Seller> findAllByCurpPagination(PaginationDto paginationDto) {
        return findAllByPagination(paginationDto, sellerRepository::findAllByCurpPagination);
    }

    private List<Seller> findAllByRfcPagination(PaginationDto paginationDto) {
        return findAllByPagination(paginationDto, sellerRepository::findAllByRfcPagination);
    }

    private List<Seller> findAllByRequestStatusPagination(PaginationDto paginationDto) {
        return findAllByPagination(paginationDto, sellerRepository::findAllByRequestStatusPagination);
    }

    private List<Seller> findAllByUserNamePagination(PaginationDto paginationDto) {
        return findAllByPagination(paginationDto, sellerRepository::findAllByUserNamePagination);
    }

    private List<Seller> findAllByUserSurnamePagination(PaginationDto paginationDto) {
        return findAllByPagination(paginationDto, sellerRepository::findAllByUserSurnamePagination);
    }

    private List<Seller> findAllByPagination(PaginationDto paginationDto, BiFunction<String, Pageable, List<Seller>> queryFunction) {
        Pageable pageable = PageRequest.of(
                paginationDto.getPaginationType().getPage(),
                paginationDto.getPaginationType().getLimit(),
                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending()
        );
        return queryFunction.apply(paginationDto.getValue(), pageable);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<Object> register(SellerDto seller, String jwtToken) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String username= jwtService.getUsernameFromToken(jwtToken);
        seller.setRfc(cryptoService.decrypt(seller.getRfc()));
        seller.setCurp(cryptoService.decrypt(seller.getCurp()));
        if (this.sellerRepository.existsByCurp(seller.getCurp())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El CURP ya se encuentra registrado en el sistema", 0), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByRfc(seller.getRfc())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El RFC ya se encuentra registrado en el sistema", 0), HttpStatus.BAD_REQUEST);
        }

        if (seller.getUser().getId() == null) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El id del usuario no puede ser nulo", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<User> userJwt = this.userRepository.findByUsername(username);

        Optional<User> user = this.userRepository.findById(seller.getUser().getId());

        if (user.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al usuario registrado dentro del sistema.", 0), HttpStatus.BAD_REQUEST);
        }

        if (userJwt.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al usuario del token registrado dentro del sistema.", 0), HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(userJwt.get().getId(), user.get().getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No puedes registrar un vendedor para otro usuario", 0), HttpStatus.BAD_REQUEST);
        }

        if (user.get().getRole().equals(Role.SELLER)) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No puedes registrar un vendedor si ya eres un vendedor", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Seller> sellerPending = this.sellerRepository.findSellerPending(user.get().getId());
        if (sellerPending.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "Ya existe una solicitud de vendedor pendiente", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Seller> sellerRejected = this.sellerRepository.findSellerRejected(user.get().getId());
        if (sellerRejected.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "Ya existe una solicitud de vendedor rechazada", 0), HttpStatus.BAD_REQUEST);
        }

        try {
            ValidateTypeFile validateTypeFile = new ValidateTypeFile();

            if(!validateTypeFile.isImageFile(seller.getImage())) {
                return new ResponseEntity<>(new CustomResponse<>(
                        null, true, 400, "El archivo debe ser de tipo imagen (JPEG, JPG, PNG)", 0
                ), HttpStatus.BAD_REQUEST);
            }

            UploadImage uploadImage = new UploadImage();
            String urlImage = uploadImage.uploadImage(seller.getImage(), seller.getRfc(), "sellers");

            Seller sellerToSave = new Seller();
            sellerToSave.setCurp(seller.getCurp());
            sellerToSave.setRfc(seller.getRfc());
            sellerToSave.setUser(user.get());
            sellerToSave.setImage(urlImage);
            sellerToSave.setStatus(true);
            sellerToSave.setRequest_status(PENDING);

            sellerToSave = this.sellerRepository.save(sellerToSave);

            return new ResponseEntity<>(new CustomResponse<>(sellerToSave, false, 201, "Vendedor registrado correctamente", 1), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CustomResponse<>(
                    null, true, 400, "Error al guardar al vendedor", 0
            ), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<Object> update(SellerDto seller, String jwtToken) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String username= jwtService.getUsernameFromToken(jwtToken);

        if (seller.getId() == null) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El id del vendedor no puede ser nulo", 0), HttpStatus.BAD_REQUEST);
        }

        if (seller.getUser().getId() == null) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El id del usuario del vendedor no puede ser nulo", 0), HttpStatus.BAD_REQUEST);
        }

        Optional<Seller> sellerOptional = this.sellerRepository.findById(seller.getId());

        seller.setRequest_status(seller.getRequest_status().toUpperCase());

        if (!sellerOptional.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al vendedor", 0), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByCurpAndAndIdNot(seller.getCurp(), seller.getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El CURP ya se encuentra registrado en el sistema", 0), HttpStatus.BAD_REQUEST);
        }

        if (this.sellerRepository.existsByRfcAndAndIdNot(seller.getRfc(), seller.getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El RFC ya se encuentra registrado en el sistema", 0), HttpStatus.BAD_REQUEST);
        }

        if (!seller.getRequest_status().equals(APPROVED) && !seller.getRequest_status().equals(REJECTED) && !seller.getRequest_status().equals(PENDING)) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "El estatus de la solicitud es inválido", 0), HttpStatus.BAD_REQUEST);
        }
        Optional<User> userJwt = this.userRepository.findByUsername(username);
        Optional<User> userOpt = this.userRepository.findById(seller.getUser().getId());

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró este usuario registrado dentro del sistema", 0), HttpStatus.BAD_REQUEST);
        }

        if (userJwt.isEmpty()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al usuario del token registrado dentro del sistema.", 0), HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(userJwt.get().getId(), userOpt.get().getId())) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No puedes actualizar un vendedor para otro usuario", 0), HttpStatus.BAD_REQUEST);
        }

        Seller sellerToUpdate = sellerOptional.get();
        sellerToUpdate.setCurp(cryptoService.decrypt( seller.getCurp()));
        sellerToUpdate.setRfc(cryptoService.decrypt(seller.getRfc()));
        sellerToUpdate.setRequest_status(seller.getRequest_status());
        sellerToUpdate.setUser(userOpt.get());

        sellerToUpdate = this.sellerRepository.save(sellerToUpdate);

        User user = userOpt.get();

        if (seller.getRequest_status().equals(APPROVED)) {
            user.setRole(Role.SELLER);
            this.userRepository.save(user);
            return new ResponseEntity<>(new CustomResponse<>(sellerToUpdate, false, 200, "Vendedor aprovado correctamente", 1), HttpStatus.OK);
        }

        if (seller.getRequest_status().equals(REJECTED) || seller.getRequest_status().equals(PENDING)) {
            user.setRole(Role.CUSTOMER);
            this.userRepository.save(user);
            return new ResponseEntity<>(new CustomResponse<>(sellerToUpdate, false, 200, "Vendedor rechazo correctamente", 1), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "Error al actualizar el vendedor", 0), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Object> changeStatus(SellerDto seller) {
        Optional<Seller> sellerOptional = this.sellerRepository.findById(seller.getId());

        if (!sellerOptional.isPresent()) {
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "No se encontró al vendedor", 0), HttpStatus.BAD_REQUEST);
        }

        Seller sellerToUpdate = sellerOptional.get();
        sellerToUpdate.setStatus(!sellerToUpdate.isStatus());

        sellerToUpdate = this.sellerRepository.save(sellerToUpdate);

        return new ResponseEntity<>(new CustomResponse<>(sellerToUpdate, false, 200, "Estatus del vendedor actualizado correctamente", 1), HttpStatus.OK);
    }
}
