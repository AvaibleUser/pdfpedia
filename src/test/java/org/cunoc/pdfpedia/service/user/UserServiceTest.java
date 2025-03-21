package org.cunoc.pdfpedia.service.user;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.user.AddUserDto;
import org.cunoc.pdfpedia.domain.dto.user.UserDto;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;
import org.cunoc.pdfpedia.domain.entity.user.RoleEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.RequestConflictException;
import org.cunoc.pdfpedia.repository.user.RoleRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void canLoadUserByUsername() {
        // given
        String email = "why@not.com";
        UserEntity expectedUser = UserEntity.builder()
                .username("why")
                .email(email)
                .password("whynot")
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .profile(ProfileEntity.builder().firstname("why").lastname("not").build())
                .wallet(new WalletEntity(BigDecimal.ZERO))
                .build();

        given(userRepository.findByEmail(email, UserEntity.class))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        // when
        UserDetails actualUser = userService.loadUserByUsername(email);

        // then
        then(actualUser).usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    void cantLoadUserByUsername_WhenUserNotFound() {
        // given
        String email = "this@is.tedious";

        given(userRepository.findByEmail(email, UserEntity.class))
                .willReturn(Optional.empty());

        // when
        catchThrowableOfType(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));

        // then
        // nothing to do
    }

    @Test
    void canFindUserByEmail() {
        // given
        String email = "whithout@email.com";
        UserDto expectedUser = UserDto.builder()
                .username("whithout username")
                .email(email)
                .build();

        given(userRepository.findByEmail(email, UserDto.class))
                .willReturn(Optional.of(expectedUser.toBuilder().build()));

        // when
        Optional<UserDto> actualUser = userService.findUserByEmail(email);

        // then
        then(actualUser).get()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void cantRegisterUser_WhenDryRunIsTrue() {
        // given
        long roleId = 2000L;
        String email = "add@user.com";
        String password = "this is a password";
        AddUserDto user = AddUserDto.builder()
                .firstname("this is a firstname")
                .lastname("this is a lastname")
                .username("this is not a username")
                .email(email)
                .password(password)
                .roleId(roleId)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(roleRepository.existsById(roleId)).willReturn(true);

        // when
        userService.registerUser(user, true);

        // then
        BDDMockito.then(userRepository).should().existsByEmail(email);
        BDDMockito.then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void canRegisterUser_WhenDryRunIsFalse() {
        // given
        long roleId = 2000L;
        String email = "add@user.com";
        String encripted = "encrypted password";
        String password = "this is a password";
        String username = "this is not a username";
        String firstname = "this is a firstname";
        String lastname = "this is a lastname";
        AddUserDto user = AddUserDto.builder()
                .firstname(firstname)
                .lastname(lastname)
                .username(username)
                .email(email)
                .password(password)
                .roleId(roleId)
                .build();

        RoleEntity role = RoleEntity.builder().id(roleId).name("USER").build();
        UserEntity expectedUser = UserEntity.builder()
                .username(username)
                .email(email)
                .password(encripted)
                .role(role)
                .profile(ProfileEntity.builder().firstname(firstname).lastname(lastname).build())
                .wallet(WalletEntity.builder().balance(BigDecimal.ZERO).build())
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(roleRepository.existsById(roleId)).willReturn(true);
        given(roleRepository.findById(roleId)).willReturn(Optional.of(role.toBuilder().build()));
        given(encoder.encode(password)).willReturn(encripted);

        // when
        userService.registerUser(user, false);

        // then
        BDDMockito.then(userRepository).should().save(refEq(expectedUser));
    }

    @Test
    void cantRegisterUser_WhenUserAlreadyExists() {
        // given
        long roleId = 2000L;
        String email = "add@user.com";
        String password = "this is a password";
        AddUserDto user = AddUserDto.builder()
                .firstname("this is a firstname")
                .lastname("this is a lastname")
                .username("this is not a username")
                .email(email)
                .password(password)
                .roleId(roleId)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(true);
        given(roleRepository.existsById(roleId)).willReturn(true);

        // when
        catchThrowableOfType(RequestConflictException.class, () -> userService.registerUser(user, false));

        // then
        BDDMockito.then(userRepository).should().existsByEmail(email);
        BDDMockito.then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void cantRegisterUser_WhenRoleDoesntExist() {
        // given
        long roleId = 2000L;
        String email = "add@user.com";
        String password = "this is a password";
        AddUserDto user = AddUserDto.builder()
                .firstname("this is a firstname")
                .lastname("this is a lastname")
                .username("this is not a username")
                .email(email)
                .password(password)
                .roleId(roleId)
                .build();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(roleRepository.existsById(roleId)).willReturn(false);

        // when
        catchThrowableOfType(RequestConflictException.class, () -> userService.registerUser(user, false));

        // then
        BDDMockito.then(userRepository).should().existsByEmail(email);
        BDDMockito.then(roleRepository).should().existsById(roleId);
        BDDMockito.then(userRepository).shouldHaveNoMoreInteractions();
    }
}
