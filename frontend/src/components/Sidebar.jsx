import { NavLink, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';

const navItems = [
  { to: '/dashboard', icon: '🏠', label: 'Dashboard'  },
  { to: '/houses',    icon: '🏘️', label: 'Evler'       },
  { to: '/profile',   icon: '👤', label: 'Profilim'    },
];

export default function Sidebar() {
  const { currentUser, logout } = useUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const initials = currentUser?.name
    ? currentUser.name.split(' ').map(w => w[0]).join('').slice(0,2).toUpperCase()
    : '?';

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>🏡 Nestify</h2>
        <p>Ev Yönetim Sistemi</p>
      </div>

      <nav className="sidebar-nav">
        <div className="sidebar-section-label">Ana Menü</div>
        {navItems.map(item => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
          >
            <span className="nav-icon">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div style={{ padding: '0 12px', marginTop: 'auto' }}>
        <div className="sidebar-user" onClick={handleLogout} title="Çıkış yap">
          <div className="user-avatar">{initials}</div>
          <div className="user-info">
            <div className="name">{currentUser?.name || 'Kullanıcı'}</div>
            <div className="sub">Çıkış yapmak için tıkla</div>
          </div>
          <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>↩</span>
        </div>
      </div>
    </aside>
  );
}
