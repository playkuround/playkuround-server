package com.playkuround.playkuroundserver.domain.appversion.domain;

import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "app_version",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "OS_AND_VERSION_UNIQUE",
                        columnNames = {"os", "version"}
                )
        }
)
public class AppVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationSystem os;

    @Column(nullable = false)
    private String version;

    public AppVersion(OperationSystem os, String version) {
        Objects.requireNonNull(os);
        Objects.requireNonNull(version);
        this.os = os;
        this.version = version;
    }

}
