package org.cunoc.pdfpedia.service.user;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.role.RoleDto;
import org.cunoc.pdfpedia.repository.user.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void canFindElegibleRoles() {
        // given
        List<RoleDto> expectedRoles = List.of(
                RoleDto.builder()
                        .id(1L)
                        .name("NOT_ADMIN")
                        .build(),
                RoleDto.builder()
                        .id(2L)
                        .name("ANOTHER_NOT_ADMIN")
                        .build());

        given(roleRepository.findAllRolesByNameNot("ADMIN", RoleDto.class)).willReturn(expectedRoles);

        // when
        List<RoleDto> actualRoles = roleService.findElegibleRoles();

        // then
        BDDAssertions.then(actualRoles)
                .usingRecursiveComparison()
                .isEqualTo(expectedRoles);
    }
}
