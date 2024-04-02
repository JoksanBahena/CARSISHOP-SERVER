package mx.edu.utez.carsishop.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mx.edu.utez.carsishop.Config.CloudinaryConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class UploadImage {
    private final CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();

    public String uploadImage(MultipartFile image, String name, String folder) throws IOException {
        Cloudinary cloudinary = cloudinaryConfig.cloudinary();

        try {
            Map<?, ?> result = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("folder", "carsishop/" + folder + "/" + name, "public_id", name));

            return (String) result.get("url");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
