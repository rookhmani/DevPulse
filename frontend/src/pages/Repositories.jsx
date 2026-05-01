import { Plus } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client.js';
import Modal from '../components/Modal.jsx';
import StatusBadge from '../components/StatusBadge.jsx';

const demoRepoKey = 'devpulse_demo_repositories';

const getDemoRepositories = () => JSON.parse(localStorage.getItem(demoRepoKey) || '[]');

const saveDemoRepository = (githubRepoUrl) => {
  const cleanedUrl = githubRepoUrl.trim().replace(/\/$/, '');
  const name = cleanedUrl.split('/').pop()?.replace('.git', '') || 'Repository';
  const repositories = getDemoRepositories();
  const existing = repositories.find((repo) => repo.githubRepoUrl === cleanedUrl);
  if (existing) {
    return repositories;
  }
  const nextRepositories = [
    ...repositories,
    {
      id: `demo-${Date.now()}`,
      name,
      githubRepoUrl: cleanedUrl,
      lastPipelineStatus: 'PENDING',
      lastDeployedVersion: null,
    },
  ];
  localStorage.setItem(demoRepoKey, JSON.stringify(nextRepositories));
  return nextRepositories;
};

export default function Repositories() {
  const [repositories, setRepositories] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [githubRepoUrl, setGithubRepoUrl] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const response = await api.get('/repositories');
      setRepositories([...response.data, ...getDemoRepositories()]);
    } catch {
      setRepositories(getDemoRepositories());
    }
  };

  useEffect(() => {
    load();
  }, []);

  const addRepository = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await api.post('/repositories', { githubRepoUrl });
      setGithubRepoUrl('');
      setShowModal(false);
      load();
    } catch (err) {
      setRepositories(saveDemoRepository(githubRepoUrl));
      setGithubRepoUrl('');
      setShowModal(false);
    }
  };

  return (
    <section>
      <div className="page-title">
        <h1>Repositories</h1>
        <button onClick={() => setShowModal(true)}><Plus size={16} /> Add Repository</button>
      </div>

      <div className="table-wrap">
        <table>
          <thead><tr><th>Name</th><th>GitHub URL</th><th>Last Pipeline</th><th>Last Version</th></tr></thead>
          <tbody>
            {repositories.map((repo) => (
              <tr key={repo.id}>
                <td><Link to={`/pipelines/${repo.id}`}>{repo.name}</Link></td>
                <td>{repo.githubRepoUrl}</td>
                <td><StatusBadge status={repo.lastPipelineStatus} /></td>
                <td>{repo.lastDeployedVersion || 'n/a'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <Modal title="Add Repository" onClose={() => setShowModal(false)}>
          <form className="form-stack" onSubmit={addRepository}>
            <label>GitHub URL<input placeholder="https://github.com/owner/repo" value={githubRepoUrl} onChange={(e) => setGithubRepoUrl(e.target.value)} /></label>
            {error && <p className="error">{error}</p>}
            <button type="submit">Track repository</button>
          </form>
        </Modal>
      )}
    </section>
  );
}
