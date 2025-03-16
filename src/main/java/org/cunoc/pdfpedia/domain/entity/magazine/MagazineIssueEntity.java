package org.cunoc.pdfpedia.domain.entity.magazine;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "magazine_issue")
@Table(name = "magazine_issue", schema = "magazine_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class MagazineIssueEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String pdfUrl;

    @Column
    private boolean isDeleted;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "magazine_id", nullable = false)
    private MagazineEntity magazine;

    @CreationTimestamp
    @Column
    private Instant publishedAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
