package org.cunoc.pdfpedia.domain.entity.announcer;

import jakarta.persistence.*;
import lombok.*;
import org.cunoc.pdfpedia.domain.entity.UserEntity;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity(name = "ad")
@Table(name = "ad", schema = "ad_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private UserEntity advertiser;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "ad_type", nullable = false)
    private ChargePeriodAd chargePeriodAd;

    @NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @NonNull
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
