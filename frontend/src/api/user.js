import api from './axios';

export const login = (data) => {
  return api.post('/users/login', data);
};

export const signup = (data) => {
  return api.post('/users/signup', data);
};

export const getMyInfo = (id) => {
  if (!id || id === 'undefined') {
    console.error('getMyInfo 요청 실패: ID가 없습니다.');
    return Promise.reject('ID 없음');
  }
  return api.get(`/users/${id}`);
};

export const updateUser = (id, data) => {
  return api.put(`/users/${id}`, data);
};

export const deleteUser = (id) => {
  return api.delete(`/users/${id}`);
};
