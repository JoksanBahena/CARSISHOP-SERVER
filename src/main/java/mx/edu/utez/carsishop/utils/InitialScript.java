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

        // User creation
        Optional<User> userExist = userRepository.findByUsername("admin@gmail.com");
        if (!userExist.isPresent()) {
            User user = User.builder()
                    .username("admin@gmail.com")
                    .password(passwordEncoder.encode(adminPassword))
                    .name("admin")
                    .surname("admin valverde")
                    .phone("1234567890")
                    .birthdate("2021-01-01")
                    .status(true)
                    .role(Role.ADMIN)
                    .build();

            userRepository.saveAndFlush(user);
        }

        // Gender
        saveGender("Masculino");
        saveGender("Femenino");
        saveGender("Otro");

        // Size
        saveSize("S");
        saveSize("M");
        saveSize("L");
        saveSize("XL");
        saveSize("XXL");

        // Category
        saveCategory("Hombre");
        saveCategory("Mujer");
        saveCategory("Niño");

        // Subcategory
        saveSubcategory("Top");
        saveSubcategory("Bottom");
        saveSubcategory("Shoes");
        saveSubcategory("Accesories");
    }

    private void saveGender(String genderName) {
        Optional<Gender> genderExist = genderRepository.findByGender(genderName);
        if (genderExist.isEmpty()) {
            Gender gender = new Gender();
            gender.setGender(genderName);
            genderRepository.saveAndFlush(gender);
        }
    }

    private void saveSize(String sizeName) {
        Optional<Size> sizeExist = sizeRepository.findByName(sizeName);
        if (sizeExist.isEmpty()) {
            Size size = new Size();
            size.setName(sizeName);
            sizeRepository.saveAndFlush(size);
        }
    }

    private void saveCategory(String categoryName) {
        Optional<Category> categoryExist = categoryRepository.findByName(categoryName);
        if (categoryExist.isEmpty()) {
            Category category = new Category();
            category.setName(categoryName);
            category.setStatus(true);
            categoryRepository.saveAndFlush(category);
        }
    }

    private void saveSubcategory(String subcategoryName) {
        Optional<Subcategory> subcategoryExist = subcaregoryRepository.findByName(subcategoryName);
        if (subcategoryExist.isEmpty()) {
            Subcategory subcategory = new Subcategory();
            subcategory.setName(subcategoryName);
            subcategory.setStatus(true);
            subcaregoryRepository.saveAndFlush(subcategory);
        }
    }

}
