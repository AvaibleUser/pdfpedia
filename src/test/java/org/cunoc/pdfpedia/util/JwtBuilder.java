package org.cunoc.pdfpedia.util;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

public class JwtBuilder {

    public static JwtRequestPostProcessor jwt(long userId, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(b -> b.subject(String.valueOf(userId)).claim("role", roles));
    }
}
