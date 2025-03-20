package org.cunoc.pdfpedia.service.user;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;
import org.cunoc.pdfpedia.domain.entity.user.RoleEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Spy
    private ConcurrentMap<String, String> signUpConfirmationCodes = new ConcurrentHashMap<>();

    @InjectMocks
    private AuthManager authManager;

    @Test
    void authenticate() {
        // given
        String email = "this@is.a.email";
        String password = "this is a encrypted password";
        var auth = new UsernamePasswordAuthenticationToken(email, password);
        UserEntity user = UserEntity.builder()
                .username("this is a username")
                .email(email)
                .password(password)
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .profile(ProfileEntity.builder()
                        .firstname("this is a firstname")
                        .lastname("this is a lastname")
                        .build())
                .wallet(new WalletEntity(BigDecimal.ZERO))
                .build();

        given(encoder.matches(password, user.getPassword())).willReturn(true);
        given(userRepository.findByEmail(email, UserEntity.class))
                .willReturn(Optional.of(user.toBuilder().build()));

        // when
        Authentication actualAuth = authManager.authenticate(auth);

        // then
        then(actualAuth)
                .satisfies(a -> then(a.getPrincipal()).isEqualTo(email))
                .satisfies(a -> then(a.getCredentials()).isEqualTo(password))
                .satisfies(a -> then(a.getAuthorities())
                        .hasSize(1)
                        .first()
                        .isEqualTo(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void authenticate_WhenSignUpConfirmationCodeExists() {
        // given
        String email = "this@is.a.email";
        String password = "this is a encrypted password";
        String code = "signup confirmation code";
        var auth = new UsernamePasswordAuthenticationToken(email, password);

        signUpConfirmationCodes.put(email, code);

        // when
        catchThrowableOfType(InsufficientAuthenticationException.class, () -> authManager.authenticate(auth));

        // then
        BDDMockito.then(signUpConfirmationCodes).should().containsKey(email);
    }

    @Test
    void authenticate_WhenUserNotFound() {
        // given
        String email = "this@is.a.email";
        String password = "this is a encrypted password";
        var auth = new UsernamePasswordAuthenticationToken(email, password);

        given(userRepository.findByEmail(email, UserEntity.class)).willReturn(Optional.empty());

        // when
        catchThrowableOfType(BadCredentialsException.class, () -> authManager.authenticate(auth));

        // then
        BDDMockito.then(userRepository).should().findByEmail(email, UserEntity.class);
    }
}
