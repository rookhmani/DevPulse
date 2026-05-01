package com.devpulse.api.service;

import com.devpulse.api.dto.PipelineRequest;
import com.devpulse.api.dto.PipelineResponse;
import com.devpulse.api.dto.PipelineStatusRequest;
import com.devpulse.api.entity.Pipeline;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.exception.ResourceNotFoundException;
import com.devpulse.api.repository.PipelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PipelineService {
    private final PipelineRepository pipelineRepository;
    private final RepositoryService repositoryService;

    @Transactional(readOnly = true)
    public List<PipelineResponse> findByRepository(Long repositoryId) {
        return pipelineRepository.findByRepositoryIdOrderByTriggeredAtDesc(repositoryId).stream().map(this::toResponse).toList();
    }

    public PipelineResponse create(PipelineRequest request) {
        RepositoryEntity repository = repositoryService.getEntity(request.repositoryId());
        Pipeline pipeline = Pipeline.builder()
                .repository(repository)
                .branchName(request.branchName())
                .status(request.status())
                .triggeredAt(request.triggeredAt() == null ? Instant.now() : request.triggeredAt())
                .completedAt(request.completedAt())
                .commitSha(request.commitSha())
                .commitMessage(request.commitMessage())
                .build();
        Pipeline saved = pipelineRepository.save(pipeline);
        return toResponse(saved, repository.getId(), repository.getName());
    }

    public PipelineResponse updateStatus(Long id, PipelineStatusRequest request) {
        Pipeline pipeline = pipelineRepository.findByIdWithRepository(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline not found"));
        pipeline.setStatus(request.status());
        pipeline.setCompletedAt(request.completedAt() == null ? Instant.now() : request.completedAt());
        return toResponse(pipelineRepository.save(pipeline));
    }

    public PipelineResponse toResponse(Pipeline pipeline) {
        return toResponse(pipeline, pipeline.getRepository().getId(), pipeline.getRepository().getName());
    }

    private PipelineResponse toResponse(Pipeline pipeline, Long repositoryId, String repositoryName) {
        Long duration = null;
        if (pipeline.getCompletedAt() != null) {
            duration = Duration.between(pipeline.getTriggeredAt(), pipeline.getCompletedAt()).getSeconds();
        }
        return new PipelineResponse(
                pipeline.getId(),
                repositoryId,
                repositoryName,
                pipeline.getBranchName(),
                pipeline.getStatus(),
                pipeline.getTriggeredAt(),
                pipeline.getCompletedAt(),
                pipeline.getCommitSha(),
                pipeline.getCommitMessage(),
                duration
        );
    }
}
