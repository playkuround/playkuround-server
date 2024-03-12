package com.playkuround.playkuroundserver.domain.badge.application.college;

import lombok.Getter;

import java.util.List;

public class CollegeBadgeList {

    @Getter
    private final static List<CollegeBadge> collegeBadges = List.of(
            new LiberalArtsBadge(),
            new AdministrationBadge(),
            new EducationBadge(),
            new SciencesBadge(),
            new RealEstateBadge(),
            new ArtAndDesignBadge(),
            new EngineeringBadge(),
            new SocialSciencesBadge(),
            new VeterinaryMedicineBadge(),
            new BiologicalSciencesBadge(),
            new ArchitectureBadge(),
            new InstituteTechnologyBadge()
    );

    private CollegeBadgeList() {
    }

}
