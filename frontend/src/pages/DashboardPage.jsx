import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUserHouses, createHouse } from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from '../components/Modal';

export default function DashboardPage() {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const [houses, setHouses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [form, setForm] = useState({ title: '', address: '', city: '' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      setLoading(true);
      const res = await getUserHouses(currentUser.id);
      setHouses(res.data || []);
    } catch {
      setError('Evler yüklenemedi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { if (currentUser) load(); }, [currentUser]);

  const handleCreateHouse = async (e) => {
    e.preventDefault();
    if (!form.title) return;
    try {
      setSaving(true);
      await createHouse({ userId: currentUser.id, ...form, members: [] });
      setShowCreateModal(false);
      setForm({ title: '', address: '', city: '' });
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Ev oluşturulamadı.');
    } finally {
      setSaving(false);
    }
  };

  const initials = (name = '') =>
    name.split(' ').map(w => w[0]).join('').slice(0,2).toUpperCase();

  const totalMembers = houses.reduce((acc, h) => {
    const members = Array.isArray(h.members) ? h.members : [];
    return acc + members.length;
  }, 0);

  return (
    <div>
      {/* Welcome Banner */}
      <div className="welcome-banner">
        <h1>Merhaba, {currentUser?.name?.split(' ')[0]} 👋</h1>
        <p>Nestify'a hoş geldiniz. Evlerinizi yönetin, etkinlik planlayın, ihtiyaçlarınızı takip edin.</p>
      </div>

      {/* Stats */}
      <div className="grid-4 mb-32">
        {[
          { icon: '🏠', label: 'Evlerim',  value: houses.length,  cls: 'purple' },
          { icon: '👥', label: 'Toplam Üye', value: totalMembers, cls: 'blue'   },
          { icon: '📅', label: 'Etkinlikler', value: '—',         cls: 'green'  },
          { icon: '🛒', label: 'İhtiyaçlar',  value: '—',         cls: 'amber'  },
        ].map(s => (
          <div key={s.label} className="stat-card">
            <div className={`stat-icon ${s.cls}`}>{s.icon}</div>
            <div className="stat-info">
              <div className="value">{s.value}</div>
              <div className="label">{s.label}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Houses */}
      <div className="section-header">
        <h2>Evlerim</h2>
        <button className="btn btn-primary btn-sm" onClick={() => setShowCreateModal(true)}>
          ＋ Yeni Ev
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : houses.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">🏠</div>
          <h3>Henüz eviniz yok</h3>
          <p>İlk evinizi oluşturun ve yönetmeye başlayın.</p>
          <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
            ＋ Ev Oluştur
          </button>
        </div>
      ) : (
        <div className="grid-2">
          {houses.map(house => {
            const members = Array.isArray(house.members) ? house.members : [];
            return (
              <div
                key={house.id}
                className="house-card"
                onClick={() => navigate(`/houses/${house.id}`)}
              >
                <div className="house-card-title">{house.title}</div>
                <div className="house-card-meta">
                  {house.city    && <div className="meta-row">📍 {house.city}{house.address ? `, ${house.address}` : ''}</div>}
                  {house.inviteCode && <div className="meta-row"><span className="invite-code">🔑 {house.inviteCode}</span></div>}
                </div>
                <div className="house-card-footer">
                  <div className="member-avatars">
                    {members.slice(0, 4).map((m, i) => (
                      <div key={i} className="member-avatar" title={m.name}>{initials(m.name || '?')}</div>
                    ))}
                    {members.length > 4 && (
                      <div className="member-avatar" style={{ background: 'var(--bg-card)', color: 'var(--text-muted)', fontSize: '0.7rem' }}>
                        +{members.length - 4}
                      </div>
                    )}
                    <span className="member-count-badge">{members.length} üye</span>
                  </div>
                  <span style={{ fontSize: '0.75rem', color: 'var(--accent-2)' }}>Detay →</span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Create Modal */}
      {showCreateModal && (
        <Modal
          title="🏠 Yeni Ev Oluştur"
          onClose={() => setShowCreateModal(false)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>İptal</button>
              <button className="btn btn-primary" onClick={handleCreateHouse} disabled={saving}>
                {saving ? 'Oluşturuluyor...' : 'Oluştur'}
              </button>
            </>
          }
        >
          <div className="form-group">
            <label className="form-label">Ev Adı *</label>
            <input className="form-input" placeholder="Örn: Merkez Dairesi" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Şehir</label>
            <input className="form-input" placeholder="İstanbul" value={form.city}
              onChange={e => setForm(f => ({ ...f, city: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Adres</label>
            <input className="form-input" placeholder="Bağcılar Mah. No:5" value={form.address}
              onChange={e => setForm(f => ({ ...f, address: e.target.value }))} />
          </div>
        </Modal>
      )}
    </div>
  );
}
