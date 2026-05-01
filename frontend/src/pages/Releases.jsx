import { Plus } from 'lucide-react';
import { useEffect, useState } from 'react';
import api from '../api/client.js';
import Modal from '../components/Modal.jsx';
import StatusBadge from '../components/StatusBadge.jsx';

const emptyForm = { repositoryId: '', versionTag: '', environment: 'STAGING', status: 'IN_PROGRESS', notes: '' };

export default function Releases() {
  const [releases, setReleases] = useState([]);
  const [repositories, setRepositories] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(emptyForm);

  const load = async () => {
    const releaseResponse = await api.get('/releases');
    setReleases(releaseResponse.data);
  };

  const loadRepositories = async () => {
    const repoResponse = await api.get('/repositories');
    setRepositories(repoResponse.data);
  };

  useEffect(() => {
    load();
    loadRepositories();
  }, []);

  const openCreateModal = async () => {
    setForm(emptyForm);
    await loadRepositories();
    setShowModal(true);
  };

  const create = async (event) => {
    event.preventDefault();
    await api.post('/releases', { ...form, repositoryId: Number(form.repositoryId) });
    setForm(emptyForm);
    setShowModal(false);
    load();
  };

  return (
    <section>
      <div className="page-title">
        <h1>Releases</h1>
        <button onClick={openCreateModal}><Plus size={16} /> New Release</button>
      </div>

      <div className="table-wrap">
        <table>
          <thead><tr><th>Version</th><th>Repository</th><th>Environment</th><th>Status</th><th>Deployed</th><th>Notes</th></tr></thead>
          <tbody>
            {releases.map((release) => (
              <tr key={release.id}>
                <td>{release.versionTag}</td>
                <td>{release.repositoryName}</td>
                <td><span className={`env ${release.environment.toLowerCase()}`}>{release.environment}</span></td>
                <td><StatusBadge status={release.status} /></td>
                <td>{new Date(release.deployedAt).toLocaleString()}</td>
                <td>{release.notes}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <Modal title="New Release" onClose={() => setShowModal(false)}>
          <form className="form-stack" onSubmit={create}>
            <label>Repository
              <select required value={form.repositoryId} onChange={(e) => setForm({ ...form, repositoryId: e.target.value })}>
                <option value="">Select repository</option>
                {repositories.map((repo) => <option key={repo.id} value={repo.id}>{repo.name}</option>)}
              </select>
            </label>
            <label>Version<input required value={form.versionTag} onChange={(e) => setForm({ ...form, versionTag: e.target.value })} /></label>
            <label>Environment<select value={form.environment} onChange={(e) => setForm({ ...form, environment: e.target.value })}><option>DEV</option><option>STAGING</option><option>PROD</option></select></label>
            <label>Status<select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}><option>IN_PROGRESS</option><option>DEPLOYED</option><option>ROLLED_BACK</option></select></label>
            <label>Notes<textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></label>
            <button type="submit">Create release</button>
          </form>
        </Modal>
      )}
    </section>
  );
}
