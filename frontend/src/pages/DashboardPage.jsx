import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUserHouses, getEventsFromHouse, getHouseNeedsFromHouse } from '../api/api';
import { useUser } from '../context/UserContext';

export default function DashboardPage() {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const [houses, setHouses] = useState([]);
  const [events, setEvents] = useState([]);
  const [needs, setNeeds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const houseRes = await getUserHouses(currentUser.id);
      const userHouses = houseRes.data || [];
      setHouses(userHouses);

      if (userHouses.length === 0) {
        setEvents([]);
        setNeeds([]);
        return;
      }

      // Her evden paralel olarak etkinlik ve ihtiyaçları topla
      const eventsPromises = userHouses.map(async (h) => {
        try {
          const res = await getEventsFromHouse(h.id);
          return (res.data || []).map(ev => ({
            ...ev,
            houseId: h.id,
            houseTitle: h.title,
          }));
        } catch {
          return [];
        }
      });

      const needsPromises = userHouses.map(async (h) => {
        try {
          const res = await getHouseNeedsFromHouse(h.id);
          return (res.data || []).map(nd => ({
            ...nd,
            houseId: h.id,
            houseTitle: h.title,
          }));
        } catch {
          return [];
        }
      });

      const [allEventsNested, allNeedsNested] = await Promise.all([
        Promise.all(eventsPromises),
        Promise.all(needsPromises),
      ]);

      const flatEvents = allEventsNested.flat();
      // Tamamlanan ihtiyaçları filtrele (sadece aktif / bekleyen ihtiyaçlar görünsün)
      const flatNeeds = allNeedsNested.flat().filter(nd => nd.status !== 'COMPLETED' && nd.status !== 'DONE');

      // Etkinlikleri tarihe göre sırala
      flatEvents.sort((a, b) => {
        const dateA = a.startedDate ? new Date(a.startedDate).getTime() : 0;
        const dateB = b.startedDate ? new Date(b.startedDate).getTime() : 0;
        return dateA - dateB;
      });

      // İhtiyaçları duruma ve tarihe göre sırala (PENDING en başta)
      const statusOrder = { PENDING: 0, PURCHASED: 1, CANCELLED: 2 };
      flatNeeds.sort((a, b) => {
        const orderDiff = (statusOrder[a.status] ?? 3) - (statusOrder[b.status] ?? 3);
        if (orderDiff !== 0) return orderDiff;
        const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        return dateB - dateA;
      });

      setEvents(flatEvents);
      setNeeds(flatNeeds);
    } catch {
      setError('Veriler yüklenirken bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (currentUser?.id) loadData();
  }, [currentUser]);

  const totalMembers = houses.reduce((acc, h) => {
    const m = Array.isArray(h.members) ? h.members : [];
    return acc + m.length;
  }, 0);

  const pendingNeedsCount = needs.filter(n => n.status === 'PENDING').length;

  const formatEventDate = (dt, isAllDay) => {
    if (!dt) return 'Tarih belirtilmedi';
    try {
      const d = new Date(dt);
      if (isNaN(d.getTime())) return dt;
      if (isAllDay) {
        return d.toLocaleDateString('tr-TR', { day: 'numeric', month: 'short', weekday: 'short' }) + ' (Tüm Gün)';
      }
      return d.toLocaleDateString('tr-TR', {
        day: 'numeric',
        month: 'short',
        weekday: 'short',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dt;
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return <span className="badge badge-pending">⏳ Bekliyor</span>;
      case 'PURCHASED':
        return <span className="badge badge-purchased" style={{ background: 'rgba(59,130,246,0.15)', color: '#60a5fa' }}>🛍️ Alındı</span>;
      case 'COMPLETED':
        return <span className="badge badge-completed" style={{ background: 'rgba(16,185,129,0.15)', color: '#34d399' }}>✅ Tamamlandı</span>;
      default:
        return <span className="badge">{status}</span>;
    }
  };

  return (
    <div>
      {/* Welcome Banner */}
      <div className="welcome-banner">
        <h1>Merhaba, {currentUser?.name?.split(' ')[0] || 'Kullanıcı'} 👋</h1>
        <p>Evlerinizdeki yaklaşan etkinlikleri ve ortak ihtiyaçları buradan takip edebilirsiniz.</p>
      </div>

      {/* Stats Cards */}
      <div className="grid-4 mb-32">
        {[
          { icon: '🏠', label: 'Evlerim',            value: houses.length,        cls: 'purple', onClick: () => navigate('/houses') },
          { icon: '📅', label: 'Toplam Etkinlik',    value: events.length,        cls: 'green'  },
          { icon: '🛒', label: 'Bekleyen İhtiyaç',   value: pendingNeedsCount,    cls: 'amber'  },
          { icon: '👥', label: 'Toplam Ev Sakini',   value: totalMembers,         cls: 'blue'   },
        ].map(s => (
          <div
            key={s.label}
            className="stat-card"
            style={s.onClick ? { cursor: 'pointer' } : {}}
            onClick={s.onClick}
          >
            <div className={`stat-icon ${s.cls}`}>{s.icon}</div>
            <div className="stat-info">
              <div className="value">{s.value}</div>
              <div className="label">{s.label}</div>
            </div>
          </div>
        ))}
      </div>

      {error && (
        <div className="alert alert-error mb-24" onClick={() => setError('')}>
          ⚠️ {error}
        </div>
      )}

      {loading ? (
        <div className="loading"><div className="spinner" /><p>Yükleniyor...</p></div>
      ) : (
        <div className="grid-2" style={{ alignItems: 'start', gap: 24 }}>
          {/* Yaklaşan Etkinlikler Bölümü */}
          <div className="card">
            <div className="flex items-center justify-between mb-16">
              <div className="flex items-center gap-8">
                <span style={{ fontSize: '1.4rem' }}>📅</span>
                <h2 style={{ fontSize: '1.15rem', margin: 0 }}>Yaklaşan Etkinlikler</h2>
              </div>
              <span className="badge badge-member">{events.length} etkinlik</span>
            </div>

            {events.length === 0 ? (
              <div className="empty-state" style={{ padding: '32px 16px' }}>
                <div className="emoji" style={{ fontSize: '2.2rem' }}>🎉</div>
                <h4 style={{ margin: '8px 0 4px', color: 'var(--text-primary)' }}>Etkinlik Yok</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  Şu an için planlanmış bir etkinlik bulunmuyor.
                </p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {events.map((ev) => (
                  <div
                    key={`${ev.houseId}-${ev.id}`}
                    onClick={() => navigate(`/houses/${ev.houseId}?tab=events&eventId=${ev.id}`)}
                    style={{
                      padding: 14,
                      borderRadius: 10,
                      background: 'var(--bg-card)',
                      border: '1px solid var(--border)',
                      cursor: 'pointer',
                      transition: 'all .2s ease',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 6,
                    }}
                    onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--accent-1)'}
                    onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--border)'}
                  >
                    <div className="flex items-center justify-between gap-8">
                      <strong style={{ fontSize: '0.95rem', color: 'var(--text-primary)' }}>
                        {ev.title}
                      </strong>
                      <span className="badge" style={{ background: 'rgba(124,58,237,0.12)', color: 'var(--accent-1)', fontSize: '0.72rem' }}>
                        🏠 {ev.houseTitle}
                      </span>
                    </div>

                    {ev.description && (
                      <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {ev.description}
                      </p>
                    )}

                    <div className="flex items-center gap-16" style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: 2, flexWrap: 'wrap' }}>
                      <span>🕒 {formatEventDate(ev.startedDate, ev.isAllDay)}</span>
                      {ev.location && <span>📍 {ev.location}</span>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* İhtiyaçlar Listesi Bölümü */}
          <div className="card">
            <div className="flex items-center justify-between mb-16">
              <div className="flex items-center gap-8">
                <span style={{ fontSize: '1.4rem' }}>🛒</span>
                <h2 style={{ fontSize: '1.15rem', margin: 0 }}>İhtiyaçlar Listesi</h2>
              </div>
              <span className="badge badge-member">{needs.length} bekleyen ihtiyaç</span>
            </div>

            {needs.length === 0 ? (
              <div className="empty-state" style={{ padding: '32px 16px' }}>
                <div className="emoji" style={{ fontSize: '2.2rem' }}>🎉</div>
                <h4 style={{ margin: '8px 0 4px', color: 'var(--text-primary)' }}>Bekleyen İhtiyaç Yok</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  Evlerinizdeki tüm ihtiyaçlar tamamlandı veya henüz yeni ihtiyaç eklenmedi.
                </p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {needs.map((nd) => (
                  <div
                    key={`${nd.houseId}-${nd.id}`}
                    onClick={() => navigate(`/houses/${nd.houseId}?tab=needs&needId=${nd.id}`)}
                    style={{
                      padding: 14,
                      borderRadius: 10,
                      background: 'var(--bg-card)',
                      border: '1px solid var(--border)',
                      cursor: 'pointer',
                      transition: 'all .2s ease',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 6,
                    }}
                    onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--accent-1)'}
                    onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--border)'}
                  >
                    <div className="flex items-center justify-between gap-8">
                      <span style={{
                        fontSize: '0.95rem',
                        fontWeight: 600,
                        color: nd.status === 'COMPLETED' ? 'var(--text-muted)' : 'var(--text-primary)',
                        textDecoration: nd.status === 'COMPLETED' ? 'line-through' : 'none',
                      }}>
                        {nd.title}
                      </span>
                      {getStatusBadge(nd.status)}
                    </div>

                    {nd.description && (
                      <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0 }}>
                        {nd.description}
                      </p>
                    )}

                    <div className="flex items-center justify-between" style={{ marginTop: 2 }}>
                      <span className="badge" style={{ background: 'rgba(124,58,237,0.12)', color: 'var(--accent-1)', fontSize: '0.72rem' }}>
                        🏠 {nd.houseTitle}
                      </span>
                      {nd.createdAt && (
                        <span style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
                          {new Date(nd.createdAt).toLocaleDateString('tr-TR')}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
