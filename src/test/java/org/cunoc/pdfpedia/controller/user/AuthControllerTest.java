package org.cunoc.pdfpedia.controller.user;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.TokenDto;
import org.cunoc.pdfpedia.domain.dto.user.AddUserDto;
import org.cunoc.pdfpedia.domain.dto.user.AuthUserDto;
import org.cunoc.pdfpedia.domain.dto.user.ConfirmUserDto;
import org.cunoc.pdfpedia.domain.dto.user.UserDto;
import org.cunoc.pdfpedia.domain.exception.FailedAuthenticateException;
import org.cunoc.pdfpedia.domain.exception.RequestConflictException;
import org.cunoc.pdfpedia.service.user.TotpService;
import org.cunoc.pdfpedia.service.user.UserService;
import org.cunoc.pdfpedia.service.util.JavaMailService;
import org.cunoc.pdfpedia.service.util.ThymeleafService;
import org.cunoc.pdfpedia.service.util.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import jakarta.mail.MessagingException;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Captor
    private ArgumentCaptor<Authentication> authCaptor;

    @Mock
    private UserService userService;

    @Mock
    private JwtService tokenService;

    @Mock
    private TotpService codesService;

    @Mock
    private JavaMailService emailService;

    @Mock
    private ThymeleafService templateRendererService;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthController authController;

    @Test
    void canSignUp() throws MessagingException {
        // given
        String code = "123456";
        String template = "sign-up-confirmation";
        String expectedHtml = "<html></html>";
        String expectedCompanyName = "RevistLand";
        AddUserDto expectedUser = AddUserDto.builder()
                .username("boring")
                .email("this@is.boring")
                .password("duuuuuh")
                .firstname("boring")
                .lastname("test No. 1")
                .roleId(1L)
                .build();

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", expectedUser);

        given(codesService.generateConfirmCode(expectedUser.email())).willReturn(code);
        given(templateRendererService.renderTemplate(eq(template), refEq(templateVariables))).willReturn(expectedHtml);

        // when
        authController.signUp(expectedUser.toBuilder().build());

        // then
        BDDMockito.then(userService).should().registerUser(expectedUser, true);
        BDDMockito.then(userService).should().registerUser(expectedUser, false);
        BDDMockito.then(emailService).should().sendHtmlEmail(eq(expectedCompanyName), eq(expectedUser.email()),
                anyString(), eq(expectedHtml));
    }

    @Test
    void cantSignUp_WhenMessageException() throws MessagingException {
        // given
        String code = "123456";
        String template = "sign-up-confirmation";
        String expectedHtml = "<html></html>";
        String expectedCompanyName = "RevistLand";
        AddUserDto expectedUser = AddUserDto.builder()
                .username("boring")
                .email("this@is.boring")
                .password("duuuuuh")
                .firstname("boring")
                .lastname("test No. 1")
                .roleId(1L)
                .build();

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", expectedUser);

        given(codesService.generateConfirmCode(expectedUser.email())).willReturn(code);
        given(templateRendererService.renderTemplate(eq(template), refEq(templateVariables))).willReturn(expectedHtml);
        willThrow(new MessagingException("No se pudo enviar el correo de confirmacion")).given(emailService)
                .sendHtmlEmail(eq(expectedCompanyName), eq(expectedUser.email()), anyString(), eq(expectedHtml));

        // when
        catchThrowableOfType(RequestConflictException.class,
                () -> authController.signUp(expectedUser.toBuilder().build()));

        // then
        BDDMockito.then(userService).should().registerUser(expectedUser, true);
        BDDMockito.then(userService).shouldHaveNoMoreInteractions();
        BDDMockito.then(emailService).should().sendHtmlEmail(eq(expectedCompanyName), eq(expectedUser.email()),
                anyString(), eq(expectedHtml));
    }

    @Test
    void canConfirmSignUp() {
        // given
        long userId = 24L;
        String roleName = "PRESIDENT";
        String expectedToken = "total permission";
        ConfirmUserDto user = new ConfirmUserDto("this@is.tedious", "123456");
        UserDto expectedUserDto = UserDto.builder()
                .id(userId)
                .username("boring")
                .email("this@is.boring")
                .roleName(roleName)
                .build();

        given(codesService.confirmCode(user.email(), user.code())).willReturn(true);
        given(userService.findUserByEmail(user.email())).willReturn(Optional.of(expectedUserDto.toBuilder().build()));
        given(tokenService.generateToken(userId, List.of(roleName))).willReturn(expectedToken);

        // when
        TokenDto actualToken = authController.confirmSignUp(user);

        // then
        then(actualToken).satisfies(t -> then(t.token()).isEqualTo(expectedToken))
                .extracting(TokenDto::user)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserDto);
    }

    @Test
    void cantConfirmSignUp_WhenCodeNotFound() {
        // given
        ConfirmUserDto user = new ConfirmUserDto("this@is.tedious", "123456");

        given(codesService.confirmCode(user.email(), user.code())).willReturn(false);

        // when
        catchThrowableOfType(FailedAuthenticateException.class,
                () -> authController.confirmSignUp(user));

        // then
        BDDMockito.then(userService).shouldHaveNoInteractions();
        BDDMockito.then(tokenService).shouldHaveNoInteractions();
    }

    @Test
    void cantConfirmSignUp_WhenUserNotFound() {
        // given
        ConfirmUserDto user = new ConfirmUserDto("this@is.tedious", "123456");

        given(codesService.confirmCode(user.email(), user.code())).willReturn(true);
        given(userService.findUserByEmail(user.email())).willReturn(Optional.empty());

        // when
        catchThrowableOfType(AuthenticationException.class,
                () -> authController.confirmSignUp(user));

        // then
        BDDMockito.then(userService).should().findUserByEmail(user.email());
        BDDMockito.then(tokenService).shouldHaveNoInteractions();
    }

    @Test
    void canSignIn() {
        // given
        long userId = 24L;
        String roleName = "ADMIN";
        String expectedToken = "total permission";
        String password = "this is secure";
        var expectedAuthentication = new UsernamePasswordAuthenticationToken("this@is.boring", password);
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username("boring")
                .email("this@is.boring")
                .roleName(roleName)
                .build();

        AuthUserDto authUser = new AuthUserDto(expectedUser.email(), password);

        given(authManager.authenticate(authCaptor.capture())).willReturn(mock(Authentication.class));
        given(tokenService.generateToken(userId, List.of(roleName))).willReturn(expectedToken);
        given(userService.findUserByEmail(expectedUser.email()))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        // when
        Optional<TokenDto> actualToken = authController.signIn(authUser);

        // then
        then(authCaptor.getValue()).isEqualTo(expectedAuthentication);
        then(actualToken).get().satisfies(t -> then(t.token()).isEqualTo(expectedToken))
                .extracting(TokenDto::user)
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }
}
