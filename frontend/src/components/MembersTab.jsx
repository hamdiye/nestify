import { useState, useEffect } from 'react';
import {
  getHouseMembers, addMemberToHouse, removeMemberFromHouse, changeMemberRole,
  getUsers
} from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from './Modal';

export default function MembersTab({ houseId }) {
  const { currentUser } = useUser();
  const [members, setMembers]   = useState([]);
  const [loading, setLoading]   = useState(true);
  const [error,   setError]     = useState('');

  const [addModal,    setAddModal]    = useState(false);
  const [roleModal,   setRoleModal]   = useState(null); // member object
  const [removeConfirm, setRemoveConfirm] = useState(null);

  const [allUsers,  setAllUsers]  = useState([]);
  const [addForm,   setAddForm]   = useState({ userId: '', role: 'MEMBER' });
  const [saving,    setSaving]    = useState(false);

  const load = async () => {
    try {
      setLoading(true);
      const res = await getHouseMembers(houseId);
      setMembers(res.data || []);
    } catch { setError('Üyeler yüklenemedi.'); }
    finally { setLoading(false); }
  };

  const loadAllUsers = async () => {
    try {
      const res = await getUsers(0, 100);
      setAllUsers(res.data.content || []);
    } catch { /**/ }
  };

  useEffect(() => { load(); }, [houseId]);

  const handleAdd = async () => {
    if (!addForm.userId) return;
    try {
      setSaving(true);
      await addMemberToHouse(houseId, { userId: Number(addForm.userId), role: addForm.role });
      setAddModal(false);
      setAddForm({ userId: '', role: 'MEMBER' });
      load();
    } catch (err) { setError(err.response?.data?.message || 'Üye eklenemedi.'); }
    finally { setSaving(false); }
  };

  const handleRemove = async () => {
    try {
      setSaving(true);
      await removeMemberFromHouse(houseId, { userId: removeConfirm.id });
      setRemoveConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Üye çıkarılamadı.'); }
    finally { setSaving(false); }
  };

  const handleChangeRole = async (newRole) => {
    try {
      setSaving(true);
      await changeMemberRole(houseId, roleModal.id, { userId: roleModal.id, role: newRole });
      setRoleModal(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Rol değiştirilemedi.'); }
    finally { setSaving(false); }
  };

  const initials = (name = '') =>
    name.split(' ').map(w => w[0]).join('').slice(0,2).toUpperCase();

  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      <div className="section-header">
        <h3>👥 Ev Üyeleri ({members.length})</h3>
        <button className="btn btn-primary btn-sm" onClick={() => { loadAllUsers(); setAddModal(true); }}>
          ＋ Üye Ekle
        </button>
      </div>

      {members.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">👥</div>
          <h3>Üye bulunamadı</h3>
          <p>Bu evde henüz üye yok.</p>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Üye</th>
                <th>E-Posta</th>
                <th>Katılım</th>
                <th>Rol</th>
                <th style={{ textAlign:'right' }}>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {members.map(m => (
                <tr key={m.id}>
                  <td>
                    <div className="flex items-center gap-12">
                      <div className="user-avatar" style={{ width:34, height:34, fontSize:'0.8rem' }}>
                        {initials(m.name)}
                      </div>
                      <div>
                        <div style={{ fontWeight:600, color:'var(--text-primary)', fontSize:'0.9rem' }}>
                          {m.name}
                          {m.id === currentUser?.id && (
                            <span className="badge badge-member" style={{ marginLeft:6, fontSize:'0.6rem' }}>Sen</span>
                          )}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>{m.email || '—'}</td>
                  <td style={{ fontSize:'0.8rem' }}>
                    {m.joinedAt ? new Date(m.joinedAt).toLocaleDateString('tr-TR') : '—'}
                  </td>
                  <td>
                    <span className={`badge badge-${m.role === 'ADMIN' ? 'admin' : 'member'}`}>
                      {m.role === 'ADMIN' ? '👑 Admin' : '👤 Üye'}
                    </span>
                  </td>
                  <td>
                    <div className="flex gap-8" style={{ justifyContent:'flex-end' }}>
                      <button className="btn btn-secondary btn-sm"
                        onClick={() => setRoleModal(m)}>
                        🔄 Rol
                      </button>
                      <button className="btn btn-danger btn-sm"
                        onClick={() => setRemoveConfirm(m)}>
                        ✕
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Add Member Modal */}
      {addModal && (
        <Modal title="➕ Üye Ekle" onClose={() => setAddModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setAddModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleAdd} disabled={saving || !addForm.userId}>
              {saving ? '...' : 'Ekle'}
            </button>
          </>}>
          <div className="form-group">
            <label className="form-label">Kullanıcı</label>
            <select className="form-select" value={addForm.userId}
              onChange={e => setAddForm(f => ({ ...f, userId: e.target.value }))}>
              <option value="">Kullanıcı seçin</option>
              {allUsers.filter(u => !members.find(m => m.id === u.id)).map(u => (
                <option key={u.id} value={u.id}>{u.name} ({u.email})</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Rol</label>
            <select className="form-select" value={addForm.role}
              onChange={e => setAddForm(f => ({ ...f, role: e.target.value }))}>
              <option value="MEMBER">👤 Üye</option>
              <option value="ADMIN">👑 Admin</option>
            </select>
          </div>
        </Modal>
      )}

      {/* Change Role Modal */}
      {roleModal && (
        <Modal title={`🔄 ${roleModal.name} — Rol Değiştir`} onClose={() => setRoleModal(null)}
          footer={<button className="btn btn-secondary" onClick={() => setRoleModal(null)}>Kapat</button>}>
          <p style={{ marginBottom:16 }}>Yeni rol seçin:</p>
          <div className="flex gap-12">
            <button className="btn btn-secondary w-full" onClick={() => handleChangeRole('MEMBER')} disabled={saving}>
              👤 Üye Yap
            </button>
            <button className="btn btn-primary w-full" onClick={() => handleChangeRole('ADMIN')} disabled={saving}>
              👑 Admin Yap
            </button>
          </div>
        </Modal>
      )}

      {/* Remove Confirm */}
      {removeConfirm && (
        <Modal title="🗑️ Üyeyi Çıkar" onClose={() => setRemoveConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setRemoveConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleRemove} disabled={saving}>
              {saving ? '...' : 'Çıkar'}
            </button>
          </>}>
          <p><strong style={{color:'var(--text-primary)'}}>{removeConfirm.name}</strong> adlı üyeyi evden çıkarmak istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
