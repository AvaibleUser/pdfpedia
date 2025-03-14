package org.cunoc.pdfpedia.service.util;

import java.io.IOException;
import java.util.Map;

import org.cunoc.pdfpedia.domain.exception.RequestConflictException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements IStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map<?, ?> uploadedFile = cloudinary.uploader().upload(file.getBytes(), Map.of());
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (IOException e) {
            throw new RequestConflictException("El archivo no se pudo subir, intente subirlo de nuevo");
        }
    }
}
