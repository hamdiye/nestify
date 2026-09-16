import { useState, useEffect } from 'react';
import { useUser } from '../context/UserContext';
import {
  getUserHouses,
  getHouseNeedsFromHouse,
  createHouseNeed,
  updateHouseNeed,
  deleteHouseNeed,
} from '../api/api';
import Modal from '../components/Modal';

const STATUS_OPTIONS = ['PENDING', 'COMPLETED', 'CANCELLED'];
const STATUS_LABELS  = { PENDING: '⏳ Bekliyor', COMPLETED: '✅ Tamamlandı', CANCELLED: '❌ İptal' };
const STATUS_BADGE   = { PENDING: 'badge-pending', COMPLETED: 'badge-completed', CANCELLED: 'badge-cancelled' };

export default function NeedsPage() {
  const { currentUser } = useUser();

  const [houses, setHouses]             = useState([]);
  const [needsByHouse, setNeedsByHouse] = useState({});
  const [selectedHouseId, setSelectedHouseId] = useState('all');
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState('');
  const [modalError, setModalError]     = useState('');

  const [createModal, setCreateModal]   = useState(false);
  const [editModal, setEditModal]       = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [saving, setSaving]             = useState(false);

  const emptyForm = { title: '', description: '', status: 'PENDING', houseId: '' };
  const [form, setForm] = useState(emptyForm);

  /**
   * Loads all houses of the current user and fetches needs for each house in parallel.
   */
  const load = async () => {
    try {
      setLoading(true);
      setError('');
      const houseRes = await getUserHouses(currentUser.id);
      const userHouses = houseRes.data || [];
      setHouses(userHouses);

      const results = await Promise.all(
        userHouses.map(async (h) => {
          try {
            const res = await getHouseNeedsFromHouse(h.id);
            return { houseId: h.id, needs: res.data || [] };
          } catch {
            return { houseId: h.id, needs: [] };
          }
        })
      );

      const map = {};
      results.forEach(({ houseId, needs }) => { map[houseId] = needs; });
      setNeedsByHouse(map);
    } catch {
      setError('İhtiyaçlar yüklenemedi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [currentUser]);

  const allNeeds = selectedHouseId === 'all'
    ? Object.entries(needsByHouse).flatMap(([hid, needs]) =>
        needs.map(n => ({
          ...n,
          houseId: Number(hid),
          houseTitle: houses.find(h => h.id === Number(hid))?.title || '',
        }))
      )
    : (needsByHouse[selectedHouseId] || []).map(n => ({
        ...n,
        houseId: Number(selectedHouseId),
        houseTitle: houses.find(h => h.id === Number(selectedHouseId))?.title || '',
      }));

  const grouped = STATUS_OPTIONS.reduce((acc, s) => {
    acc[s] = allNeeds.filter(n => n.status === s);
    return acc;
  }, {});

  /**
   * Handles creation of a new house need.
   */
  const handleCreate = async () => {
    if (!form.title || !form.houseId) return;
    setModalError('');
    try {
      setSaving(true);
      await createHouseNeed({
        houseId: Number(form.houseId),
        createdById: currentUser.id,
        title: form.title,
        description: form.description,
        status: form.status,
      });
      setCreateModal(false);
      setForm(emptyForm);
      load();
    } catch (err) {
      setModalError(err.response?.data?.message || 'Oluşturma hatası.');
    } finally {
      setSaving(false);
    }
  };

  /**
   * Handles updating an existing house need.
   */
  const handleUpdate = async () => {
    if (!editModal) return;
    setModalError('');
    try {
      setSaving(true);
      await updateHouseNeed({
        id: editModal.id,
        houseId: Number(editModal.houseId || form.houseId),
        createdById: currentUser.id,
        title: form.title,
        description: form.description,
        status: form.status,
      });
      setEditModal(null);
      load();
    } catch (err) {
      setModalError(err.response?.data?.message || 'Güncelleme hatası.');
    } finally {
      setSaving(false);
    }
  };

  /**
   * Handles deletion of a house need.
   */
  const handleDelete = async () => {
    setModalError('');
    try {
      await deleteHouseNeed({
        houseNeedId: deleteConfirm.id,
        houseId: Number(deleteConfirm.houseId),
        userId: currentUser.id,
      });
      setDeleteConfirm(null);
      load();
    } catch (err) {
      setModalError(err.response?.data?.message || 'Silme hatası.');
    }
  };

  /**
   * Quickly updates the status of a house need without opening a modal.
   *
   * @param {Object} need   - The house need to update
   * @param {string} status - The new status value
   */
  const quickStatus = async (need, status) => {
    try {
      await updateHouseNeed({
        id: need.id,
        houseId: Number(need.houseId),
        createdById: currentUser.id,
        title: need.title,
        description: need.description || '',
        status,
      });
      load();
    } catch { /* ignore */ }
  };

  const openEdit = (need) => {
    setForm({ title: need.title, description: need.description || '', status: need.status, houseId: need.houseId });
    setEditModal(need);
  };

  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      {/* Sayfa Başlığı */}
      <div className="welcome-banner">
        <h1>🛒 İhtiyaçlar</h1>
        <p>Tüm evlerdeki alışveriş listesi ve ortak ihtiyaçları buradan yönetin.</p>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      {houses.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">🏡</div>
          <h3>Henüz bir eve üye değilsiniz</h3>
          <p>İhtiyaç oluşturmak için önce bir eve katılmanız gerekiyor.</p>
        </div>
      ) : (
        <>
          {/* Filtre + Ekle */}
          <div className="section-header" style={{ marginBottom: 24 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <label className="form-label" style={{ margin: 0, whiteSpace: 'nowrap' }}>Ev Filtresi:</label>
              <select
                className="form-select"
                style={{ minWidth: 180 }}
                value={selectedHouseId}
                onChange={e => setSelectedHouseId(e.target.value)}
              >
                <option value="all">Tüm Evler ({allNeeds.length})</option>
                {houses.map(h => (
                  <option key={h.id} value={h.id}>
                    {h.title} ({(needsByHouse[h.id] || []).length})
                  </option>
                ))}
              </select>
            </div>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => {
                setForm({ ...emptyForm, houseId: selectedHouseId !== 'all' ? selectedHouseId : houses[0]?.id || '' });
                setCreateModal(true);
              }}
            >
              ＋ İhtiyaç Ekle
            </button>
          </div>

          {/* İhtiyaç Listesi */}
          {allNeeds.length === 0 ? (
            <div className="empty-state card">
              <div className="emoji">🛒</div>
              <h3>İhtiyaç bulunamadı</h3>
              <p>Seçilen evde henüz bir ihtiyaç eklenmemiş.</p>
              <button className="btn btn-primary btn-sm" onClick={() => setCreateModal(true)}>＋ Ekle</button>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              {STATUS_OPTIONS.filter(s => grouped[s].length > 0).map(status => (
                <div key={status}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
                    <span className={`badge ${STATUS_BADGE[status]}`}>{STATUS_LABELS[status]}</span>
                    <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>({grouped[status].length})</span>
                  </div>
                  <div className="table-container">
                    <table>
                      <thead>
                        <tr>
                          <th>İhtiyaç</th>
                          <th>Açıklama</th>
                          {selectedHouseId === 'all' && <th>Ev</th>}
                          <th>Durum</th>
                          <th style={{ textAlign: 'right' }}>İşlemler</th>
                        </tr>
                      </thead>
                      <tbody>
                        {grouped[status].map(need => (
                          <tr key={need.id}>
                            <td><span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{need.title}</span></td>
                            <td style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                              {need.description || '—'}
                            </td>
                            {selectedHouseId === 'all' && (
                              <td><span className="badge" style={{ background: 'rgba(124,58,237,0.15)', color: 'var(--accent-1)' }}>{need.houseTitle}</span></td>
                            )}
                            <td>
                              <select
                                className="form-select"
                                style={{ padding: '4px 28px 4px 8px', fontSize: '0.78rem', maxWidth: 140 }}
                                value={need.status}
                                onChange={e => quickStatus(need, e.target.value)}
                              >
                                {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                              </select>
                            </td>
                            <td>
                              <div className="flex gap-8" style={{ justifyContent: 'flex-end' }}>
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
        </>
      )}

      {/* Oluşturma Modal */}
      {createModal && (
        <Modal title="🛒 Yeni İhtiyaç" onClose={() => { setCreateModal(false); setModalError(''); }}
          error={modalError}
          footer={<>
            <button className="btn btn-secondary" onClick={() => { setCreateModal(false); setModalError(''); }}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving || !form.title || !form.houseId}>
              {saving ? '...' : 'Ekle'}
            </button>
          </>}>
          <div className="form-group">
            <label className="form-label">Ev *</label>
            <select className="form-select" value={form.houseId} onChange={e => setForm(f => ({ ...f, houseId: e.target.value }))}>
              <option value="">Ev Seçin</option>
              {houses.map(h => <option key={h.id} value={h.id}>{h.title}</option>)}
            </select>
          </div>
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
            <select className="form-select" value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
              {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
            </select>
          </div>
        </Modal>
      )}

      {/* Düzenleme Modal */}
      {editModal && (
        <Modal title="✏️ İhtiyacı Düzenle" onClose={() => { setEditModal(null); setModalError(''); }}
          error={modalError}
          footer={<>
            <button className="btn btn-secondary" onClick={() => { setEditModal(null); setModalError(''); }}>İptal</button>
            <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>{saving ? '...' : 'Kaydet'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Durum</label>
            <select className="form-select" value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
              {STATUS_OPTIONS.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
            </select>
          </div>
        </Modal>
      )}

      {/* Silme Onayı */}
      {deleteConfirm && (
        <Modal title="🗑️ İhtiyacı Sil" onClose={() => { setDeleteConfirm(null); setModalError(''); }}
          error={modalError}
          footer={<>
            <button className="btn btn-secondary" onClick={() => { setDeleteConfirm(null); setModalError(''); }}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{ color: 'var(--text-primary)' }}>{deleteConfirm.title}</strong> öğesini silmek istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
