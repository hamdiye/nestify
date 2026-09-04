import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getHousesOfUser, createHouse, updateHouse, deleteHouse } from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from '../components/Modal';

export default function HousesPage() {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const [houses, setHouses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [createModal, setCreateModal] = useState(false);
  const [editModal,   setEditModal]   = useState(null); // house object
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  const [form, setForm] = useState({ title: '', address: '', city: '' });
  const [saving, setSaving] = useState(false);

  const load = async () => {
    try {
      setLoading(true);
      const res = await getHousesOfUser(currentUser.id);
      setHouses(res.data || []);
    } catch { setError('Evler yüklenemedi.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!form.title) return;
    try {
      setSaving(true);
      await createHouse({ userId: currentUser.id, ...form, members: [] });
      setCreateModal(false);
      setForm({ title: '', address: '', city: '' });
      load();
    } catch (err) { setError(err.response?.data?.message || 'Hata oluştu.'); }
    finally { setSaving(false); }
  };

  const handleEdit = async (e) => {
    e.preventDefault();
    if (!editModal?.id) return;
    try {
      setSaving(true);
      await updateHouse(editModal.id, { userId: currentUser.id, ...form });
      setEditModal(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Güncelleme hatası.'); }
    finally { setSaving(false); }
  };

  const openEdit = (house, ev) => {
    ev.stopPropagation();
    setForm({ title: house.title, address: house.address || '', city: house.city || '' });
    setEditModal(house);
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await deleteHouse(deleteConfirm.id);
      setDeleteConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Silme hatası.'); }
  };

  const initials = (name = '') =>
    name.split(' ').map(w => w[0]).join('').slice(0,2).toUpperCase();


  return (
    <div>
      <div className="page-header">
        <div className="page-header-title">
          <h1>🏘️ Evlerim</h1>
          <p>Üye olduğunuz evleri görüntüleyin ve yönetin.</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setForm({ title:'', address:'', city:'' }); setCreateModal(true); }}>
          ＋ Yeni Ev
        </button>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : houses.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">🏠</div>
          <h3>Henüz ev yok</h3>
          <p>İlk evi oluşturun.</p>
          <button className="btn btn-primary" onClick={() => setCreateModal(true)}>＋ Ev Oluştur</button>
        </div>
      ) : (
        <div className="grid-2">
          {houses.map(house => {
            const members = Array.isArray(house.members) ? house.members : [];
            return (
              <div key={house.id} className="house-card" onClick={() => navigate(`/houses/${house.id}`)}>
                <div className="house-card-title">{house.title}</div>
                <div className="house-card-meta">
                  {house.city    && <div className="meta-row">📍 {house.city}{house.address ? `, ${house.address}` : ''}</div>}
                  {house.inviteCode && <div className="meta-row"><span className="invite-code">🔑 {house.inviteCode}</span></div>}
                </div>
                <div className="house-card-footer">
                  <div className="member-avatars">
                    {members.slice(0, 3).map((m, i) => (
                      <div key={i} className="member-avatar" title={m.name}>{initials(m.name || '?')}</div>
                    ))}
                    {members.length > 3 && (
                      <div className="member-avatar" style={{ background:'var(--bg-card)', color:'var(--text-muted)', fontSize:'0.65rem' }}>
                        +{members.length - 3}
                      </div>
                    )}
                    <span className="member-count-badge">{members.length} üye</span>
                  </div>
                  <div className="flex gap-8" onClick={e => e.stopPropagation()}>
                    <button className="btn btn-secondary btn-sm" onClick={e => openEdit(house, e)}>✏️</button>
                    <button className="btn btn-danger btn-sm" onClick={e => { e.stopPropagation(); setDeleteConfirm(house); }}>🗑️</button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {createModal && (
        <Modal title="🏠 Yeni Ev Oluştur" onClose={() => setCreateModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving}>{saving ? '...' : 'Oluştur'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Ev Adı *</label>
            <input className="form-input" placeholder="Örn: Yazlık" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Şehir</label>
            <input className="form-input" placeholder="Ankara" value={form.city}
              onChange={e => setForm(f => ({ ...f, city: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Adres</label>
            <input className="form-input" placeholder="Sokak No Daire" value={form.address}
              onChange={e => setForm(f => ({ ...f, address: e.target.value }))} />
          </div>
        </Modal>
      )}

      {editModal && (
        <Modal title="✏️ Evi Düzenle" onClose={() => setEditModal(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setEditModal(null)}>İptal</button>
            <button className="btn btn-primary" onClick={handleEdit} disabled={saving}>{saving ? '...' : 'Kaydet'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Ev Adı *</label>
            <input className="form-input" placeholder="Örn: Yazlık" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Şehir</label>
            <input className="form-input" placeholder="Ankara" value={form.city}
              onChange={e => setForm(f => ({ ...f, city: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Adres</label>
            <input className="form-input" placeholder="Sokak No Daire" value={form.address}
              onChange={e => setForm(f => ({ ...f, address: e.target.value }))} />
          </div>
        </Modal>
      )}

      {/* Delete Confirm */}
      {deleteConfirm && (
        <Modal title="🗑️ Evi Sil" onClose={() => setDeleteConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setDeleteConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{color:'var(--text-primary)'}}>{deleteConfirm.title}</strong> adlı evi silmek istediğinizden emin misiniz?</p>
          <p style={{marginTop:8, fontSize:'0.82rem', color:'var(--danger)'}}>⚠️ Bu işlem geri alınamaz. Ev boş olmalıdır.</p>
        </Modal>
      )}
    </div>
  );
}
