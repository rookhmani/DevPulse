package com.devpulse.api.repository;

import com.devpulse.api.entity.Pipeline;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.enums.PipelineStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    @Query("select p from Pipeline p join fetch p.repository where p.repository.id = :repositoryId order by p.triggeredAt desc")
    List<Pipeline> findByRepositoryIdOrderByTriggeredAtDesc(@Param("repositoryId") Long repositoryId);

    @Query("select p from Pipeline p join fetch p.repository where p.id = :id")
    Optional<Pipeline> findByIdWithRepository(@Param("id") Long id);

    Optional<Pipeline> findTopByRepositoryOrderByTriggeredAtDesc(RepositoryEntity repository);
    long countByStatus(PipelineStatus status);
    long countByTriggeredAtAfter(Instant since);
}
