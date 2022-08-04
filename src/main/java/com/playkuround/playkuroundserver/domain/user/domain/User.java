package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS") // H2 test
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    @Length(min = 2, max = 8)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Major major;

    @Builder
    public User(String email, String nickname, Major major) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
    }

}
