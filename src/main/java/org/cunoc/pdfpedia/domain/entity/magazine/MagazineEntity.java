package org.cunoc.pdfpedia.domain.entity.magazine;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "magazine")
@Table(name = "magazine", schema = "magazine_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class MagazineEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String title;

    @NonNull
    @Column(nullable = false)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal costPerDay;

    @Column
    private LocalDate adBlockingExpirationDate;

    @Column
    private boolean disableLikes;

    @Column
    private boolean disableComments;

    @Column
    private boolean disableSuscriptions;

    @Column
    private boolean isDeleted;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private UserEntity editor;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToMany
    @JoinTable(name = "magazine_tag", schema = "magazine_control", joinColumns = @JoinColumn(name = "magazine_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<TagEntity> tags;

    @OneToMany(mappedBy = "magazine")
    private Set<MagazineIssueEntity> issues;

    @OneToMany(mappedBy = "magazine")
    private Set<SubscriptionEntity> subscriptions;

    @ManyToMany
    @JoinTable(name = "like", schema = "interaction_control", joinColumns = @JoinColumn(name = "magazine_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> likes;

    @OneToMany(mappedBy = "magazine")
    private Set<CommentEntity> comments;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
