import api from './axios';

// 1. 전체 조회
export const getCommunityList = () => {
  return api.get('/community');
};

// 2. 작성
export const createCommunity = (formData) => {
  // 🎯 주소창에서도 ?userId=... 부분을 완전히 지워줍니다!
  return api.post(`/community`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// 3. 단일 조회
export const getCommunity = (communityId) => {
  return api.get(`/community/${communityId}`);
};

// 4. 업데이트 (수정)
export const updateCommunity = (postId, formData) => {
  // 🎯 postId를 파라미터로 받고, URL 경로에 포함시킵니다.
  // 백엔드 설정에 따라 api.put, api.patch, 혹은 api.post를 선택하세요.
  return api.post(`/community/${postId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
// export const updateCommunity = (communityId, data) => {
//   return api.post(`/community/${communityId}`, data);
// };

// 5. 삭제
export const deleteCommunity = (communityId) => {
  return api.delete(`/community/${communityId}`);
};
