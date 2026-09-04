import { useState } from 'react';
import { updateUser } from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from '../components/Modal';

export default function ProfilePage() {
  const { currentUser, login } = useUser();
  const [editModal, setEditModal] = useState(false);
  const [saving,    setSaving]    = useState(false);
  const [error,     setError]     = useState('');
  const [success,   setSuccess]   = useState('');

  const [form, setForm] = useState({
    name:  currentUser?.name  || '',
    email: currentUser?.email || '',
    password: '',
  });

  const handleUpdate = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      setSaving(true);
      const payload = { name: form.name, email: form.email };
      if (form.password) payload.password = form.password;

      const res = await updateUser(currentUser.id, payload);
      // Güncel bilgileri context'e yansıt
      login({ ...res.data, userId: res.data.id, fullName: res.data.name });
      setSuccess('Profiliniz başarıyla güncellendi.');
      setEditModal(false);
      setForm(f => ({ ...f, password: '' }));
    } catch (err) {
      setError(err.response?.data?.message || 'Güncelleme başarısız.');
    } finally {
      setSaving(false);
    }
  };

  const initials = (name = '') =>
    name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();

  return (
    <div>
      <div className="page-header">
        <div className="page-header-title">
          <h1>👤 Profilim</h1>
          <p>Hesap bilgilerinizi görüntüleyin ve düzenleyin.</p>
        </div>
      </div>

      {success && <div className="alert alert-success">✓ {success}</div>}
      {error   && <div className="alert alert-error">⚠️ {error}</div>}

      <div style={{ maxWidth: 560 }}>
        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: 24, marginBottom: 24 }}>
          <div className="user-avatar" style={{ width: 72, height: 72, fontSize: '1.6rem', flexShrink: 0 }}>
            {initials(currentUser?.name)}
          </div>
          <div style={{ flex: 1 }}>
            <h2 style={{ marginBottom: 4 }}>{currentUser?.name}</h2>
            <p style={{ fontSize: '0.875rem', marginBottom: 0 }}>{currentUser?.email}</p>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: 4 }}>
              Kullanıcı ID: {currentUser?.id}
            </p>
          </div>
          <button
            className="btn btn-primary btn-sm"
            onClick={() => {
              setForm({ name: currentUser?.name || '', email: currentUser?.email || '', password: '' });
              setError('');
              setEditModal(true);
            }}
          >
            ✏️ Düzenle
          </button>
        </div>

        <div className="card">
          <h3 style={{ marginBottom: 20, fontSize: '0.95rem', color: 'var(--text-secondary)' }}>
            HESAP BİLGİLERİ
          </h3>
          {[
            { label: 'Ad Soyad', value: currentUser?.name },
            { label: 'E-Posta',  value: currentUser?.email },
            { label: 'Şifre',    value: '••••••••' },
          ].map(row => (
            <div key={row.label} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '14px 0', borderBottom: '1px solid var(--border)'
            }}>
              <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                {row.label}
              </span>
              <span style={{ fontSize: '0.9rem', color: 'var(--text-primary)', fontWeight: 500 }}>
                {row.value}
              </span>
            </div>
          ))}
          <div style={{ paddingTop: 14 }} />
        </div>
      </div>

      {editModal && (
        <Modal
          title="✏️ Profili Düzenle"
          onClose={() => setEditModal(false)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setEditModal(false)}>İptal</button>
              <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>
                {saving ? 'Kaydediliyor...' : 'Kaydet'}
              </button>
            </>
          }
        >
          <form onSubmit={handleUpdate}>
            <div className="form-group">
              <label className="form-label">Ad Soyad</label>
              <input
                className="form-input"
                value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">E-Posta</label>
              <input
                className="form-input"
                type="email"
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Yeni Şifre <span style={{ color: 'var(--text-muted)', fontWeight: 400 }}>(değiştirmek istemiyorsan boş bırak)</span></label>
              <input
                className="form-input"
                type="password"
                placeholder="Min. 6 karakter"
                value={form.password}
                onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                minLength={form.password ? 6 : undefined}
              />
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
