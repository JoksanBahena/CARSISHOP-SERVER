package mx.edu.utez.carsishop.utils;

import mx.edu.utez.carsishop.models.category.Category;
import mx.edu.utez.carsishop.models.category.CategoryRepository;
import mx.edu.utez.carsishop.models.gender.Gender;
import mx.edu.utez.carsishop.models.gender.GenderRepository;
import mx.edu.utez.carsishop.models.size.Size;
import mx.edu.utez.carsishop.models.size.SizeRepository;
import mx.edu.utez.carsishop.models.subcategory.SubcaregoryRepository;
import mx.edu.utez.carsishop.models.subcategory.Subcategory;
import mx.edu.utez.carsishop.models.user.Role;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialScript implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenderRepository genderRepository;
    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final SubcaregoryRepository subcaregoryRepository;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Autowired
    public InitialScript(UserRepository userRepository, PasswordEncoder passwordEncoder, GenderRepository genderRepository, SizeRepository sizeRepository, CategoryRepository categoryRepository, SubcaregoryRepository subcaregoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.genderRepository = genderRepository;
        this.sizeRepository = sizeRepository;
        this.categoryRepository = categoryRepository;
        this.subcaregoryRepository = subcaregoryRepository;
    }

    @Override
    public void run(String... args)  {

        Optional<User> userExist = userRepository.findByUsername("admin@gmail.com");
        if (!userExist.isPresent()) {
            User user = User.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode( adminPassword))
                    .name("admin")
                    .surname("admin valverde")
                    .phone("1234567890")
                    .birthdate("2021-01-01")
                    .status(true)
                    .role(Role.ADMIN)
                    .build();

            userRepository.saveAndFlush(user);
        }

        //Gender
        Gender gender = new Gender();
        Optional<Gender> genderExist;

        genderExist = genderRepository.findByGender("Masculino");
        if (!genderExist.isPresent()) {
            gender.setGender("Masculino");
            gender.setId(1);
            genderRepository.saveAndFlush(gender);
        }

        genderExist = genderRepository.findByGender("Femenino");
        if (!genderExist.isPresent()){
            gender.setGender("Femenino");
            gender.setId(2);
            genderRepository.saveAndFlush(gender);
        }

        genderExist = genderRepository.findByGender("Otro");
        if (!genderExist.isPresent()) {
            gender.setGender("Otro");
            gender.setId(3);
            genderRepository.saveAndFlush(gender);
        }

        //Size
        Size size = new Size();
        Optional<Size> sizeExist;
        sizeExist = sizeRepository.findByName("S");
        if (!sizeExist.isPresent()) {
            size.setName("S");
            sizeRepository.saveAndFlush(size);
        }

        sizeExist = sizeRepository.findByName("M");
        if (!sizeExist.isPresent()) {
            size.setName("M");
            sizeRepository.saveAndFlush(size);
        }

        sizeExist = sizeRepository.findByName("L");
        if (!sizeExist.isPresent()) {
            size.setName("L");
            sizeRepository.saveAndFlush(size);
        }

        sizeExist = sizeRepository.findByName("XL");
        if (!sizeExist.isPresent()) {
            size.setName("XL");
            sizeRepository.saveAndFlush(size);
        }

        sizeExist = sizeRepository.findByName("XXL");
        if (!sizeExist.isPresent()) {
            size.setName("XXL");
            sizeRepository.saveAndFlush(size);
        }

        //Category
        Category category = new Category();
        Optional<Category> categoryExist;

        categoryExist = categoryRepository.findByName("Hombre");
        if (!categoryExist.isPresent()) {
            category.setStatus(true);
            category.setName("Hombre");
            categoryRepository.saveAndFlush(category);
        }

        categoryExist = categoryRepository.findByName("Mujer");
        if (!categoryExist.isPresent()) {
            category.setStatus(true);
            category.setName("Mujer");
            categoryRepository.saveAndFlush(category);
        }

        categoryExist = categoryRepository.findByName("Niño");
        if (!categoryExist.isPresent()) {
            category.setName("Niño");
            category.setStatus(true);
            categoryRepository.saveAndFlush(category);
        }

        //Subcategory
        Subcategory subcategory = new Subcategory();
        Optional<Subcategory> subcategoryExist;

        subcategoryExist = subcaregoryRepository.findByName("Top");
        if (!subcategoryExist.isPresent()) {
            subcategory.setName("Top");
            subcategory.setStatus(true);
            subcaregoryRepository.saveAndFlush(subcategory);
        }

        subcategoryExist = subcaregoryRepository.findByName("Bottom");
        if (!subcategoryExist.isPresent()) {
            subcategory.setName("Bottom");
            subcategory.setStatus(true);
            subcaregoryRepository.saveAndFlush(subcategory);
        }

        subcategoryExist = subcaregoryRepository.findByName("Shoes");
        if (!subcategoryExist.isPresent()) {
            subcategory.setStatus(true);
            subcategory.setName("Shoes");
            subcaregoryRepository.saveAndFlush(subcategory);
        }

        subcategoryExist = subcaregoryRepository.findByName("Accesories");
        if (!subcategoryExist.isPresent()) {
            subcategory.setStatus(true);
            subcategory.setName("Accesories");
            subcaregoryRepository.saveAndFlush(subcategory);
        }


    }

}
