package org.cunoc.pdfpedia.service.user;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCodesService {

    private final GoogleAuthenticator googleAuth;
    private final ConcurrentMap<String, String> emailConfirmationCodes;

    public String generateEmailConfirmationCode(String email) {
        GoogleAuthenticatorKey credentials = googleAuth.createCredentials();
        String code = String.format("%06d", googleAuth.getTotpPassword(credentials.getKey()));
        emailConfirmationCodes.put(email, code);
        return code;
    }

    public boolean confirmUserEmailCode(String email, String code) {
        return emailConfirmationCodes.remove(email, code);
    }

    public boolean existsUserEmailCode(String email, String code) {
        return Objects.equals(emailConfirmationCodes.get(email), code);
    }
}
