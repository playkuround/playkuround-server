package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import lombok.Getter;

import java.util.List;

public class SpecialDayBadgeList {

    @Getter
    private static final List<SpecialDayBadge> specialDayBadges = List.of(
            new FoundationDayBadge(),
            new ArborDayBadge(),
            new ChildrenDayBadge(),
            new DuckDayBadge(),
            new WhiteDayBadge(),
            new ChuseokDayBadge(),
            new KoreanDayBadge(),
            new DokdoDayBadge(),
            new KimchiDayBadge(),
            new ChristmasDayBadge()
    );

    private SpecialDayBadgeList() {
    }
}
