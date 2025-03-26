package org.cunoc.pdfpedia.repository.user;

import java.util.List;

import org.cunoc.pdfpedia.domain.entity.user.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    <T> List<T> findAllRolesByNameNot(String name, Class<T> type);
}
