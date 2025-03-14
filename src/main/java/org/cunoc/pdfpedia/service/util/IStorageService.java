package org.cunoc.pdfpedia.service.util;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    /**
     * Stores the file in the implemented option
     * @param file that contains the information to save
     * @return the url where the file was saved
     */
    String uploadFile(MultipartFile file);
}
