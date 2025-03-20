package org.cunoc.pdfpedia.service.user;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.warrenstrange.googleauth.GoogleAuthenticator;

@ExtendWith(MockitoExtension.class)
public class TotpServiceTest {

    @Captor
    private ArgumentCaptor<String> totpCaptor;

    @Spy
    private GoogleAuthenticator googleAuth = new GoogleAuthenticator();

    @Spy
    private ConcurrentMap<String, String> emailConfirmationCodes = new ConcurrentHashMap<>();

    @InjectMocks
    private TotpService totpService;

    @Test
    void generateConfirmCode() {
        // given
        String email = "this@is.a.email";

        // when
        String actualCode = totpService.generateConfirmCode(email);

        // then
        BDDMockito.then(emailConfirmationCodes).should().put(eq(email), totpCaptor.capture());
        then(actualCode).isEqualTo(totpCaptor.getValue());
    }

    @Test
    void confirmCode() {
        // given
        String email = "this@is.a.email";
        String code = "this is a code";

        emailConfirmationCodes.put(email, code);

        // when
        boolean actual = totpService.confirmCode(email, code);

        // then
        BDDMockito.then(emailConfirmationCodes).should().remove(email, code);
        then(actual).isTrue();
    }

    @Test
    void existsCode() {
        // given
        String email = "this@is.a.email";
        String code = "this is a code";

        emailConfirmationCodes.put(email, code);

        // when
        boolean actual = totpService.existsCode(email, code);

        // then
        BDDMockito.then(emailConfirmationCodes).should().get(eq(email));
        then(actual).isTrue();
    }
}
