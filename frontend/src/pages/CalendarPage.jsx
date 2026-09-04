import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import {
  getHousesOfUser,
  getEventsFromHouse,
  getEventCategoriesFromHouse,
  getHouseMembers,
  createEvent
} from '../api/api';
import Modal from '../components/Modal';

const MONTH_NAMES = [
  'Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran',
  'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık'
];

const WEEKDAY_NAMES = ['Pzt', 'Sal', 'Çar', 'Per', 'Cum', 'Cmt', 'Paz'];

export default function CalendarPage() {
  const { currentUser } = useUser();
  const navigate = useNavigate();

  const [currentDate, setCurrentDate] = useState(() => new Date());
  const [selectedDate, setSelectedDate] = useState(() => new Date());
  const [selectedHouseId, setSelectedHouseId] = useState('all');
  const [viewMode, setViewMode] = useState('month'); // 'month' | 'agenda'

  const [houses, setHouses] = useState([]);
  const [events, setEvents] = useState([]);
  const [categories, setCategories] = useState({});
  const [members, setMembers] = useState({});
  const [houseCategories, setHouseCategories] = useState({}); // houseId -> [categories]
  const [houseMembers, setHouseMembers] = useState({});       // houseId -> [members]

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedEventModal, setSelectedEventModal] = useState(null);

  // Yeni Etkinlik Oluşturma Modalı State'leri
  const [createModal, setCreateModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const emptyForm = {
    houseId: '',
    title: '',
    description: '',
    startedDate: '',
    endDate: '',
    isAllDay: false,
    location: '',
    eventCategoryId: '',
    assignedUserId: ''
  };
  const [form, setForm] = useState(emptyForm);

  const toIso = (s) => s ? s + ':00' : null;

  // Verileri yükle
  const loadCalendarData = async () => {
    if (!currentUser?.id) return;
    try {
      setLoading(true);
      setError('');

      const housesRes = await getHousesOfUser(currentUser.id);
      const userHouses = housesRes.data || [];
      setHouses(userHouses);

      if (userHouses.length === 0) {
        setEvents([]);
        return;
      }

      // Her ev için paralel veri çekimi (etkinlikler, kategoriler, üyeler)
      const eventPromises = userHouses.map(async (h) => {
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

      const catPromises = userHouses.map(async (h) => {
        try {
          const res = await getEventCategoriesFromHouse(h.id);
          return { houseId: h.id, categories: res.data || [] };
        } catch {
          return { houseId: h.id, categories: [] };
        }
      });

      const memberPromises = userHouses.map(async (h) => {
        try {
          const res = await getHouseMembers(h.id);
          return { houseId: h.id, members: res.data || [] };
        } catch {
          return { houseId: h.id, members: [] };
        }
      });

      const [allEventsNested, allCatsResults, allMembersResults] = await Promise.all([
        Promise.all(eventPromises),
        Promise.all(catPromises),
        Promise.all(memberPromises),
      ]);

      const flatEvents = allEventsNested.flat();
      setEvents(flatEvents);

      // Kategori haritaları
      const catMap = {};
      const hCatMap = {};
      allCatsResults.forEach(({ houseId, categories: cList }) => {
        hCatMap[houseId] = cList;
        cList.forEach(c => {
          if (c?.id) catMap[c.id] = c;
        });
      });
      setCategories(catMap);
      setHouseCategories(hCatMap);

      // Üye haritaları
      const memMap = {};
      const hMemMap = {};
      allMembersResults.forEach(({ houseId, members: mList }) => {
        hMemMap[houseId] = mList;
        mList.forEach(m => {
          if (m?.id) memMap[m.id] = m.name;
        });
      });
      setMembers(memMap);
      setHouseMembers(hMemMap);

    } catch {
      setError('Takvim verileri yüklenirken bir sorun oluştu.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCalendarData();
  }, [currentUser]);

  // Yeni Etkinlik Modalını Aç
  const openCreateModal = (targetDate = null) => {
    const d = targetDate || selectedDate || new Date();
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const datePrefix = `${yyyy}-${mm}-${dd}`;

    const defaultHouseId = (selectedHouseId !== 'all' && selectedHouseId)
      ? String(selectedHouseId)
      : (houses.length > 0 ? String(houses[0].id) : '');

    setForm({
      ...emptyForm,
      houseId: defaultHouseId,
      startedDate: `${datePrefix}T10:00`,
      endDate: `${datePrefix}T11:00`,
      assignedUserId: currentUser?.id ? String(currentUser.id) : ''
    });
    setCreateModal(true);
  };

  // Yeni Etkinlik Kaydet
  const handleCreateEvent = async () => {
    if (!form.title.trim()) {
      alert('Lütfen etkinlik başlığını girin.');
      return;
    }
    if (!form.houseId) {
      alert('Lütfen etkinliğin ait olduğu evi seçin.');
      return;
    }

    try {
      setSaving(true);
      await createEvent({
        houseId: Number(form.houseId),
        assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : currentUser.id,
        eventCategoryId: form.eventCategoryId ? Number(form.eventCategoryId) : null,
        title: form.title.trim(),
        description: form.description,
        startedDate: toIso(form.startedDate),
        endDate: toIso(form.endDate),
        isAllDay: form.isAllDay,
        location: form.location
      });
      setCreateModal(false);
      setForm(emptyForm);
      await loadCalendarData();
    } catch (err) {
      alert(err.response?.data?.message || 'Etkinlik oluşturulurken bir hata oluştu.');
    } finally {
      setSaving(false);
    }
  };

  // Ev filtresine göre etkinlikleri filtrele
  const filteredEvents = useMemo(() => {
    if (selectedHouseId === 'all') return events;
    return events.filter(e => String(e.houseId) === String(selectedHouseId));
  }, [events, selectedHouseId]);

  // Ay takvimi günleri
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const calendarDays = useMemo(() => {
    const firstDayIndex = (new Date(year, month, 1).getDay() + 6) % 7; // Pazartesi 0
    const daysInCurrentMonth = new Date(year, month + 1, 0).getDate();
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    const days = [];

    // Önceki aydan taşan günler
    for (let i = firstDayIndex - 1; i >= 0; i--) {
      const d = daysInPrevMonth - i;
      const dateObj = new Date(year, month - 1, d);
      days.push({
        date: dateObj,
        dayNumber: d,
        isCurrentMonth: false,
        isPrevMonth: true,
      });
    }

    // Bu ayın günleri
    for (let d = 1; d <= daysInCurrentMonth; d++) {
      const dateObj = new Date(year, month, d);
      days.push({
        date: dateObj,
        dayNumber: d,
        isCurrentMonth: true,
      });
    }

    // Sonraki aydan tamamlayıcı günler (42 veya 35 hücre)
    const totalCells = days.length > 35 ? 42 : 35;
    const remaining = totalCells - days.length;
    for (let d = 1; d <= remaining; d++) {
      const dateObj = new Date(year, month + 1, d);
      days.push({
        date: dateObj,
        dayNumber: d,
        isCurrentMonth: false,
        isNextMonth: true,
      });
    }

    return days;
  }, [year, month]);

  // Yardımcı fonksiyonlar
  const isSameDay = (d1, d2) => {
    if (!d1 || !d2) return false;
    return (
      d1.getFullYear() === d2.getFullYear() &&
      d1.getMonth() === d2.getMonth() &&
      d1.getDate() === d2.getDate()
    );
  };

  const getEventsForDay = (dateObj) => {
    const y = dateObj.getFullYear();
    const m = String(dateObj.getMonth() + 1).padStart(2, '0');
    const d = String(dateObj.getDate()).padStart(2, '0');
    const dateStr = `${y}-${m}-${d}`;

    return filteredEvents.filter(ev => {
      if (!ev.startedDate) return false;
      const evDate = ev.startedDate.slice(0, 10);
      return evDate === dateStr;
    });
  };

  const nextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1));
  };

  const prevMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1));
  };

  const goToToday = () => {
    const now = new Date();
    setCurrentDate(now);
    setSelectedDate(now);
  };

  const formatEventTime = (ev) => {
    if (ev.isAllDay) return 'Tüm Gün';
    if (!ev.startedDate) return '';
    try {
      const d = new Date(ev.startedDate);
      return d.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' });
    } catch {
      return '';
    }
  };

  const selectedDayEvents = useMemo(() => {
    if (!selectedDate) return [];
    return getEventsForDay(selectedDate);
  }, [selectedDate, filteredEvents]);

  // Liste (Ajanda) Görünümü için Mevcut Ayın Etkinlikleri
  const currentMonthEvents = useMemo(() => {
    return filteredEvents
      .filter(ev => {
        if (!ev.startedDate) return false;
        const d = new Date(ev.startedDate);
        return d.getFullYear() === year && d.getMonth() === month;
      })
      .sort((a, b) => new Date(a.startedDate) - new Date(b.startedDate));
  }, [filteredEvents, year, month]);

  // Formdaki seçili evin kategorileri ve üyeleri
  const currentFormCategories = useMemo(() => {
    if (!form.houseId) return [];
    return houseCategories[form.houseId] || [];
  }, [form.houseId, houseCategories]);

  const currentFormMembers = useMemo(() => {
    if (!form.houseId) return [];
    return houseMembers[form.houseId] || [];
  }, [form.houseId, houseMembers]);

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      {/* Üst Başlık ve Filtreler */}
      <div className="card" style={{ padding: '24px 28px' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 16
        }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <span style={{ fontSize: '2rem' }}>📅</span>
              <div>
                <h1 style={{ fontSize: '1.6rem', fontWeight: 800, margin: 0, letterSpacing: '-0.02em' }}>
                  Etkinlik Takvimi
                </h1>
                <p style={{ margin: '4px 0 0', fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                  Evlerinizdeki tüm planları ve özel günleri takvim üzerinden organize edin.
                </p>
              </div>
            </div>
          </div>

          {/* Filtre ve Buton Kontrolleri */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
            {/* Ev Filtresi */}
            <div style={{ minWidth: 180 }}>
              <select
                className="form-select"
                value={selectedHouseId}
                onChange={(e) => setSelectedHouseId(e.target.value)}
                style={{ width: '100%', padding: '8px 32px 8px 12px', fontSize: '0.85rem' }}
              >
                <option value="all">🏠 Tüm Evler ({events.length} Etkinlik)</option>
                {houses.map(h => (
                  <option key={h.id} value={h.id}>
                    {h.title}
                  </option>
                ))}
              </select>
            </div>

            {/* Görünüm Butonları */}
            <div className="tabs" style={{ marginBottom: 0 }}>
              <button
                className={`tab ${viewMode === 'month' ? 'active' : ''}`}
                onClick={() => setViewMode('month')}
                style={{ padding: '8px 14px', fontSize: '0.82rem' }}
              >
                🗓️ Ay Görünümü
              </button>
              <button
                className={`tab ${viewMode === 'agenda' ? 'active' : ''}`}
                onClick={() => setViewMode('agenda')}
                style={{ padding: '8px 14px', fontSize: '0.82rem' }}
              >
                📋 Liste Görünümü
              </button>
            </div>

            {/* ＋ Yeni Etkinlik Butonu */}
            <button
              className="btn btn-primary"
              onClick={() => openCreateModal()}
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: 6,
                padding: '9px 18px',
                fontSize: '0.86rem',
                fontWeight: 600,
                boxShadow: '0 4px 14px rgba(124, 58, 237, 0.35)'
              }}
            >
              ＋ Yeni Etkinlik
            </button>
          </div>
        </div>

        {error && (
          <div className="alert alert-error" style={{ marginTop: 16 }} onClick={() => setError('')}>
            ⚠️ {error}
          </div>
        )}
      </div>

      {/* Ay / Yıl Gezinme Barı */}
      <div className="card" style={{ padding: '16px 24px' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 12
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <button
              className="btn btn-secondary btn-sm"
              onClick={prevMonth}
              title="Önceki Ay"
              style={{ padding: '6px 14px' }}
            >
              ◀
            </button>
            <h2 style={{ fontSize: '1.35rem', fontWeight: 700, margin: 0, minWidth: 180, textAlign: 'center' }}>
              {MONTH_NAMES[month]} {year}
            </h2>
            <button
              className="btn btn-secondary btn-sm"
              onClick={nextMonth}
              title="Sonraki Ay"
              style={{ padding: '6px 14px' }}
            >
              ▶
            </button>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <button
              className="btn btn-secondary btn-sm"
              onClick={goToToday}
              style={{ fontWeight: 600 }}
            >
              📍 Bugün
            </button>
            <span className="badge badge-member" style={{ fontSize: '0.78rem' }}>
              {currentMonthEvents.length} Etkinlik Bu Ay
            </span>
          </div>
        </div>
      </div>

      {/* Görünüm 1: AY TAKVİMİ (GRID) */}
      {viewMode === 'month' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 1fr) 340px', gap: 24, alignItems: 'start' }}>
          {/* Takvim Grid Kartı */}
          <div className="card" style={{ padding: 18, overflowX: 'auto' }}>
            {/* Hafta Günleri Başlığı */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, minmax(100px, 1fr))',
              gap: 8,
              marginBottom: 10,
              textAlign: 'center'
            }}>
              {WEEKDAY_NAMES.map((name, idx) => (
                <div
                  key={name}
                  style={{
                    padding: '8px 0',
                    fontWeight: 700,
                    fontSize: '0.8rem',
                    color: idx >= 5 ? 'var(--accent-1)' : 'var(--text-secondary)',
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em'
                  }}
                >
                  {name}
                </div>
              ))}
            </div>

            {/* Gün Hücreleri */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, minmax(100px, 1fr))',
              gap: 8,
            }}>
              {calendarDays.map((cell, idx) => {
                const dayEvents = getEventsForDay(cell.date);
                const isToday = isSameDay(cell.date, new Date());
                const isSelected = isSameDay(cell.date, selectedDate);

                return (
                  <div
                    key={idx}
                    onClick={() => setSelectedDate(cell.date)}
                    style={{
                      minHeight: 105,
                      padding: 8,
                      borderRadius: 10,
                      background: isSelected
                        ? 'rgba(124, 58, 237, 0.12)'
                        : cell.isCurrentMonth
                        ? 'rgba(255, 255, 255, 0.02)'
                        : 'rgba(255, 255, 255, 0.005)',
                      border: isSelected
                        ? '2px solid var(--accent-1)'
                        : isToday
                        ? '1px solid rgba(59, 130, 246, 0.5)'
                        : '1px solid var(--border)',
                      opacity: cell.isCurrentMonth ? 1 : 0.38,
                      cursor: 'pointer',
                      transition: 'all 0.15s ease',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 4,
                      position: 'relative'
                    }}
                    onMouseEnter={(e) => {
                      if (!isSelected) e.currentTarget.style.borderColor = 'var(--border-hover)';
                    }}
                    onMouseLeave={(e) => {
                      if (!isSelected) {
                        e.currentTarget.style.borderColor = isToday ? 'rgba(59, 130, 246, 0.5)' : 'var(--border)';
                      }
                    }}
                  >
                    {/* Gün Numarası & Bugün Rozeti */}
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      fontSize: '0.8rem',
                      fontWeight: isToday || isSelected ? 700 : 500,
                      color: isToday ? '#60a5fa' : isSelected ? 'var(--accent-1)' : 'var(--text-primary)'
                    }}>
                      <span>{cell.dayNumber}</span>
                      {isToday && (
                        <span style={{
                          fontSize: '0.62rem',
                          background: 'rgba(59, 130, 246, 0.2)',
                          color: '#60a5fa',
                          padding: '1px 5px',
                          borderRadius: 4,
                          fontWeight: 600
                        }}>
                          Bugün
                        </span>
                      )}
                    </div>

                    {/* Günün Etkinlik Çipleri */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 3, marginTop: 2, overflow: 'hidden' }}>
                      {dayEvents.slice(0, 2).map((ev) => {
                        const cat = categories[ev.eventCategoryId];
                        const catColor = cat?.colorCode || 'var(--accent-1)';
                        return (
                          <div
                            key={ev.id}
                            onClick={(e) => {
                              e.stopPropagation();
                              setSelectedEventModal(ev);
                            }}
                            title={`${ev.title} (${ev.houseTitle})`}
                            style={{
                              padding: '2px 6px',
                              borderRadius: 4,
                              background: 'rgba(255, 255, 255, 0.06)',
                              borderLeft: `3px solid ${catColor}`,
                              fontSize: '0.7rem',
                              color: 'var(--text-primary)',
                              whiteSpace: 'nowrap',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              display: 'flex',
                              alignItems: 'center',
                              gap: 4
                            }}
                          >
                            <span style={{ fontWeight: 600 }}>{ev.title}</span>
                          </div>
                        );
                      })}

                      {dayEvents.length > 2 && (
                        <div style={{
                          fontSize: '0.66rem',
                          color: 'var(--accent-1)',
                          fontWeight: 600,
                          padding: '1px 4px'
                        }}>
                          +{dayEvents.length - 2} daha...
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Sağ Panel: Seçilen Günün Etkinlikleri */}
          <div className="card" style={{ padding: 20 }}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              marginBottom: 16,
              paddingBottom: 12,
              borderBottom: '1px solid var(--border)'
            }}>
              <div>
                <h3 style={{ fontSize: '1rem', fontWeight: 700, margin: 0 }}>
                  {selectedDate ? selectedDate.toLocaleDateString('tr-TR', { day: 'numeric', month: 'long', weekday: 'long' }) : 'Seçilen Gün'}
                </h3>
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  {selectedDayEvents.length} Etkinlik
                </span>
              </div>
              <button
                className="btn btn-primary btn-sm"
                onClick={() => openCreateModal(selectedDate)}
                style={{ fontSize: '0.76rem', padding: '4px 10px' }}
                title="Bu güne etkinlik ekle"
              >
                ＋ Ekle
              </button>
            </div>

            {selectedDayEvents.length === 0 ? (
              <div className="empty-state" style={{ padding: '36px 12px' }}>
                <div className="emoji" style={{ fontSize: '2rem' }}>☀️</div>
                <h4 style={{ margin: '8px 0 4px', fontSize: '0.95rem' }}>Planlanan Etkinlik Yok</h4>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: 12 }}>
                  Bu gün için herhangi bir ev etkinliği kaydedilmemiş.
                </p>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => openCreateModal(selectedDate)}
                >
                  ＋ Bu Güne Etkinlik Ekle
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {selectedDayEvents.map(ev => {
                  const cat = categories[ev.eventCategoryId];
                  const catColor = cat?.colorCode || 'var(--accent-1)';
                  const memberName = members[ev.assignedUserId] || '—';

                  return (
                    <div
                      key={ev.id}
                      style={{
                        padding: 14,
                        borderRadius: 10,
                        background: 'rgba(255, 255, 255, 0.03)',
                        border: '1px solid var(--border)',
                        borderLeft: `4px solid ${catColor}`,
                        display: 'flex',
                        flexDirection: 'column',
                        gap: 8
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 6 }}>
                        <strong style={{ fontSize: '0.92rem', color: 'var(--text-primary)' }}>
                          {ev.title}
                        </strong>
                        <span className="badge" style={{ fontSize: '0.68rem', background: 'rgba(124, 58, 237, 0.12)', color: 'var(--accent-1)' }}>
                          🏠 {ev.houseTitle}
                        </span>
                      </div>

                      {ev.description && (
                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', margin: 0 }}>
                          {ev.description}
                        </p>
                      )}

                      <div style={{ display: 'flex', flexDirection: 'column', gap: 4, fontSize: '0.76rem', color: 'var(--text-muted)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                          <span>🕒</span>
                          <span>{formatEventTime(ev)}</span>
                          {cat && (
                            <>
                              <span>•</span>
                              <span style={{ color: catColor }}>🏷️ {cat.title}</span>
                            </>
                          )}
                        </div>
                        {ev.location && (
                          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                            <span>📍</span>
                            <span>{ev.location}</span>
                          </div>
                        )}
                        {ev.assignedUserId && (
                          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                            <span>👤</span>
                            <span>{memberName}</span>
                          </div>
                        )}
                      </div>

                      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 4 }}>
                        <button
                          className="btn btn-secondary btn-sm"
                          style={{ fontSize: '0.75rem', padding: '4px 10px' }}
                          onClick={() => navigate(`/houses/${ev.houseId}?tab=events&eventId=${ev.id}`)}
                        >
                          🔍 Ev Sayfasında Gör ➔
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Görünüm 2: LİSTE (AJANDA) GÖRÜNÜMÜ */}
      {viewMode === 'agenda' && (
        <div className="card" style={{ padding: 24 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
            <h3 style={{ fontSize: '1.1rem', margin: 0 }}>
              📋 {MONTH_NAMES[month]} {year} Etkinlik Listesi
            </h3>
            <span className="badge badge-member">{currentMonthEvents.length} Etkinlik</span>
          </div>

          {currentMonthEvents.length === 0 ? (
            <div className="empty-state" style={{ padding: '48px 16px' }}>
              <div className="emoji" style={{ fontSize: '2.5rem' }}>🗓️</div>
              <h3>Bu Ay Etkinlik Bulunmuyor</h3>
              <p>Seçilen filtrede bu ay için planlanmış herhangi bir etkinlik bulunmamaktadır.</p>
              <button
                className="btn btn-primary btn-sm"
                onClick={() => openCreateModal()}
                style={{ marginTop: 12 }}
              >
                ＋ İlk Etkinliği Ekle
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              {currentMonthEvents.map(ev => {
                const cat = categories[ev.eventCategoryId];
                const catColor = cat?.colorCode || 'var(--accent-1)';
                const evDate = ev.startedDate ? new Date(ev.startedDate) : null;
                const memberName = members[ev.assignedUserId] || '—';

                return (
                  <div
                    key={ev.id}
                    className="card"
                    style={{
                      padding: 16,
                      borderLeft: `4px solid ${catColor}`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      gap: 16,
                      flexWrap: 'wrap'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 16, minWidth: 260, flex: 1 }}>
                      {/* Tarih Rozeti */}
                      <div style={{
                        minWidth: 62,
                        padding: '8px 10px',
                        borderRadius: 8,
                        background: 'rgba(124, 58, 237, 0.12)',
                        border: '1px solid rgba(124, 58, 237, 0.25)',
                        textAlign: 'center'
                      }}>
                        <div style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--accent-1)', lineHeight: 1 }}>
                          {evDate ? evDate.getDate() : '—'}
                        </div>
                        <div style={{ fontSize: '0.68rem', color: 'var(--text-secondary)', marginTop: 2, textTransform: 'uppercase' }}>
                          {evDate ? evDate.toLocaleDateString('tr-TR', { month: 'short' }) : ''}
                        </div>
                      </div>

                      {/* Etkinlik Detayları */}
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                          <strong style={{ fontSize: '1rem', color: 'var(--text-primary)' }}>
                            {ev.title}
                          </strong>
                          <span className="badge" style={{ background: 'rgba(124, 58, 237, 0.12)', color: 'var(--accent-1)', fontSize: '0.72rem' }}>
                            🏠 {ev.houseTitle}
                          </span>
                          {ev.isAllDay && (
                            <span className="badge badge-member" style={{ fontSize: '0.68rem' }}>Tüm Gün</span>
                          )}
                        </div>

                        {ev.description && (
                          <p style={{ fontSize: '0.84rem', color: 'var(--text-secondary)', margin: '0 0 6px' }}>
                            {ev.description}
                          </p>
                        )}

                        <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: '0.78rem', color: 'var(--text-muted)', flexWrap: 'wrap' }}>
                          <span>🕒 {evDate ? evDate.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' }) : '—'}</span>
                          {ev.location && <span>📍 {ev.location}</span>}
                          {cat && <span style={{ color: catColor }}>🏷️ {cat.title}</span>}
                          {ev.assignedUserId && <span>👤 {memberName}</span>}
                        </div>
                      </div>
                    </div>

                    <div>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => navigate(`/houses/${ev.houseId}?tab=events&eventId=${ev.id}`)}
                      >
                        🔍 Detaya Git
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* ＋ Yeni Etkinlik Oluşturma Modalı */}
      {createModal && (
        <Modal
          title="📅 Yeni Etkinlik Oluştur"
          onClose={() => setCreateModal(false)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>
                İptal
              </button>
              <button className="btn btn-primary" onClick={handleCreateEvent} disabled={saving}>
                {saving ? 'Kaydediliyor...' : 'Oluştur'}
              </button>
            </>
          }
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {/* Ev Seçimi */}
            <div className="form-group">
              <label className="form-label">Ev *</label>
              <select
                className="form-select"
                value={form.houseId}
                onChange={(e) => {
                  const hId = e.target.value;
                  setForm(f => ({
                    ...f,
                    houseId: hId,
                    eventCategoryId: '',
                    assignedUserId: currentUser?.id ? String(currentUser.id) : ''
                  }));
                }}
              >
                <option value="">Ev seçiniz</option>
                {houses.map(h => (
                  <option key={h.id} value={h.id}>
                    {h.title}
                  </option>
                ))}
              </select>
            </div>

            {/* Başlık */}
            <div className="form-group">
              <label className="form-label">Etkinlik Başlığı *</label>
              <input
                className="form-input"
                placeholder="Örn: Ev Temizliği, Akşam Yemeği"
                value={form.title}
                onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
                autoFocus
              />
            </div>

            {/* Açıklama */}
            <div className="form-group">
              <label className="form-label">Açıklama</label>
              <textarea
                className="form-textarea"
                rows={2}
                placeholder="Etkinlik hakkında notlar..."
                value={form.description}
                onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
              />
            </div>

            {/* Başlangıç ve Bitiş */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div className="form-group">
                <label className="form-label">Başlangıç</label>
                <input
                  className="form-input"
                  type="datetime-local"
                  value={form.startedDate}
                  onChange={e => setForm(f => ({ ...f, startedDate: e.target.value }))}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Bitiş</label>
                <input
                  className="form-input"
                  type="datetime-local"
                  value={form.endDate}
                  onChange={e => setForm(f => ({ ...f, endDate: e.target.value }))}
                />
              </div>
            </div>

            {/* Konum */}
            <div className="form-group">
              <label className="form-label">Konum</label>
              <input
                className="form-input"
                placeholder="Yer / Adres"
                value={form.location}
                onChange={e => setForm(f => ({ ...f, location: e.target.value }))}
              />
            </div>

            {/* Kategori ve Atanan Kişi */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div className="form-group">
                <label className="form-label">Kategori</label>
                <select
                  className="form-select"
                  value={form.eventCategoryId}
                  onChange={e => setForm(f => ({ ...f, eventCategoryId: e.target.value }))}
                >
                  <option value="">Kategori seçiniz</option>
                  {currentFormCategories.map(c => (
                    <option key={c.id} value={c.id}>
                      {c.title}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Sorumlu Üye</label>
                <select
                  className="form-select"
                  value={form.assignedUserId}
                  onChange={e => setForm(f => ({ ...f, assignedUserId: e.target.value }))}
                >
                  <option value="">Üye seçiniz</option>
                  {currentFormMembers.map(m => (
                    <option key={m.id} value={m.id}>
                      {m.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Tüm Gün */}
            <div className="form-group">
              <label style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={form.isAllDay}
                  onChange={e => setForm(f => ({ ...f, isAllDay: e.target.checked }))}
                />
                <span className="form-label" style={{ margin: 0 }}>Tüm Gün</span>
              </label>
            </div>
          </div>
        </Modal>
      )}

      {/* Hızlı İnceleme Modalı (Ay Takviminde Etkinliğe Tıklandığında) */}
      {selectedEventModal && (
        <Modal
          title={`📅 ${selectedEventModal.title}`}
          onClose={() => setSelectedEventModal(null)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setSelectedEventModal(null)}>
                Kapat
              </button>
              <button
                className="btn btn-primary"
                onClick={() => {
                  const ev = selectedEventModal;
                  setSelectedEventModal(null);
                  navigate(`/houses/${ev.houseId}?tab=events&eventId=${ev.id}`);
                }}
              >
                🔍 Ev Sayfasında Göster
              </button>
            </>
          }
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span className="badge" style={{ background: 'rgba(124, 58, 237, 0.12)', color: 'var(--accent-1)' }}>
                🏠 {selectedEventModal.houseTitle}
              </span>
              {selectedEventModal.isAllDay && (
                <span className="badge badge-member">Tüm Gün</span>
              )}
              {categories[selectedEventModal.eventCategoryId] && (
                <span
                  className="badge"
                  style={{
                    border: `1px solid ${categories[selectedEventModal.eventCategoryId].colorCode}`,
                    color: categories[selectedEventModal.eventCategoryId].colorCode
                  }}
                >
                  🏷️ {categories[selectedEventModal.eventCategoryId].title}
                </span>
              )}
            </div>

            {selectedEventModal.description && (
              <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', margin: 0 }}>
                {selectedEventModal.description}
              </p>
            )}

            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: 8,
              padding: 12,
              borderRadius: 8,
              background: 'rgba(255, 255, 255, 0.03)',
              border: '1px solid var(--border)',
              fontSize: '0.84rem'
            }}>
              <div>
                <strong style={{ color: 'var(--text-secondary)' }}>Başlangıç: </strong>
                <span>{selectedEventModal.startedDate ? new Date(selectedEventModal.startedDate).toLocaleString('tr-TR') : '—'}</span>
              </div>
              {selectedEventModal.endDate && (
                <div>
                  <strong style={{ color: 'var(--text-secondary)' }}>Bitiş: </strong>
                  <span>{new Date(selectedEventModal.endDate).toLocaleString('tr-TR')}</span>
                </div>
              )}
              {selectedEventModal.location && (
                <div>
                  <strong style={{ color: 'var(--text-secondary)' }}>Konum: </strong>
                  <span>📍 {selectedEventModal.location}</span>
                </div>
              )}
              {selectedEventModal.assignedUserId && (
                <div>
                  <strong style={{ color: 'var(--text-secondary)' }}>Sorumlu Üye: </strong>
                  <span>👤 {members[selectedEventModal.assignedUserId] || '—'}</span>
                </div>
              )}
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
