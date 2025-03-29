package org.cunoc.pdfpedia.service.admin.export;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.service.util.ThymeleafService;
import org.springframework.stereotype.Service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final ThymeleafService thymeleafService;
    private final PdfGeneratorService pdfGeneratorService;

    public ResponseEntity<Resource> downloadPdf(String templateName, Map<String, Object> templateVariables){
        String billHtml = thymeleafService.renderTemplate(templateName, templateVariables);
        try {
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("pdf-test.pdf")
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.badRequest().build();
        }
    }

    public byte[]  generatePdf(String templateName, Map<String, Object> templateVariables){
        String billHtml = thymeleafService.renderTemplate(templateName, templateVariables);
        try {
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return resource.getByteArray();
        }catch (Exception e){
            return null;
        }
    }

}
