import { useEffect, useMemo, useState } from 'react';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import api from '../api/client.js';
import MetricCard from '../components/MetricCard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';

const getDemoRepositories = () => JSON.parse(localStorage.getItem('devpulse_demo_repositories') || '[]');
const getDemoReleases = () => JSON.parse(localStorage.getItem('devpulse_demo_releases') || '[]');

const buildLastSevenDays = (releases) => {
  const today = new Date();
  return Array.from({ length: 7 }, (_, index) => {
    const date = new Date(today);
    date.setDate(today.getDate() - (6 - index));
    const key = date.toISOString().slice(0, 10);
    return {
      day: date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' }),
      count: releases.filter((release) => release.deployedAt?.slice(0, 10) === key).length,
    };
  });
};

const mergeDeploymentDays = (apiDays = [], demoDays = []) => {
  const countsByDay = new Map(apiDays.map((item) => [item.day, item.count]));
  demoDays.forEach((item) => countsByDay.set(item.day, (countsByDay.get(item.day) || 0) + item.count));
  return demoDays.map((item) => ({ ...item, count: countsByDay.get(item.day) || 0 }));
};

export default function Dashboard() {
  const [metrics, setMetrics] = useState(null);
  const [repositories, setRepositories] = useState([]);

  const load = async () => {
    try {
      const [metricResponse, repoResponse] = await Promise.all([
        api.get('/dashboard/metrics'),
        api.get('/repositories'),
      ]);
      const demoRepositories = getDemoRepositories();
      const demoReleases = getDemoReleases();
      const demoDays = buildLastSevenDays(demoReleases);
      setMetrics({
        ...metricResponse.data,
        totalRepositories: metricResponse.data.totalRepositories + demoRepositories.length,
        deploymentsThisWeek: metricResponse.data.deploymentsThisWeek + demoReleases.length,
        deploymentsPerDay: mergeDeploymentDays(metricResponse.data.deploymentsPerDay, demoDays),
      });
      setRepositories([...repoResponse.data, ...demoRepositories]);
    } catch {
      const demoRepositories = getDemoRepositories();
      const demoReleases = getDemoReleases();
      setMetrics({
        totalRepositories: demoRepositories.length,
        totalPipelines: demoRepositories.length,
        successRate: demoRepositories.length
          ? Math.round((demoRepositories.filter((repo) => repo.lastPipelineStatus === 'SUCCESS').length / demoRepositories.length) * 100)
          : 0,
        deploymentsThisWeek: demoReleases.length,
        deploymentsPerDay: buildLastSevenDays(demoReleases),
      });
      setRepositories(demoRepositories);
    }
  };

  useEffect(() => {
    load();
    const timer = setInterval(load, 15000);
    return () => clearInterval(timer);
  }, []);

  const cards = useMemo(() => [
    ['Total Repos', metrics?.totalRepositories ?? 0],
    ['Total Pipelines', metrics?.totalPipelines ?? 0],
    ['Success Rate', `${metrics?.successRate ?? 0}%`],
    ['Deployments This Week', metrics?.deploymentsThisWeek ?? 0],
  ], [metrics]);

  return (
    <section>
      <div className="page-title">
        <h1>Release Dashboard</h1>
        <span>Auto-refreshes every 15 seconds</span>
      </div>

      <div className="metric-grid">
        {cards.map(([label, value]) => <MetricCard key={label} label={label} value={value} />)}
      </div>

      <div className="dashboard-grid">
        <section className="panel">
          <h2>Pipeline Status Board</h2>
          <div className="repo-status-grid">
            {repositories.map((repo) => (
              <article key={repo.id} className={`repo-status ${repo.lastPipelineStatus === 'RUNNING' ? 'pulse' : ''}`}>
                <div>
                  <strong>{repo.name}</strong>
                  <span>{repo.githubRepoUrl}</span>
                </div>
                <StatusBadge status={repo.lastPipelineStatus} />
              </article>
            ))}
          </div>
        </section>

        <section className="panel">
          <h2>Deployments</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={metrics?.deploymentsPerDay || []}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="day" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="count" fill="#0f9f6e" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </section>
      </div>
    </section>
  );
}
