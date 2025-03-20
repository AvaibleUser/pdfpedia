package org.cunoc.pdfpedia.service.user;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email, UserEntity.class)
                .orElseThrow(() -> new UsernameNotFoundException("No se pudo encontrar al usuario"));
    }

    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email, UserDto.class);
    }

    @Transactional
    public void registerUser(AddUserDto user, boolean dryRun) {
        if (userRepository.existsByEmail(user.email())) {
            throw new RequestConflictException("El email que se intenta registrar ya esta en uso");
        }
        if (!roleRepository.existsById(user.roleId())) {
            throw new RequestConflictException("El rol que se intenta registrar no existe");
        }
        if (dryRun) {
            return;
        }
        String encryptedPassword = encoder.encode(user.password());
        RoleEntity role = roleRepository.findById(user.roleId()).get();

        ProfileEntity profile = ProfileEntity.builder()
                .firstname(user.firstname())
                .lastname(user.lastname())
                .hobbies(user.hobbies())
                .description(user.description())
                .interestsTopics(user.interestsTopics())
                .build();

        WalletEntity wallet = WalletEntity.builder()
                .balance(BigDecimal.ZERO)
                .build();

        UserEntity newUser = UserEntity.builder()
                .username(user.username())
                .email(user.email())
                .password(encryptedPassword)
                .role(role)
                .profile(profile)
                .wallet(wallet)
                .build();

        profile.setUser(newUser);
        wallet.setUser(newUser);

        userRepository.save(newUser);
    }
}
