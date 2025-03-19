package org.cunoc.pdfpedia.controller.user;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.role.RoleDto;
import org.cunoc.pdfpedia.service.user.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleDto> getRoles() {
        return roleService.findElegibleRoles();
    }
}
