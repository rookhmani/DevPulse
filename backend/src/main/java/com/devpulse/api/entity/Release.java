package com.devpulse.api.entity;

import com.devpulse.api.enums.ReleaseEnvironment;
import com.devpulse.api.enums.ReleaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "releases")
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private RepositoryEntity repository;

    @Column(nullable = false)
    private String versionTag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseStatus status;

    @Column(nullable = false)
    private Instant deployedAt;

    @Column(length = 1500)
    private String notes;
}
