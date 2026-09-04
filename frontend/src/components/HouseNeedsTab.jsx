import { useState, useEffect } from 'react';
import {
  getHouseNeedsFromHouse, createHouseNeed, updateHouseNeed, deleteHouseNeed
} from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from './Modal';

const STATUS_OPTIONS = ['PENDING', 'DONE', 'CANCELLED'];
const STATUS_LABELS  = { PENDING: '⏳ Bekliyor', DONE: '✅ Tamamlandı', CANCELLED: '❌ İptal' };
const STATUS_BADGE   = { PENDING: 'badge-pending', DONE: 'badge-done', CANCELLED: 'badge-cancelled' };

export default function HouseNeedsTab({ houseId }) {
  const { currentUser } = useUser();
  const [needs,   setNeeds]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  const [createModal,   setCreateModal]   = useState(false);
  const [editModal,     setEditModal]     = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [saving,        setSaving]        = useState(false);

  const emptyForm = { title:'', description:'', status:'PENDING' };
  const [form, setForm] = useState(emptyForm);

  const load = async () => {
    try {
      setLoading(true);
      const res = await getHouseNeedsFromHouse(houseId);
      setNeeds(res.data || []);
    } catch { setError('İhtiyaçlar yüklenemedi.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [houseId]);

  const handleCreate = async () => {
    if (!form.title) return;
    try {
      setSaving(true);
      await createHouseNeed({
        houseId: Number(houseId),
        createdById: currentUser.id,
        title: form.title,
        description: form.description,
        status: form.status
      });
      setCreateModal(false);
      setForm(emptyForm);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Oluşturma hatası.'); }
    finally { setSaving(false); }
  };

  const handleUpdate = async () => {
    if (!editModal) return;
    try {
      setSaving(true);
      await updateHouseNeed({
        id: editModal.id,
        houseId: Number(houseId),
        createdById: currentUser.id,
        title: form.title,
        description: form.description,
        status: form.status
      });
      setEditModal(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Güncelleme hatası.'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    try {
      await deleteHouseNeed({ houseNeedId: deleteConfirm.id, houseId: Number(houseId), userId: currentUser.id });
      setDeleteConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Silme hatası.'); }
  };

  // Hızlı durum değiştirme
  const quickStatus = async (need, status) => {
    try {
      await updateHouseNeed({ id: need.id, houseId: Number(houseId), createdById: currentUser.id, title: need.title, description: need.description || '', status });
      load();
    } catch { /* ignore */ }
  };

  const openEdit = (need) => {
    setForm({ title: need.title, description: need.description || '', status: need.status });
    setEditModal(need);
  };


  if (loading) return <div className="loading"><div className="spinner" /></div>;

  const grouped = STATUS_OPTIONS.reduce((acc, s) => {
    acc[s] = needs.filter(n => n.status === s);
    return acc;
  }, {});

  return (
    <div>
      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      <div className="section-header">
        <h3>🛒 Ev İhtiyaçları ({needs.length})</h3>
        <button className="btn btn-primary btn-sm" onClick={() => { setForm(emptyForm); setCreateModal(true); }}>
          ＋ İhtiyaç Ekle
        </button>
      </div>

      {needs.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">🛒</div>
          <h3>İhtiyaç bulunamadı</h3>
          <p>Ev alışveriş listesini veya görevleri buradan yönetin.</p>
          <button className="btn btn-primary btn-sm" onClick={() => setCreateModal(true)}>＋ Ekle</button>
        </div>
      ) : (
        <div style={{ display:'flex', flexDirection:'column', gap:24 }}>
          {STATUS_OPTIONS.filter(s => grouped[s].length > 0).map(status => (
            <div key={status}>
              <div style={{ display:'flex', alignItems:'center', gap:8, marginBottom:12 }}>
                <span className={`badge ${STATUS_BADGE[status]}`}>{STATUS_LABELS[status]}</span>
                <span style={{ fontSize:'0.78rem', color:'var(--text-muted)' }}>({grouped[status].length})</span>
              </div>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>İhtiyaç</th>
                      <th>Açıklama</th>
                      <th>Durum</th>
                      <th style={{ textAlign:'right' }}>İşlemler</th>
                    </tr>
                  </thead>
                  <tbody>
                    {grouped[status].map(need => (
                      <tr key={need.id}>
                        <td>
                          <span style={{ fontWeight:600, color:'var(--text-primary)' }}>{need.title}</span>
                        </td>
                        <td style={{ maxWidth:200, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>
                          {need.description || '—'}
                        </td>
                        <td>
                          <select
                            className="form-select"
                            style={{ padding:'4px 28px 4px 8px', fontSize:'0.78rem', maxWidth:140 }}
                            value={need.status}
                            onChange={e => quickStatus(need, e.target.value)}
                          >
                            {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                          </select>
                        </td>
                        <td>
                          <div className="flex gap-8" style={{ justifyContent:'flex-end' }}>
                            <button className="btn btn-secondary btn-sm" onClick={() => openEdit(need)}>✏️</button>
                            <button className="btn btn-danger btn-sm" onClick={() => setDeleteConfirm(need)}>🗑️</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          ))}
        </div>
      )}

      {createModal && (
        <Modal title="🛒 Yeni İhtiyaç" onClose={() => setCreateModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving}>{saving?'...':'Ekle'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Örn: Deterjan al" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Detaylar..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Durum</label>
            <select className="form-select" value={form.status}
              onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
              {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
            </select>
          </div>
        </Modal>
      )}

      {editModal && (
        <Modal title="✏️ İhtiyacı Düzenle" onClose={() => setEditModal(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setEditModal(null)}>İptal</button>
            <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>{saving?'...':'Kaydet'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Örn: Deterjan al" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Detaylar..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Durum</label>
            <select className="form-select" value={form.status}
              onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
              {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
            </select>
          </div>
        </Modal>
      )}

      {deleteConfirm && (
        <Modal title="🗑️ İhtiyacı Sil" onClose={() => setDeleteConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setDeleteConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{color:'var(--text-primary)'}}>{deleteConfirm.title}</strong> öğesini silmek istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
