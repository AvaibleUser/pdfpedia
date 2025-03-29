package org.cunoc.pdfpedia.domain.entity.interaction;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "magazine_id", nullable = false)
    private MagazineEntity magazine;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionId that = (SubscriptionId) o;
        return Objects.equals(user, that.user) && Objects.equals(magazine, that.magazine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, magazine);
    }
}