package org.cunoc.pdfpedia.controller.user;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.role.RoleDto;
import org.cunoc.pdfpedia.service.user.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @Test
    void canGetRoles() {
        // given
        List<RoleDto> expectedRoles = List.of(
                RoleDto.builder()
                        .id(1L)
                        .name("ADMIN")
                        .build(),
                RoleDto.builder()
                        .id(2L)
                        .name("USER")
                        .build());

        given(roleService.findElegibleRoles()).willReturn(expectedRoles);

        // when
        List<RoleDto> actualRoles = roleController.getRoles();

        // then
        BDDAssertions.then(actualRoles)
                .usingRecursiveComparison()
                .isEqualTo(expectedRoles);
    }
}
