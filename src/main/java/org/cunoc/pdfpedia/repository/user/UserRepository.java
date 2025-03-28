package org.cunoc.pdfpedia.repository.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM user a
    WHERE a.role.name != 'ADMIN' AND a.createdAt BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countRegisterByMonthByBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);



    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM user a
    WHERE a.role.name != 'ADMIN'
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countRegisterByMonth();



}
