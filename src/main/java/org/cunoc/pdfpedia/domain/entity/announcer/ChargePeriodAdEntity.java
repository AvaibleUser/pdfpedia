package org.cunoc.pdfpedia.domain.entity.announcer;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

import org.cunoc.pdfpedia.domain.type.AdType;

import static lombok.AccessLevel.PRIVATE;

@Entity(name = "charge_period_ad")
@Table(name = "charge_period_ad", schema = "ad_control")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class ChargePeriodAdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ad_type", nullable = false)
    private AdType adType;

    @NonNull
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @NonNull
    @Column(nullable = false,  precision = 10, scale = 2)
    private BigDecimal cost;

    @OneToMany(mappedBy = "chargePeriodAd")
    private Set<AdEntity> ads;




}
