package com.devpulse.api.controller;

import com.devpulse.api.dto.ReleaseRequest;
import com.devpulse.api.dto.ReleaseResponse;
import com.devpulse.api.service.ReleaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseController {
    private final ReleaseService releaseService;

    @GetMapping
    public List<ReleaseResponse> getReleases() {
        return releaseService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReleaseResponse createRelease(@Valid @RequestBody ReleaseRequest request) {
        return releaseService.create(request);
    }
}
