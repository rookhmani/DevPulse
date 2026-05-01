package com.devpulse.api.dto;

import java.util.List;

public record DashboardMetricsResponse(
        long totalRepositories,
        long totalPipelines,
        long totalDeployments,
        double successRate,
        double avgBuildTimeMinutes,
        long deploymentsThisWeek,
        List<DeploymentDayCount> deploymentsPerDay
) {
    public record DeploymentDayCount(String day, long count) {
    }
}
