package org.cunoc.pdfpedia.service.user;

import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.user.AddUserDto;
import org.cunoc.pdfpedia.domain.dto.user.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {

    Optional<UserDto> findUserByEmail(String email);

    void registerUser(AddUserDto user, boolean dryRun);
}
