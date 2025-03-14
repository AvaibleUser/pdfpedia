package org.cunoc.pdfpedia.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage.cloudinary")
public record CloudinaryProperty(
        String cloudName,
        String apiKey,
        String apiSecret) {
}
