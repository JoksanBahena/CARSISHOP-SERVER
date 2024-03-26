package mx.edu.utez.carsishop.Config;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    public Cloudinary cloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "carsishop");
        config.put("api_key", "559447282286912");
        config.put("api_secret", "swEh-BrBrcuiCa4vni2G2mgI-7k");
        Cloudinary cloudinary = new Cloudinary(config);

        return cloudinary;
    }
}
