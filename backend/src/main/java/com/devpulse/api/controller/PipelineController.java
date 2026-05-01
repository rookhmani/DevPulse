package com.devpulse.api.controller;

import com.devpulse.api.dto.PipelineRequest;
import com.devpulse.api.dto.PipelineResponse;
import com.devpulse.api.dto.PipelineStatusRequest;
import com.devpulse.api.service.PipelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pipelines")
@RequiredArgsConstructor
public class PipelineController {
    private final PipelineService pipelineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PipelineResponse createPipeline(@Valid @RequestBody PipelineRequest request) {
        return pipelineService.create(request);
    }

    @PatchMapping("/{id}/status")
    public PipelineResponse updateStatus(@PathVariable Long id, @Valid @RequestBody PipelineStatusRequest request) {
        return pipelineService.updateStatus(id, request);
    }
}
