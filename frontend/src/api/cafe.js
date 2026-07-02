import api from './axios';

export const getCafes = (params) => {
  return api.get('/cafes', { params });
};

export const createCafe = (data) => {
  return api.post('/cafes', data);
};

export const getCafe = (id) => {
  return api.get(`/cafes/${id}`);
};

export const updateCafe = (id, data) => {
  return api.put(`/cafes/${id}`, data);
};

export const deleteCafe = (id) => {
  return api.delete(`/cafes/${id}`);
};

export const checkCafeExists = (kakaoId) => {
  // kakaoId 체크는 별도 도메인일 수 있으나 일단 공통 api 사용
  return api.get(`/cafes/check/${kakaoId}`);
};

export const submitCongestionVote = (cafeId, level) => {
  // 이미 세팅된 api 인스턴스를 사용한다고 가정
  return api.post(`/cafes/${cafeId}/congestion-votes`, {
    congestionLevel: level,
  });
};
