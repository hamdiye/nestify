import { useState, useEffect } from 'react';
import {
  getEventsFromHouse, createEvent, updateEvent, deleteEvent,
  getEventCategoriesFromHouse, getHouseMembers
} from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from './Modal';

export default function EventsTab({ houseId, highlightEventId }) {
  const { currentUser } = useUser();
  const [events,     setEvents]     = useState([]);
  const [categories, setCategories] = useState([]);
  const [members,    setMembers]    = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState('');

  const [createModal,   setCreateModal]   = useState(false);
  const [editModal,     setEditModal]     = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [saving,        setSaving]        = useState(false);

  const emptyForm = {
    title:'', description:'', startedDate:'', endDate:'',
    isAllDay: false, location:'', eventCategoryId:'', assignedUserId:''
  };
  const [form, setForm] = useState(emptyForm);

  const load = async () => {
    try {
      setLoading(true);
      const [evRes, catRes, memRes] = await Promise.all([
        getEventsFromHouse(houseId),
        getEventCategoriesFromHouse(houseId),
        getHouseMembers(houseId)
      ]);
      setEvents(evRes.data || []);
      setCategories(catRes.data || []);
      setMembers(memRes.data || []);
    } catch { setError('Etkinlikler yüklenemedi.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [houseId]);

  useEffect(() => {
    if (!loading && highlightEventId) {
      const timer = setTimeout(() => {
        const el = document.getElementById(`event-${highlightEventId}`);
        if (el) {
          el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      }, 150);
      return () => clearTimeout(timer);
    }
  }, [loading, highlightEventId, events]);

  const toDatetimeLocal = (dt) => dt ? dt.replace(' ','T').slice(0,16) : '';
  const toIso = (s) => s ? s + ':00' : null;

  const handleCreate = async () => {
    if (!form.title) return;
    try {
      setSaving(true);
      await createEvent({
        houseId: Number(houseId),
        assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : currentUser.id,
        eventCategoryId: form.eventCategoryId ? Number(form.eventCategoryId) : null,
        title: form.title,
        description: form.description,
        startedDate: toIso(form.startedDate),
        endDate: toIso(form.endDate),
        isAllDay: form.isAllDay,
        location: form.location
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
      await updateEvent(editModal.id, {
        houseId: Number(houseId),
        assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : currentUser.id,
        eventCategoryId: form.eventCategoryId ? Number(form.eventCategoryId) : null,
        title: form.title,
        description: form.description,
        startedDate: toIso(form.startedDate),
        endDate: toIso(form.endDate),
        isAllDay: form.isAllDay,
        location: form.location
      });
      setEditModal(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Güncelleme hatası.'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    try {
      await deleteEvent({ eventId: deleteConfirm.id, houseId: Number(houseId), userId: currentUser.id });
      setDeleteConfirm(null);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Silme hatası.'); }
  };

  const openEdit = (ev) => {
    setForm({
      title: ev.title || '',
      description: ev.description || '',
      startedDate: toDatetimeLocal(ev.startedDate),
      endDate: toDatetimeLocal(ev.endDate),
      isAllDay: ev.isAllDay || false,
      location: ev.location || '',
      eventCategoryId: ev.eventCategoryId || '',
      assignedUserId: ev.assignedUserId || ''
    });
    setEditModal(ev);
  };

  const getCategoryColor = (id) => {
    const cat = categories.find(c => c.id === id);
    return cat?.colorCode || 'var(--accent-1)';
  };

  const getCategoryName = (id) => {
    const cat = categories.find(c => c.id === id);
    return cat?.title || '—';
  };

  const getMemberName = (id) => {
    const m = members.find(m => m.id === id);
    return m?.name || '—';
  };


  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      {error && <div className="alert alert-error" onClick={() => setError('')}>⚠️ {error}</div>}

      <div className="section-header">
        <h3>📅 Etkinlikler ({events.length})</h3>
        <button className="btn btn-primary btn-sm" onClick={() => { setForm(emptyForm); setCreateModal(true); }}>
          ＋ Etkinlik Ekle
        </button>
      </div>

      {events.length === 0 ? (
        <div className="empty-state card">
          <div className="emoji">📅</div>
          <h3>Etkinlik bulunamadı</h3>
          <p>Planlarınızı organize etmek için etkinlik oluşturun.</p>
          <button className="btn btn-primary btn-sm" onClick={() => setCreateModal(true)}>＋ Ekle</button>
        </div>
      ) : (
        <div className="grid-2">
          {events.map(ev => {
            const catColor = getCategoryColor(ev.eventCategoryId);
            const isHighlighted = highlightEventId && Number(highlightEventId) === ev.id;
            return (
              <div
                key={ev.id}
                id={`event-${ev.id}`}
                className="card"
                style={{
                  borderLeft: `4px solid ${catColor}`,
                  padding: 20,
                  ...(isHighlighted ? {
                    border: '2px solid var(--accent-1)',
                    borderLeft: `5px solid ${catColor}`,
                    boxShadow: '0 0 0 4px rgba(124, 58, 237, 0.25), 0 8px 25px rgba(0, 0, 0, 0.4)',
                    transition: 'all 0.3s ease'
                  } : {})
                }}
              >
                <div className="flex items-center gap-12 mb-16">
                  <div style={{
                    width:8, height:8, borderRadius:'50%', background: catColor, flexShrink:0
                  }} />
                  <h4 style={{ flex:1, color:'var(--text-primary)' }}>{ev.title}</h4>
                  {isHighlighted && (
                    <span className="badge" style={{ background: 'rgba(124,58,237,0.2)', color: 'var(--accent-1)', fontSize: '0.68rem' }}>
                      🎯 Seçilen
                    </span>
                  )}
                  {ev.isAllDay && <span className="badge badge-member" style={{fontSize:'0.65rem'}}>Tüm Gün</span>}
                </div>
                {ev.description && <p style={{ fontSize:'0.82rem', marginBottom:12 }}>{ev.description}</p>}
                <div style={{ display:'flex', flexDirection:'column', gap:4, marginBottom:16 }}>
                  {ev.startedDate && (
                    <div style={{ fontSize:'0.78rem', color:'var(--text-muted)', display:'flex', gap:6 }}>
                      <span>🗓️</span>
                      <span>{new Date(ev.startedDate).toLocaleString('tr-TR')}</span>
                    </div>
                  )}
                  {ev.location && (
                    <div style={{ fontSize:'0.78rem', color:'var(--text-muted)', display:'flex', gap:6 }}>
                      <span>📍</span><span>{ev.location}</span>
                    </div>
                  )}
                  <div style={{ fontSize:'0.78rem', color:'var(--text-muted)', display:'flex', gap:6 }}>
                    <span>👤</span><span>{getMemberName(ev.assignedUserId)}</span>
                    <span>•</span><span>🏷️ {getCategoryName(ev.eventCategoryId)}</span>
                  </div>
                </div>
                <div className="flex gap-8">
                  <button className="btn btn-secondary btn-sm" onClick={() => openEdit(ev)}>✏️</button>
                  <button className="btn btn-danger btn-sm" onClick={() => setDeleteConfirm(ev)}>🗑️</button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {createModal && (
        <Modal title="📅 Yeni Etkinlik" onClose={() => setCreateModal(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>İptal</button>
            <button className="btn btn-primary" onClick={handleCreate} disabled={saving}>{saving?'...':'Oluştur'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Etkinlik adı" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Detaylar..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <div className="form-group">
              <label className="form-label">Başlangıç</label>
              <input className="form-input" type="datetime-local" value={form.startedDate}
                onChange={e => setForm(f => ({ ...f, startedDate: e.target.value }))} />
            </div>
            <div className="form-group">
              <label className="form-label">Bitiş</label>
              <input className="form-input" type="datetime-local" value={form.endDate}
                onChange={e => setForm(f => ({ ...f, endDate: e.target.value }))} />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Konum</label>
            <input className="form-input" placeholder="Yer / Adres" value={form.location}
              onChange={e => setForm(f => ({ ...f, location: e.target.value }))} />
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <div className="form-group">
              <label className="form-label">Kategori</label>
              <select className="form-select" value={form.eventCategoryId}
                onChange={e => setForm(f => ({ ...f, eventCategoryId: e.target.value }))}>
                <option value="">Seçiniz</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.title}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Atanan Kişi</label>
              <select className="form-select" value={form.assignedUserId}
                onChange={e => setForm(f => ({ ...f, assignedUserId: e.target.value }))}>
                <option value="">Seçiniz</option>
                {members.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
              </select>
            </div>
          </div>
          <div className="form-group">
            <label style={{ display:'flex', alignItems:'center', gap:8, cursor:'pointer' }}>
              <input type="checkbox" checked={form.isAllDay}
                onChange={e => setForm(f => ({ ...f, isAllDay: e.target.checked }))} />
              <span className="form-label" style={{ margin:0 }}>Tüm Gün</span>
            </label>
          </div>
        </Modal>
      )}

      {editModal && (
        <Modal title="✏️ Etkinliği Düzenle" onClose={() => setEditModal(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setEditModal(null)}>İptal</button>
            <button className="btn btn-primary" onClick={handleUpdate} disabled={saving}>{saving?'...':'Kaydet'}</button>
          </>}>
          <div className="form-group">
            <label className="form-label">Başlık *</label>
            <input className="form-input" placeholder="Etkinlik adı" value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))} autoFocus />
          </div>
          <div className="form-group">
            <label className="form-label">Açıklama</label>
            <textarea className="form-textarea" placeholder="Detaylar..." value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <div className="form-group">
              <label className="form-label">Başlangıç</label>
              <input className="form-input" type="datetime-local" value={form.startedDate}
                onChange={e => setForm(f => ({ ...f, startedDate: e.target.value }))} />
            </div>
            <div className="form-group">
              <label className="form-label">Bitiş</label>
              <input className="form-input" type="datetime-local" value={form.endDate}
                onChange={e => setForm(f => ({ ...f, endDate: e.target.value }))} />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Konum</label>
            <input className="form-input" placeholder="Yer / Adres" value={form.location}
              onChange={e => setForm(f => ({ ...f, location: e.target.value }))} />
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <div className="form-group">
              <label className="form-label">Kategori</label>
              <select className="form-select" value={form.eventCategoryId}
                onChange={e => setForm(f => ({ ...f, eventCategoryId: e.target.value }))}>
                <option value="">Seçiniz</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.title}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Atanan Kişi</label>
              <select className="form-select" value={form.assignedUserId}
                onChange={e => setForm(f => ({ ...f, assignedUserId: e.target.value }))}>
                <option value="">Seçiniz</option>
                {members.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
              </select>
            </div>
          </div>
          <div className="form-group">
            <label style={{ display:'flex', alignItems:'center', gap:8, cursor:'pointer' }}>
              <input type="checkbox" checked={form.isAllDay}
                onChange={e => setForm(f => ({ ...f, isAllDay: e.target.checked }))} />
              <span className="form-label" style={{ margin:0 }}>Tüm Gün</span>
            </label>
          </div>
        </Modal>
      )}

      {deleteConfirm && (
        <Modal title="🗑️ Etkinliği Sil" onClose={() => setDeleteConfirm(null)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setDeleteConfirm(null)}>İptal</button>
            <button className="btn btn-danger" onClick={handleDelete}>Evet, Sil</button>
          </>}>
          <p><strong style={{color:'var(--text-primary)'}}>{deleteConfirm.title}</strong> etkinliğini silmek istediğinizden emin misiniz?</p>
        </Modal>
      )}
    </div>
  );
}
