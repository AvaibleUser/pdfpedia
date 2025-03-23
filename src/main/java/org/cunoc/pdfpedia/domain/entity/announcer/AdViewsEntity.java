package org.cunoc.pdfpedia.domain.entity.announcer;


import jakarta.persistence.*;
import lombok.*;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Entity(name = "ad_view")
@Table(name = "ad_view", schema = "ad_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AdViewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    private AdEntity ad;

    @Column(name = "url_view", length = 255)
    private String urlView;

    @CreationTimestamp
    @Column
    private Instant createdAt;

}
