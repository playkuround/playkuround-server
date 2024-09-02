package com.playkuround.playkuroundserver.domain.badge.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    @Builder
    public Badge(User user, BadgeType badgeType) {
        this.user = user;
        this.badgeType = badgeType;
    }

    public static Badge createBadge(User user, BadgeType badgeType) {
        return Badge.builder()
                .user(user)
                .badgeType(badgeType)
                .build();
    }

}
