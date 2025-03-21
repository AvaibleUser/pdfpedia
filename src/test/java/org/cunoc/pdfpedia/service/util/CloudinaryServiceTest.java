package org.cunoc.pdfpedia.service.util;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Map;

import org.cunoc.pdfpedia.domain.exception.RequestConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile file;

    @Mock
    private Uploader uploader;

    @Mock
    private Url url;

    @Test
    void canUploadFile() throws IOException {
        // given
        byte[] expectedBytes = "file content".getBytes();
        String expectedPublicId = "cloudinary-public-id";
        String expectedPublicUrl = "el-archivo.pdf";

        given(cloudinary.uploader()).willReturn(uploader);
        given(cloudinary.url()).willReturn(url);
        given(file.getBytes()).willReturn(expectedBytes);
        given(uploader.upload(expectedBytes, Map.of())).willReturn(Map.of("public_id", expectedPublicId));
        given(url.secure(true)).willReturn(url);
        given(url.generate(expectedPublicId)).willReturn(expectedPublicUrl);

        // when
        String actualPublicUrl = cloudinaryService.uploadFile(file);

        // then
        then(actualPublicUrl).isEqualTo(expectedPublicUrl);
    }

    @Test
    void cantUploadFile_WhenFileBytesThrowsException() throws IOException {
        // given
        given(file.getBytes()).willThrow(new IOException());

        // when
        catchThrowableOfType(RequestConflictException.class, () -> cloudinaryService.uploadFile(file));

        // then
        // nothing to do
    }
}
