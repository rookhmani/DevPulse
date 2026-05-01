package com.devpulse.api.controller;

import com.devpulse.api.dto.CommitResponse;
import com.devpulse.api.dto.PipelineResponse;
import com.devpulse.api.dto.RepositoryCreateRequest;
import com.devpulse.api.dto.RepositoryResponse;
import com.devpulse.api.dto.ReleaseResponse;
import com.devpulse.api.service.PipelineService;
import com.devpulse.api.service.RepositoryService;
import com.devpulse.api.service.ReleaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final PipelineService pipelineService;
    private final ReleaseService releaseService;

    @GetMapping
    public List<RepositoryResponse> getRepositories() {
        return repositoryService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepositoryResponse createRepository(@Valid @RequestBody RepositoryCreateRequest request) {
        return repositoryService.create(request);
    }

    @GetMapping("/{id}/pipelines")
    public List<PipelineResponse> getPipelines(@PathVariable Long id) {
        return pipelineService.findByRepository(id);
    }

    @GetMapping("/{id}/releases")
    public List<ReleaseResponse> getReleases(@PathVariable Long id) {
        return releaseService.findByRepository(id);
    }

    @GetMapping("/{id}/commits")
    public List<CommitResponse> getCommits(@PathVariable Long id) {
        return repositoryService.getCommits(id);
    }
}
