package org.cunoc.pdfpedia.config;

import org.cunoc.pdfpedia.config.property.CloudinaryProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class StorageConfig {

    @Bean
    Cloudinary cloudinary(CloudinaryProperty property) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", property.cloudName(),
                "api_key", property.apiKey(),
                "api_secret", property.apiSecret()));
    }
}
