import { useEffect, useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import api from './api/client.js';
import Layout from './components/Layout.jsx';
import Dashboard from './pages/Dashboard.jsx';
import PipelineDetail from './pages/PipelineDetail.jsx';
import Releases from './pages/Releases.jsx';
import Repositories from './pages/Repositories.jsx';

function ProtectedRoute({ children }) {
  const [ready, setReady] = useState(Boolean(localStorage.getItem('devpulse_token')));
  const [error, setError] = useState('');

  useEffect(() => {
    if (ready) {
      return;
    }

    const signInDemoUser = async () => {
      try {
        const { data } = await api.post('/auth/login', {
          email: 'admin@devpulse.local',
          password: 'admin123',
        });
        localStorage.setItem('devpulse_token', data.token);
        localStorage.setItem('devpulse_user', JSON.stringify(data));
        setReady(true);
      } catch {
        setError('Unable to connect to the DevPulse API.');
      }
    };

    signInDemoUser();
  }, [ready]);

  if (error) {
    return <main className="login-page"><section className="login-panel"><strong>{error}</strong></section></main>;
  }

  return ready ? children : <main className="login-page"><section className="login-panel"><strong>Opening DevPulse...</strong></section></main>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Navigate to="/" replace />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="repositories" element={<Repositories />} />
        <Route path="pipelines/:repoId" element={<PipelineDetail />} />
        <Route path="releases" element={<Releases />} />
      </Route>
    </Routes>
  );
}
