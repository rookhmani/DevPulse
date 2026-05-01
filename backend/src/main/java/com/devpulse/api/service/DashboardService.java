package com.devpulse.api.service;

import com.devpulse.api.dto.DashboardMetricsResponse;
import com.devpulse.api.entity.Pipeline;
import com.devpulse.api.entity.Release;
import com.devpulse.api.enums.PipelineStatus;
import com.devpulse.api.enums.ReleaseStatus;
import com.devpulse.api.repository.PipelineRepository;
import com.devpulse.api.repository.ReleaseRepository;
import com.devpulse.api.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RepositoryJpaRepository repositoryRepository;
    private final PipelineRepository pipelineRepository;
    private final ReleaseRepository releaseRepository;

    public DashboardMetricsResponse getMetrics() {
        List<Pipeline> pipelines = pipelineRepository.findAll();
        List<Release> releases = releaseRepository.findAll();
        long deployedReleases = releases.stream().filter(r -> r.getStatus() == ReleaseStatus.DEPLOYED).count();
        long totalPipelines = pipelines.size();
        long successes = pipelines.stream().filter(p -> p.getStatus() == PipelineStatus.SUCCESS).count();
        double successRate = totalPipelines == 0 ? 0 : (successes * 100.0) / totalPipelines;
        double avgMinutes = pipelines.stream()
                .filter(p -> p.getCompletedAt() != null)
                .mapToLong(p -> Duration.between(p.getTriggeredAt(), p.getCompletedAt()).toSeconds())
                .average()
                .orElse(0) / 60.0;

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<DashboardMetricsResponse.DeploymentDayCount> perDay = today.minusDays(6).datesUntil(today.plusDays(1))
                .map(day -> new DashboardMetricsResponse.DeploymentDayCount(
                        day.toString(),
                        releases.stream()
                                .filter(r -> r.getStatus() == ReleaseStatus.DEPLOYED)
                                .filter(r -> LocalDate.ofInstant(r.getDeployedAt(), ZoneOffset.UTC).equals(day))
                                .count()))
                .toList();

        Instant sevenDaysAgo = today.minusDays(6).atStartOfDay().toInstant(ZoneOffset.UTC);
        long weekDeployments = releaseRepository.countByStatusAndDeployedAtAfter(ReleaseStatus.DEPLOYED, sevenDaysAgo);

        return new DashboardMetricsResponse(
                repositoryRepository.count(),
                totalPipelines,
                deployedReleases,
                Math.round(successRate * 10.0) / 10.0,
                Math.round(avgMinutes * 10.0) / 10.0,
                weekDeployments,
                perDay
        );
    }
}
