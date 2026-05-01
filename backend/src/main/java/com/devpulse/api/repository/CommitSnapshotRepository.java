package com.devpulse.api.repository;

import com.devpulse.api.entity.CommitSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommitSnapshotRepository extends JpaRepository<CommitSnapshot, Long> {
    List<CommitSnapshot> findByRepositoryIdOrderByTimestampDesc(Long repositoryId);
    void deleteByRepositoryId(Long repositoryId);
}
