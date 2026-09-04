import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getHouseMembers, removeMemberFromHouse, changeMemberRole
} from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from './Modal';

export default function MembersTab({ houseId }) {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const [members, setMembers]   = useState([]);
  const [loading, setLoading]   = useState(true);
  const [error,   setError]     = useState('');

  const [roleModal,   setRoleModal]   = useState(null);
  const [removeConfirm, setRemoveConfirm] = useState(null);
  const [leaveConfirm,  setLeaveConfirm]  = useState(false);
  const [saving,    setSaving]    = useState(false);

  const load = async () => {
    try {
      setLoading(true);
      const res = await getHouseMembers(houseId);
      setMembers(res.data || []);
    } catch { setError('Üyeler yüklenemedi.'); }
    finally { setLoading(false); }
  };


  useEffect(() => { load(); }, [houseId]);


  const handleRemove = async () => {
    try {
      setSaving(true);
      await removeMemberFromHouse(houseId, { userId: removeConfirm.id });
      setRemoveConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Üye çıkarılamadı.'); }
    finally { setSaving(false); }
  };

  const handleLeaveHouse = async () => {
    try {
      setSaving(true);
      await removeMemberFromHouse(houseId, { userId: currentUser.id });
      setLeaveConfirm(false);
      navigate('/houses');
    } catch (err) {
      setError(err.response?.data?.message || 'Evden ayrılırken bir hata oluştu.');
    } finally {
      setSaving(false);
    }
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

  const currentMember = members.find(m => m.id === currentUser?.id);
  const isAdmin = currentMember?.role === 'ADMIN';
  const isNonAdmin = currentMember && currentMember.role !== 'ADMIN';

  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      <div className="section-header">
        <h3>👥 Ev Üyeleri ({members.length})</h3>
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
                {(isAdmin || isNonAdmin) && <th style={{ textAlign:'right' }}>İşlemler</th>}
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
                  {isAdmin ? (
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
                  ) : isNonAdmin ? (
                    <td>
                      <div className="flex gap-8" style={{ justifyContent:'flex-end' }}>
                        {m.id === currentUser?.id && (
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => setLeaveConfirm(true)}
                            style={{ display:'inline-flex', alignItems:'center', gap:4 }}
                          >
                            🚪 Evden Ayrıl
                          </button>
                        )}
                      </div>
                    </td>
                  ) : null}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Add Member Modal — kaldırıldı */}

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

      {/* Leave House Confirm Modal */}
      {leaveConfirm && (
        <Modal
          title="🚪 Evden Ayrıl"
          onClose={() => setLeaveConfirm(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setLeaveConfirm(false)}>İptal</button>
            <button className="btn btn-danger" onClick={handleLeaveHouse} disabled={saving}>
              {saving ? 'Ayrılınıyor...' : 'Evet, Ayrıl'}
            </button>
          </>}
        >
          <p>Bu evden ayrılmak istediğinizden emin misiniz?</p>
          <p style={{ marginTop:8, fontSize:'0.82rem', color:'var(--danger)' }}>
            ⚠️ Evden ayrıldıktan sonra tekrar katılabilmek için evin davet koduna ihtiyacınız olacaktır.
          </p>
        </Modal>
      )}
    </div>
  );
}
