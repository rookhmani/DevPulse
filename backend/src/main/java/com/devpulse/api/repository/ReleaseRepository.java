package com.devpulse.api.repository;

import com.devpulse.api.entity.Release;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.enums.ReleaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReleaseRepository extends JpaRepository<Release, Long> {
    List<Release> findByRepositoryIdOrderByDeployedAtDesc(Long repositoryId);
    Optional<Release> findTopByRepositoryOrderByDeployedAtDesc(RepositoryEntity repository);
    long countByStatusAndDeployedAtAfter(ReleaseStatus status, Instant since);
}
