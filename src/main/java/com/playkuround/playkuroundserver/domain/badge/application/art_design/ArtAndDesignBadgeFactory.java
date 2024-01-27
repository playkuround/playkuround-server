package com.playkuround.playkuroundserver.domain.badge.application.art_design;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class ArtAndDesignBadgeFactory {

    private final static List<ArtAndDesignBadge> artAndDesignBadges = List.of(
            new ArtAndDesignBeforeNoon(),
            new ArtAndDesignNight(),
            new ArtAndDesignAfterNoon()
    );

    public BadgeType getBadgeType(Set<BadgeType> userBadges) {
        LocalTime now = LocalDateTime.now().toLocalTime();
        return artAndDesignBadges.stream()
                .filter(artAndDesignBadge -> artAndDesignBadge.supports(userBadges, now))
                .findFirst()
                .map(ArtAndDesignBadge::getBadge)
                .orElse(null);
    }


}
