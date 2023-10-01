package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    @Length(min = 2, max = 8)
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Major major;


    @Builder
    public User(String email, String nickname, Major major) {
        this.email = email;
        this.nickname = nickname;
        this.major = major;
    }

}
