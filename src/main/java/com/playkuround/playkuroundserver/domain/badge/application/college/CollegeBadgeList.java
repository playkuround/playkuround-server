package com.playkuround.playkuroundserver.domain.badge.application.college;

import lombok.Getter;

import java.util.List;

public class CollegeBadgeList {

    @Getter
    private static final List<CollegeBadge> collegeBadges = List.of(
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
            new InstituteTechnologyBadge(),
            new SangHuhBadge(),
            new InternationalBadge()
    );

    private CollegeBadgeList() {
    }

}
