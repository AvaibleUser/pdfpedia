package org.cunoc.pdfpedia.service.util;

import java.time.Instant;
import java.util.Collection;

import org.cunoc.pdfpedia.config.property.TokenProperty;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService implements ITokenService {

    private final JwtEncoder jwtEncoder;
    private final TokenProperty tokenProperty;

    public <T> String generateToken(long userId, Collection<T> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(tokenProperty.expirationTime(), tokenProperty.timeUnit()))
                .subject(String.valueOf(userId))
                .claim("role", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
