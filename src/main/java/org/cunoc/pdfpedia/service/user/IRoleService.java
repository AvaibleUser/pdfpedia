package org.cunoc.pdfpedia.service.user;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.role.RoleDto;

public interface IRoleService {

    List<RoleDto> findElegibleRoles();
}
