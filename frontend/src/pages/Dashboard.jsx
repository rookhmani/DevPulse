import { useEffect, useMemo, useState } from 'react';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import api from '../api/client.js';
import MetricCard from '../components/MetricCard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';

export default function Dashboard() {
  const [metrics, setMetrics] = useState(null);
  const [repositories, setRepositories] = useState([]);

  const load = async () => {
    const [metricResponse, repoResponse] = await Promise.all([
      api.get('/dashboard/metrics'),
      api.get('/repositories'),
    ]);
    setMetrics(metricResponse.data);
    setRepositories(repoResponse.data);
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
