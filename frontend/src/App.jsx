import { useEffect, useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import api from './api/client.js';
import Layout from './components/Layout.jsx';
import Dashboard from './pages/Dashboard.jsx';
import PipelineDetail from './pages/PipelineDetail.jsx';
import Releases from './pages/Releases.jsx';
import Repositories from './pages/Repositories.jsx';

function ProtectedRoute({ children }) {
  const [ready, setReady] = useState(false);
  const [message, setMessage] = useState('Opening DevPulse...');

  useEffect(() => {
    localStorage.removeItem('devpulse_token');
    localStorage.setItem('devpulse_user', JSON.stringify({ username: 'Demo user', role: 'ADMIN' }));
    setMessage('Opening DevPulse...');
    setReady(true);
  }, []);

  return ready ? children : <main className="login-page"><section className="login-panel"><strong>{message}</strong></section></main>;
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
