package org.cunoc.pdfpedia.domain.entity.interaction;

import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "subscription")
@Table(name = "subscription", schema = "interaction_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "user", "magazine" })
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class SubscriptionEntity {

    @EmbeddedId
    private SubscriptionId id;

    @NonNull
    @ManyToOne(optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NonNull
    @ManyToOne(optional = false)
    @MapsId("magazineId")
    @JoinColumn(name = "magazine_id", nullable = false)
    private MagazineEntity magazine;

    @Column
    private boolean isDeleted;

    // @CreationTimestamp
    @NonNull
    @Column(nullable = false)
    private Instant subscribedAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
