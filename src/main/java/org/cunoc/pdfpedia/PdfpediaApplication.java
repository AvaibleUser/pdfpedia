package org.cunoc.pdfpedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PdfpediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfpediaApplication.class, args);
    }

}
