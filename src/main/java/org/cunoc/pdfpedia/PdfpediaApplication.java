package org.cunoc.pdfpedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource("file:${user.dir}/.env")
public class PdfpediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfpediaApplication.class, args);
    }

}
