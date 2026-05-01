import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/client.js';
import StatusBadge from '../components/StatusBadge.jsx';

export default function PipelineDetail() {
  const { repoId } = useParams();
  const [pipelines, setPipelines] = useState([]);
  const [commits, setCommits] = useState([]);
  const [savingStatus, setSavingStatus] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    const [pipelineResponse, commitResponse] = await Promise.all([
      api.get(`/repositories/${repoId}/pipelines`),
      api.get(`/repositories/${repoId}/commits`),
    ]);
    setPipelines(pipelineResponse.data);
    setCommits(commitResponse.data);
  };

  const setLatestPipelineStatus = async (status) => {
    setError('');
    setSavingStatus(status);
    try {
      const latestPipeline = pipelines[0];

      if (latestPipeline) {
        await api.patch(`/pipelines/${latestPipeline.id}/status`, { status });
      } else {
        const latestCommit = commits[0];
        await api.post('/pipelines', {
          repositoryId: Number(repoId),
          branchName: 'main',
          status,
          commitSha: latestCommit?.sha || 'manual-run',
          commitMessage: latestCommit?.message || 'Manual pipeline run',
        });
      }

      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not update pipeline status.');
    } finally {
      setSavingStatus('');
    }
  };

  useEffect(() => {
    load();
    const timer = setInterval(load, 15000);
    return () => clearInterval(timer);
  }, [repoId]);

  return (
    <section>
      <div className="page-title">
        <h1>Pipeline Detail</h1>
        <span>Live polling enabled</span>
      </div>

      <div className="detail-grid">
        <section className="panel">
          <div className="panel-head">
            <h2>Timeline</h2>
            <div className="status-actions">
              <button
                className="success-action"
                disabled={Boolean(savingStatus)}
                onClick={() => setLatestPipelineStatus('SUCCESS')}
              >
                {savingStatus === 'SUCCESS' ? 'Saving...' : 'Mark Success'}
              </button>
              <button
                className="running-action"
                disabled={Boolean(savingStatus)}
                onClick={() => setLatestPipelineStatus('RUNNING')}
              >
                Running
              </button>
              <button
                className="failed-action"
                disabled={Boolean(savingStatus)}
                onClick={() => setLatestPipelineStatus('FAILED')}
              >
                Failed
              </button>
            </div>
          </div>
          {error && <p className="error">{error}</p>}
          <div className="timeline">
            {pipelines.length === 0 && (
              <p className="empty-state">No pipeline run yet. Use the buttons above to create one from the latest commit.</p>
            )}
            {pipelines.map((pipeline) => (
              <article key={pipeline.id} className="timeline-item">
                <div className="timeline-dot" />
                <div>
                  <div className="timeline-head">
                    <strong>{pipeline.branchName}</strong>
                    <StatusBadge status={pipeline.status} />
                  </div>
                  <p>{pipeline.commitMessage || pipeline.commitSha}</p>
                  <span>{new Date(pipeline.triggeredAt).toLocaleString()} · {pipeline.durationSeconds ? `${pipeline.durationSeconds}s` : 'running'}</span>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="panel">
          <h2>Commit Log</h2>
          <div className="commit-list">
            {commits.map((commit) => (
              <article key={commit.id}>
                <code>{commit.sha.slice(0, 7)}</code>
                <strong>{commit.message}</strong>
                <span>{commit.author || 'unknown'} · {commit.timestamp ? new Date(commit.timestamp).toLocaleString() : ''}</span>
              </article>
            ))}
          </div>
        </section>
      </div>
    </section>
  );
}
