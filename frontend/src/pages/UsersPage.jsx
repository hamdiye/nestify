import { useState, useEffect } from 'react';
import { getUsers, createUser, updateUser, deleteUser } from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from '../components/Modal';

export default function UsersPage() {
  const { currentUser } = useUser();
  const [users, setUsers] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [createModal, setCreateModal] = useState(false);
  const [editModal, setEditModal] = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({ name: '', email: '', password: '', confirmPassword: '' });

  const PAGE_SIZE = 10;

  const load = async (p = 0) => {
    try {
      setLoading(true);
      const res = await getUsers(p, PAGE_SIZE, 'id', 'asc');
      setUsers(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch { setError('Kullanıcılar yüklenemedi.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(page); }, [page]);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      await createUser(form);
      setCreateModal(false);
      setForm({ name: '', email: '', password: '', confirmPassword: '' });
      load(page);
    } catch (err) { setError(err.response?.data?.message || 'Oluşturma hatası.'); }
    finally { setSaving(false); }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      await updateUser(editModal.id, form);
      setEditModal(null);
      load(page);
    } catch (err) { setError(err.response?.data?.message || 'Güncelleme hatası.'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    try {
      await deleteUser(deleteConfirm.id);
      setDeleteConfirm(null);
      load(page);
    } catch (err) { setError(err.response?.data?.message || 'Silme hatası.'); }
  };

  const openEdit = (user) => {
    setForm({ name: user.name, email: user.email, password: '' });
    setEditModal(user);
  };

  const initials = (name = '') =>
    name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();

  const formatDate = (dt) =>
    dt ? new Date(dt).toLocaleDateString('tr-TR') : '—';

  const totalPages = Math.ceil(total / PAGE_SIZE);

  const UserForm = ({ onSubmit, showPassword = true }) => (
    <form onSubmit={onSubmit}>
      <div className="form-group">
        <label className="form-label">Ad Soyad *</label>
        <input className="form-input" placeholder="Ad Soyad" value={form.name}
          onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
      </div>
      <div className="form-group">
        <label className="form-label">E-Posta *</label>
        <input className="form-input" type="email" placeholder="ornek@mail.com" value={form.email}
          onChange={e => setForm(f => ({ ...f, email: e.target.value }))} required />
      </div>
      {showPassword && (
        <>
          <div className="form-group">
            <label className="form-label">Şifre {editModal ? '(boş bırakılabilir)' : '*'}</label>
            <input className="form-input" type="password" placeholder="Min. 6 karakter" value={form.password}
              onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
              required={!editModal} minLength={editModal && form.password ? 6 : undefined} />
          </div>
          {!editModal && (
            <div className="form-group">
              <label className="form-label">Şifre Tekrar *</label>
              <input className="form-input" type="password" placeholder="Şifreyi tekrar girin" value={form.confirmPassword}
                onChange={e => setForm(f => ({ ...f, confirmPassword: e.target.value }))}
                required />
            </div>
          )}
        </>
      )}
    </form>
  );

  return (
    <div>
      <div className="page-header">
        <div className="page-header-title">
          <h1>👥 Kullanıcılar</h1>
          <p>Sistemdeki tüm kullanıcıları yönetin. ({total} toplam)</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setForm({ name: '', email: '', password: '', confirmPassword: '' }); setCreateModal(true); }}>
          ＋ Kullanıcı Ekle
        </button>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : (
        <>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Kullanıcı</th>
                  <th>E-Posta</th>
                  <th>Kayıt Tarihi</th>
                  <th style={{ textAlign: 'right' }}>İşlemler</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan={4} style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                      Kullanıcı bulunamadı.
                    </td>
                  </tr>
                ) : users.map(user => (
                  <tr key={user.id}>
                    <td>
                      <div className="flex items-center gap-12">
                        <div className="user-avatar" style={{ width: 36, height: 36, fontSize: '0.85rem' }}>
                          {initials(user.name)}
                        </div>
                        <div>
                          <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '0.9rem' }}>
                            {user.name}
                            {currentUser?.id === user.id && (
                              <span className="badge badge-admin" style={{ marginLeft: 8, fontSize: '0.65rem' }}>Sen</span>
                            )}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ID: {user.id}</div>
                        </div>
                      </div>
                    </td>
                    <td>{user.email}</td>
                    <td>{formatDate(user.createdAt)}</td>
                    <td>
                      <div className="flex gap-8" style={{ justifyContent: 'flex-end' }}>
                        <button className="btn btn-secondary btn-sm" onClick={() => openEdit(user)}>✏️ Düzenle</button>
                        {currentUser?.id !== user.id && (
                          <button className="btn btn-danger btn-sm" onClick={() => setDeleteConfirm(user)}>🗑️</button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button className="page-btn" onClick={() => setPage(p => p - 1)} disabled={page === 0}>‹</button>
              {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
                const p = page < 4 ? i : page - 3 + i;
                if (p >= totalPages) return null;
                return (
                  <button key={p} className={`page-btn${page === p ? ' active' : ''}`} onClick={() => setPage(p)}>
                    {p + 1}
                  </button>
                );
              })}
              <button className="page-btn" onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1}>›</button>
            </div>
          )}
        </>
      )}

      {/* Create */}
      {createModal && (
        <Modal title="➕ Yeni Kullanıcı" onClose={() => setCreateModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving}>{saving ? '...' : 'Oluştur'}</button>
          </>}>
          <UserForm onSubmit={handleCreate} />
        </Modal>
      )}

      {/* Edit */}
      {editModal && (
        <Modal title="✏️ Kullanıcıyı Düzenle" onClose={() => setEditModal(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setEditModal(null)}>İptal</button>
            <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>{saving ? '...' : 'Kaydet'}</button>
          </>}>
          <UserForm onSubmit={handleUpdate} />
        </Modal>
      )}

      {/* Delete */}
      {deleteConfirm && (
        <Modal title="🗑️ Kullanıcıyı Sil" onClose={() => setDeleteConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setDeleteConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{ color: 'var(--text-primary)' }}>{deleteConfirm.name}</strong> adlı kullanıcıyı silmek istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
