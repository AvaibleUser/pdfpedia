package org.cunoc.pdfpedia.controller.user;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

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
import org.cunoc.pdfpedia.service.user.ICodesService;
import org.cunoc.pdfpedia.service.user.IUserService;
import org.cunoc.pdfpedia.service.util.IEmailService;
import org.cunoc.pdfpedia.service.util.ITemplateService;
import org.cunoc.pdfpedia.service.util.ITokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final ICodesService codesService;
    private final IEmailService emailService;
    private final ITemplateService templateRendererService;
    private final AuthenticationManager authManager;

    private TokenDto toTokenDto(UserDto user) {
        String token = tokenService.generateToken(user.id(), List.of(user.roleName()));
        return new TokenDto(token, user);
    }

    @PostMapping("/sign-up")
    @ResponseStatus(CREATED)
    public void signUp(@RequestBody @Valid AddUserDto user) {
        userService.registerUser(user, true);

        String code = codesService.generateConfirmCode(user.email());
        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", user);
        String confirmationHtml = templateRendererService.renderTemplate("sign-up-confirmation", templateVariables);

        try {
            emailService.sendHtmlEmail("RevistLand", user.email(),
                    "Confirmacion de usuario en RevistLand", confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion");
        }
        userService.registerUser(user, false);
    }

    @PutMapping("/sign-up")
    public TokenDto confirmSignUp(@RequestBody @Valid ConfirmUserDto user) {
        boolean confirmed = codesService.confirmCode(user.email(),
                user.code());
        if (!confirmed) {
            throw new FailedAuthenticateException("No se pudo confirmar la cuenta");
        }

        return userService.findUserByEmail(user.email())
                .map(this::toTokenDto)
                .orElseThrow(() -> new InsufficientAuthenticationException("No se encontro el registro del usuario"));
    }

    @PostMapping("/sign-in")
    public Optional<TokenDto> signIn(@RequestBody @Valid AuthUserDto user) {
        var authenticableUser = unauthenticated(user.email(), user.password());
        authManager.authenticate(authenticableUser);

        return userService.findUserByEmail(user.email())
                .map(this::toTokenDto);
    }
}
