import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://api.nestify.hamdiyecicek.tech/api/v1';

// Aktif kullanıcı ID'si ve JWT token yönetimi
let actingUserId = null;
let authToken = null;

export const setActingUserId = (id) => { actingUserId = id; };
export const getActingUserId = () => actingUserId;

export const setAuthToken = (token) => { authToken = token; };
export const getAuthToken = () => authToken;

const api = axios.create({ baseURL: BASE_URL });

api.interceptors.request.use((config) => {
  // Token'ı önce değişkenden, yoksa localStorage'dan al
  let token = authToken;
  if (!token) {
    try {
      const stored = localStorage.getItem('nestify_user');
      if (stored) {
        const user = JSON.parse(stored);
        token = user?.token || user?.accessToken || null;
      }
    } catch {
      token = null;
    }
  }

  // 1. JWT Bearer token başlığını ekle (Backend Spring Security için zorunlu)
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.warn('Oturum süresi dolmuş veya yetkisiz istek.');
    }
    return Promise.reject(error);
  }
);

// ─── Auth ────────────────────────────────────────────────────
export const loginUser = (data) => api.post('/auth/login', data);

// ─── Users ──────────────────────────────────────────────────
export const getUsers = (page = 0, size = 10, sortBy = 'id', sortDirection = 'asc') =>
  api.get(`/users?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`);

export const getUserById   = (id)   => api.get(`/users/${id}`);
export const getUserHouses = (id)   => api.get(`/users/${id}/houses`);
export const createUser    = (data) => api.post('/users', data);
export const updateUser    = (id, data) => api.put(`/users/${id}`, data);
export const deleteUser    = (id)   => api.delete(`/users/${id}`);

// ─── Houses ─────────────────────────────────────────────────
export const getHouses       = ()         => api.get('/houses');
export const getHousesOfUser = (userId)   => api.get(`/users/${userId}/houses`);
export const getHouseById    = (id)       => api.get(`/houses/${id}`);
export const createHouse     = (data)     => api.post('/houses', data);
export const updateHouse     = (id, data) => api.put(`/houses/${id}`, data);
export const deleteHouse     = (id)       => api.delete(`/houses/${id}`);
export const joinHouseByInviteCode = (inviteCode) =>
  api.post('/houses/0/members/join', { inviteCode });

// ─── House Members ───────────────────────────────────────────
export const getHouseMembers   = (houseId)       => api.get(`/houses/${houseId}/members`);
export const addMemberToHouse  = (houseId, data) => api.post(`/houses/${houseId}/members`, data);
export const removeMemberFromHouse = (houseId, dataOrUserId) => {
  const userId = typeof dataOrUserId === 'object' ? dataOrUserId.userId : dataOrUserId;
  return api.delete(`/houses/${houseId}/members/${userId}`);
};
export const changeMemberRole  = (houseId, userId, data) =>
  api.patch(`/houses/${houseId}/members/${userId}/role`, data);

// ─── Events ─────────────────────────────────────────────────
export const getEventsFromHouse = (houseId)       => api.get(`/houses/${houseId}/events`);
export const createEvent        = (data)          => api.post(`/houses/${data.houseId}/events`, data);
export const updateEvent        = (eventId, data) => api.put(`/houses/${data.houseId}/events/${eventId}`, data);
export const deleteEvent        = (data)          => api.delete(`/houses/${data.houseId}/events/${data.eventId}`);

// ─── Event Categories ────────────────────────────────────────
export const getEventCategoriesFromHouse = (houseId) => api.get(`/houses/${houseId}/event-categories`);
export const createEventCategory         = (data)    => api.post(`/houses/${data.houseId}/event-categories`, data);
export const updateEventCategory         = (data)    => api.put(`/houses/${data.houseId}/event-categories/${data.eventCategoryId || data.id}`, data);
export const deleteEventCategory         = (data)    => api.delete(`/houses/${data.houseId}/event-categories/${data.categoryId || data.id}`);

// ─── House Needs ─────────────────────────────────────────────
export const getHouseNeedsFromHouse = (houseId) => api.get(`/houses/${houseId}/house-needs`);
export const createHouseNeed        = (data)    => api.post(`/houses/${data.houseId}/house-needs`, data);
export const updateHouseNeed        = (data)    => api.put(`/houses/${data.houseId}/house-needs/${data.id || data.houseNeedId}`, data);
export const deleteHouseNeed        = (data)    => api.delete(`/houses/${data.houseId}/house-needs/${data.houseNeedId || data.id}`);

// ─── Transactions ────────────────────────────────────────────
export const getTransactionsFromHouse = (houseId) => api.get(`/houses/${houseId}/transactions`);
export const createTransaction        = (houseId, data) => api.post(`/houses/${houseId}/transactions`, data);
export const updateTransaction        = (houseId, transactionId, data) => api.put(`/houses/${houseId}/transactions/${transactionId}`, data);
export const deleteTransaction        = (houseId, transactionId) => api.delete(`/houses/${houseId}/transactions/${transactionId}`);
