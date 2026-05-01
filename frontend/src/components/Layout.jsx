import { Activity, Boxes, GitBranch, Rocket } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';

export default function Layout() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('devpulse_user') || '{}');

  const logout = () => {
    localStorage.removeItem('devpulse_token');
    localStorage.removeItem('devpulse_user');
    navigate('/login');
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand"><Activity size={24} /> DevPulse</div>
        <nav>
          <NavLink to="/" end><Boxes size={18} /> Dashboard</NavLink>
          <NavLink to="/repositories"><GitBranch size={18} /> Repositories</NavLink>
          <NavLink to="/releases"><Rocket size={18} /> Releases</NavLink>
        </nav>
        <div className="sidebar-footer">
          <span>{user.username || 'Developer'}</span>
          <button onClick={logout}>Logout</button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
