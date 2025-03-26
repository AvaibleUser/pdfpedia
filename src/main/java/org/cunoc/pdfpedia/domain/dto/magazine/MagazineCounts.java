package org.cunoc.pdfpedia.domain.dto.magazine;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazineCounts(
        long issuesCount,
        long subscriptionsCount,
        long likesCount,
        long commentsCount,
        @JsonIgnore String tagNames) {

    @JsonGetter("tagNames")
    public Set<String> getTagNames() {
        return tagNames == null ? null : Set.of(tagNames.split(","));
    }
}
