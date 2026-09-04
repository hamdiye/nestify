import { useState, useEffect } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { getHouseById, removeMemberFromHouse } from '../api/api';
import { useUser } from '../context/UserContext';
import Modal from '../components/Modal';
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

function CopyButton({ text }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(text);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      /* fallback */
    }
  };

  return (
    <button
      onClick={handleCopy}
      title="Kopyala"
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 4,
        padding: '3px 10px', borderRadius: 6, fontSize: '0.75rem',
        background: copied ? 'rgba(16,185,129,.15)' : 'rgba(124,58,237,.12)',
        border: `1px solid ${copied ? 'rgba(16,185,129,.35)' : 'rgba(124,58,237,.3)'}`,
        color: copied ? '#34d399' : 'var(--accent-1)',
        cursor: 'pointer', transition: 'all .2s', fontFamily: 'inherit', fontWeight: 600,
        whiteSpace: 'nowrap',
      }}
    >
      {copied ? '✓ Kopyalandı!' : '📋 Kopyala'}
    </button>
  );
}

export default function HouseDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { currentUser } = useUser();
  const [house,   setHouse]   = useState(null);
  const [loading, setLoading] = useState(true);

  const urlTab = searchParams.get('tab');
  const [activeTab, setActiveTab] = useState(urlTab && TABS.some(t => t.id === urlTab) ? urlTab : 'members');
  const [leaveConfirm, setLeaveConfirm] = useState(false);
  const [leaving, setLeaving] = useState(false);

  useEffect(() => {
    const t = searchParams.get('tab');
    if (t && TABS.some(tab => tab.id === t)) {
      setActiveTab(t);
    }
  }, [searchParams]);

  const switchTab = (tabId) => {
    setActiveTab(tabId);
    setSearchParams({ tab: tabId });
  };

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

  const handleLeaveHouse = async () => {
    try {
      setLeaving(true);
      await removeMemberFromHouse(house.id, { userId: currentUser.id });
      setLeaveConfirm(false);
      navigate('/houses');
    } catch (err) {
      alert(err.response?.data?.message || 'Evden ayrılırken bir hata oluştu.');
    } finally {
      setLeaving(false);
    }
  };

  if (loading) return (
    <div className="loading"><div className="spinner" /><p>Ev yükleniyor...</p></div>
  );

  if (!house) return null;

  const members = Array.isArray(house.members) ? house.members : [];
  const currentMember = members.find(m => m.id === currentUser?.id);
  const isNonAdmin = currentMember && currentMember.role !== 'ADMIN';

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
              <div style={{ display:'flex', flexDirection:'column', gap:6 }}>
                <div style={{ display:'flex', alignItems:'center', gap:10, flexWrap:'wrap' }}>
                  <span className="invite-code">🔑 {house.inviteCode}</span>
                  <CopyButton text={house.inviteCode} />
                </div>
                <p style={{ fontSize:'0.78rem', color:'var(--text-muted)', margin:0 }}>
                  💡 Eve üye eklemek istediğiniz kullanıcıyla bu kodu paylaşın. Kullanıcı bu kodla eve katılabilir.
                </p>
              </div>
              <span style={{ fontSize:'0.82rem', color:'var(--text-muted)' }}>
                👥 {members.length} üye
              </span>
            </div>
          </div>
          <div style={{ textAlign:'right', display:'flex', flexDirection:'column', alignItems:'flex-end', gap:10 }}>
            <div>
              <div style={{ fontSize:'0.72rem', color:'var(--text-muted)', marginBottom:4 }}>OLUŞTURULMA</div>
              <div style={{ fontSize:'0.875rem', color:'var(--text-secondary)' }}>
                {house.createdAt ? new Date(house.createdAt).toLocaleDateString('tr-TR') : '—'}
              </div>
            </div>
            {isNonAdmin && (
              <button
                className="btn btn-danger btn-sm"
                onClick={() => setLeaveConfirm(true)}
                style={{ display:'inline-flex', alignItems:'center', gap:6 }}
              >
                🚪 Evden Ayrıl
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="tabs">
        {TABS.map(tab => (
          <button
            key={tab.id}
            className={`tab${activeTab === tab.id ? ' active' : ''}`}
            onClick={() => switchTab(tab.id)}
          >
            {tab.icon} {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'members'    && <MembersTab          houseId={id} />}
      {activeTab === 'events'     && <EventsTab           houseId={id} highlightEventId={searchParams.get('eventId')} />}
      {activeTab === 'categories' && <EventCategoriesTab  houseId={id} />}
      {activeTab === 'needs'      && <HouseNeedsTab       houseId={id} highlightNeedId={searchParams.get('needId')} />}

      {/* Leave House Confirm Modal */}
      {leaveConfirm && (
        <Modal
          title="🚪 Evden Ayrıl"
          onClose={() => setLeaveConfirm(false)}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setLeaveConfirm(false)}>İptal</button>
            <button className="btn btn-danger" onClick={handleLeaveHouse} disabled={leaving}>
              {leaving ? 'Ayrılınıyor...' : 'Evet, Ayrıl'}
            </button>
          </>}
        >
          <p><strong style={{ color:'var(--text-primary)' }}>{house.title}</strong> adlı evden ayrılmak istediğinizden emin misiniz?</p>
          <p style={{ marginTop:8, fontSize:'0.82rem', color:'var(--danger)' }}>
            ⚠️ Evden ayrıldıktan sonra tekrar katılabilmek için evin davet koduna ihtiyacınız olacaktır.
          </p>
        </Modal>
      )}
    </div>
  );
}
