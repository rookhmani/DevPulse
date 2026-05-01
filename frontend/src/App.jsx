import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Login from './pages/Login.jsx';
import PipelineDetail from './pages/PipelineDetail.jsx';
import Releases from './pages/Releases.jsx';
import Repositories from './pages/Repositories.jsx';

function ProtectedRoute({ children }) {
  return localStorage.getItem('devpulse_token') ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
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
