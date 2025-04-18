package org.cunoc.pdfpedia.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.cunoc.pdfpedia.config.property.RsaProperty;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.user.AuthManager;
import org.cunoc.pdfpedia.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class AuthConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtDecoder jwtDecoder,
            UserService userDetailsService) throws Exception {
        return httpSecurity
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v1/roles").permitAll()
                        .requestMatchers("/v1/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtCustomizer -> jwtCustomizer
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(this::convertJwtToAuthentication)))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(PUT);
        config.addAllowedMethod(PATCH);
        config.addAllowedMethod(DELETE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(UserRepository userRepository) {
        return new AuthManager(userRepository, passwordEncoder(), signUpCondifmationCodes());
    }

    @Bean
    JwtDecoder jwtDecoder(RsaProperty rsaProperty) {
        return NimbusJwtDecoder.withPublicKey(rsaProperty.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(RsaProperty rsaProperty) {
        JWK jwk = new RSAKey.Builder(rsaProperty.publicKey())
                .privateKey(rsaProperty.privateKey())
                .build();

        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    GoogleAuthenticator googleAuthenticator() {
        return new GoogleAuthenticator();
    }

    @Bean
    ConcurrentMap<String, String> signUpCondifmationCodes() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    private AbstractAuthenticationToken convertJwtToAuthentication(Jwt jwt) {
        long id = Long.parseLong(jwt.getSubject());
        List<SimpleGrantedAuthority> authorities = jwt.getClaimAsStringList("role")
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new JwtAuthenticationToken(jwt, authorities, String.valueOf(id));
    }
}
