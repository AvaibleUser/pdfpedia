package org.cunoc.pdfpedia.repository.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    Optional<?> findUnknownById(long id, Class<?> type);

    <U> Optional<U> findById(long id, Class<U> type);

    <U> Optional<U> findByEmail(String email, Class<U> type);

    long countByRole_NameAndCreatedAtBetween(String roleName, Instant startDate, Instant endDate);

    long countByRole_Name(String roleName);

    <T>List<T> findAllByRole_Name(String roleName, Class<T> type);

}
