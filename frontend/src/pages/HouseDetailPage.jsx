import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getHouseById } from '../api/api';
import MembersTab from '../components/MembersTab';
import EventsTab from '../components/EventsTab';
import EventCategoriesTab from '../components/EventCategoriesTab';
import HouseNeedsTab from '../components/HouseNeedsTab';

const TABS = [
  { id: 'members',    icon: '👥', label: 'Üyeler'      },
  { id: 'events',     icon: '📅', label: 'Etkinlikler' },
  { id: 'categories', icon: '🏷️', label: 'Kategoriler' },
  { id: 'needs',      icon: '🛒', label: 'İhtiyaçlar'  },
];

export default function HouseDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [house,   setHouse]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('members');

  useEffect(() => {
    (async () => {
      try {
        const res = await getHouseById(id);
        setHouse(res.data);
      } catch {
        navigate('/houses');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  if (loading) return (
    <div className="loading"><div className="spinner" /><p>Ev yükleniyor...</p></div>
  );

  if (!house) return null;

  const members = Array.isArray(house.members) ? house.members : [];

  return (
    <div>
      {/* Breadcrumb */}
      <div className="flex items-center gap-8 mb-24" style={{ fontSize:'0.85rem', color:'var(--text-muted)' }}>
        <span style={{ cursor:'pointer', color:'var(--text-secondary)' }} onClick={() => navigate('/houses')}>
          🏘️ Evler
        </span>
        <span>›</span>
        <span style={{ color:'var(--text-primary)', fontWeight:600 }}>{house.title}</span>
      </div>

      {/* House Header */}
      <div className="card mb-24" style={{ background:'var(--accent-grad-soft)', border:'1px solid rgba(124,58,237,.2)' }}>
        <div className="flex items-center justify-between gap-16" style={{ flexWrap:'wrap' }}>
          <div>
            <h1 style={{ marginBottom:8 }}>{house.title}</h1>
            <div className="flex gap-16" style={{ flexWrap:'wrap' }}>
              {house.city && (
                <span className="meta-row" style={{ fontSize:'0.875rem' }}>
                  📍 {house.city}{house.address ? `, ${house.address}` : ''}
                </span>
              )}
              <span className="invite-code">🔑 {house.inviteCode}</span>
              <span style={{ fontSize:'0.82rem', color:'var(--text-muted)' }}>
                👥 {members.length} üye
              </span>
            </div>
          </div>
          <div style={{ textAlign:'right' }}>
            <div style={{ fontSize:'0.72rem', color:'var(--text-muted)', marginBottom:4 }}>OLUŞTURULMA</div>
            <div style={{ fontSize:'0.875rem', color:'var(--text-secondary)' }}>
              {house.createdAt ? new Date(house.createdAt).toLocaleDateString('tr-TR') : '—'}
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="tabs">
        {TABS.map(tab => (
          <button
            key={tab.id}
            className={`tab${activeTab === tab.id ? ' active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.icon} {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'members'    && <MembersTab          houseId={id} />}
      {activeTab === 'events'     && <EventsTab           houseId={id} />}
      {activeTab === 'categories' && <EventCategoriesTab  houseId={id} />}
      {activeTab === 'needs'      && <HouseNeedsTab       houseId={id} />}
    </div>
  );
}
