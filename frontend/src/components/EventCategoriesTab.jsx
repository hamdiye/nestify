import { useState, useEffect } from 'react';
import {
  getEventCategoriesFromHouse, createEventCategory, updateEventCategory, deleteEventCategory
} from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from './Modal';

export default function EventCategoriesTab({ houseId }) {
  const { currentUser } = useUser();
  const [categories, setCategories] = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState('');

  const [createModal, setCreateModal] = useState(false);
  const [editModal,   setEditModal]   = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [saving, setSaving] = useState(false);

  const emptyForm = { title:'', description:'', colorCode:'#7c3aed' };
  const [form, setForm] = useState(emptyForm);

  const load = async () => {
    try {
      setLoading(true);
      const res = await getEventCategoriesFromHouse(houseId);
      setCategories(res.data || []);
    } catch { setError('Kategoriler yüklenemedi.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [houseId]);

  const handleCreate = async () => {
    if (!form.title || !form.colorCode) return;
    try {
      setSaving(true);
      await createEventCategory({ userId: currentUser.id, houseId: Number(houseId), ...form });
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
      await updateEventCategory({
        userId: currentUser.id,
        houseId: Number(houseId),
        eventCategoryId: editModal.id,
        ...form
      });
      setEditModal(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Güncelleme hatası.'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    try {
      await deleteEventCategory({ actingUserId: currentUser.id, houseId: Number(houseId), categoryId: deleteConfirm.id });
      setDeleteConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Silme hatası.'); }
  };

  const openEdit = (cat) => {
    setForm({ title: cat.title, description: cat.description || '', colorCode: cat.colorCode });
    setEditModal(cat);
  };


  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      <div className="section-header">
        <h3>🏷️ Etkinlik Kategorileri ({categories.length})</h3>
        <button className="btn btn-primary btn-sm" onClick={() => { setForm(emptyForm); setCreateModal(true); }}>
          ＋ Kategori Ekle
        </button>
      </div>

      {categories.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">🏷️</div>
          <h3>Kategori bulunamadı</h3>
          <p>Etkinliklerinizi düzenlemek için kategori oluşturun.</p>
          <button className="btn btn-primary btn-sm" onClick={() => setCreateModal(true)}>＋ Ekle</button>
        </div>
      ) : (
        <div className="grid-3">
          {categories.map(cat => (
            <div key={cat.id} className="card" style={{ borderLeft: `4px solid ${cat.colorCode}` }}>
              <div className="flex items-center gap-12 mb-16">
                <div className="color-dot" style={{ width:16, height:16, background: cat.colorCode }} />
                <h4 style={{ flex:1 }}>{cat.title}</h4>
              </div>
              {cat.description && <p style={{ fontSize:'0.82rem', marginBottom:16 }}>{cat.description}</p>}
              <div className="flex gap-8">
                <button className="btn btn-secondary btn-sm" onClick={() => openEdit(cat)}>✏️ Düzenle</button>
                <button className="btn btn-danger btn-sm" onClick={() => setDeleteConfirm(cat)}>🗑️</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {createModal && (
        <Modal title="🏷️ Yeni Kategori" onClose={() => setCreateModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving}>{saving ? '...' : 'Oluştur'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Örn: Temizlik" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Kısa açıklama..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Renk Kodu</label>
            <div className="color-preview">
              <input type="color" value={form.colorCode}
                onChange={e => setForm(f => ({ ...f, colorCode: e.target.value }))}
                style={{ width:40, height:40, border:'none', background:'none', cursor:'pointer', padding:0 }} />
              <input className="form-input" placeholder="#7c3aed" value={form.colorCode}
                onChange={e => setForm(f => ({ ...f, colorCode: e.target.value }))} style={{ fontFamily:'monospace' }} />
              <div className="dot" style={{ background: form.colorCode }} />
            </div>
          </div>
        </Modal>
      )}

      {editModal && (
        <Modal title="✏️ Kategoriyi Düzenle" onClose={() => setEditModal(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setEditModal(null)}>İptal</button>
            <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>{saving ? '...' : 'Kaydet'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Örn: Temizlik" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Kısa açıklama..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div className="form-group">
            <label className="form-label">Renk Kodu</label>
            <div className="color-preview">
              <input type="color" value={form.colorCode}
                onChange={e => setForm(f => ({ ...f, colorCode: e.target.value }))}
                style={{ width:40, height:40, border:'none', background:'none', cursor:'pointer', padding:0 }} />
              <input className="form-input" placeholder="#7c3aed" value={form.colorCode}
                onChange={e => setForm(f => ({ ...f, colorCode: e.target.value }))} style={{ fontFamily:'monospace' }} />
              <div className="dot" style={{ background: form.colorCode }} />
            </div>
          </div>
        </Modal>
      )}

      {/* Delete */}
      {deleteConfirm && (
        <Modal title="🗑️ Kategoriyi Sil" onClose={() => setDeleteConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setDeleteConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{color:'var(--text-primary)'}}>{deleteConfirm.title}</strong> kategorisini silmek istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
