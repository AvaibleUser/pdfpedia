package org.cunoc.pdfpedia.service.user;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.role.RoleDto;
import org.cunoc.pdfpedia.repository.user.RoleRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> findElegibleRoles() {
        return roleRepository.findAllRolesByNameNot("ADMIN", RoleDto.class);
    }
}
