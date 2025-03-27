package org.cunoc.pdfpedia.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles("test")
@Inherited
@Documented
@SpringBootTest
@AutoConfigureCache
@AutoConfigureMockMvc
public @interface ApiTest {

}
