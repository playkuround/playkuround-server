package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.request.BadgeFindRequest;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeRepository badgeRepository;

    @Test
    void 뱃지_개수가_0개이면_빈리스트가_반환된다() {
        // given
        User user = TestUtil.createUser();
        when(badgeRepository.findByUser(user))
                .thenReturn(new ArrayList<>());

        // when
        List<BadgeFindRequest> result = badgeService.findBadgeByEmail(user);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 뱃지_개수_3개_조회() {
        // given
        User user = TestUtil.createUser();
        List<Badge> badges = List.of(
                new Badge(user, BadgeType.ENGINEER),
                new Badge(user, BadgeType.ADVENTURE_1),
                new Badge(user, BadgeType.ATTENDANCE_1)
        );
        when(badgeRepository.findByUser(user))
                .thenReturn(badges);

        // when
        List<BadgeFindRequest> result = badgeService.findBadgeByEmail(user);

        // then
        assertThat(result).hasSize(3);

        List<String> target = result.stream()
                .map(BadgeFindRequest::getName)
                .toList();
        assertThat(target).containsOnly(BadgeType.ENGINEER.name(), BadgeType.ADVENTURE_1.name(), BadgeType.ATTENDANCE_1.name());
    }
}