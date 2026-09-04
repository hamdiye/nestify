import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

// Aktif kullanıcı ID'si header'a eklenir
let actingUserId = null;

export const setActingUserId = (id) => { actingUserId = id; };
export const getActingUserId = () => actingUserId;

const api = axios.create({ baseURL: BASE_URL });

api.interceptors.request.use((config) => {
  if (actingUserId) config.headers['X-Acting-User-Id'] = actingUserId;
  return config;
});

// ─── Auth ────────────────────────────────────────────────────
export const loginUser = (data) => api.post('/auth/login', data);

// ─── Users ──────────────────────────────────────────────────
export const getUsers = (page = 0, size = 10, sortBy = 'id', sortDirection = 'asc') =>
  api.get(`/users?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`);

export const getUserById   = (id)   => api.get(`/users/${id}`);
export const getUserHouses = (id)   => api.get(`/users/${id}/houses`);
export const createUser    = (data) => api.post('/users/add', data);
export const updateUser    = (id, data) => api.put(`/users/update/${id}`, data);
export const deleteUser    = (id)   => api.delete(`/users/delete/${id}`);

// ─── Houses ─────────────────────────────────────────────────
export const getHouses       = ()         => api.get('/houses');
export const getHousesOfUser = (userId)   => api.get(`/users/${userId}/houses`);
export const getHouseById = (id)     => api.get(`/houses/${id}`);
export const createHouse  = (data)   => api.post('/houses/add', data);
export const updateHouse  = (id, data) => api.put(`/houses/update/${id}`, data);
export const deleteHouse  = (id)     => api.delete(`/houses/delete/${id}`);

// ─── House Members ───────────────────────────────────────────
export const getHouseMembers   = (houseId)       => api.get(`/houses/${houseId}/members`);
export const addMemberToHouse  = (houseId, data) => api.post(`/houses/${houseId}/members/addMember`, data);
export const removeMemberFromHouse = (houseId, data) => api.post(`/houses/${houseId}/members/removeMember`, data);
export const changeMemberRole  = (houseId, userId, data) =>
  api.post(`/houses/${houseId}/members/${userId}/changeRole`, data);

// ─── Events ─────────────────────────────────────────────────
export const getEventsFromHouse = (houseId)       => api.get(`/houses/${houseId}/events`);
export const createEvent        = (data)           => api.post('/events/add', data);
export const updateEvent        = (eventId, data)  => api.post(`/events/update/${eventId}`, data);
export const deleteEvent        = (data)           => api.delete('/events/delete', { data });

// ─── Event Categories ────────────────────────────────────────
export const getEventCategoriesFromHouse = (houseId) => api.get(`/houses/${houseId}/event-categories`);
export const createEventCategory         = (data)    => api.post('/event-categories/add', data);
export const updateEventCategory         = (data)    => api.post('/event-categories/update', data);
export const deleteEventCategory         = (data)    => api.delete('/event-categories/delete', { data });

// ─── House Needs ─────────────────────────────────────────────
export const getHouseNeedsFromHouse = (houseId) => api.get(`/houses/${houseId}/house-needs`);
export const createHouseNeed        = (data)    => api.post('/house-needs/add', data);
export const updateHouseNeed        = (data)    => api.put('/house-needs/update', data);
export const deleteHouseNeed        = (data)    => api.delete('/house-needs/delete', { data });
