package org.cunoc.pdfpedia.repository.user;

import java.time.Instant;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.entity.user.RoleEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    <U> Optional<U> findByEmail(String email, Class<U> type);

    long countByRole_NameAndCreatedAtBetween(String roleName, Instant startDate, Instant endDate);

    long countByRole_Name(String roleName);

}
