package com.devpulse.api.repository;

import com.devpulse.api.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryJpaRepository extends JpaRepository<RepositoryEntity, Long> {
    boolean existsByGithubRepoUrl(String githubRepoUrl);
}
