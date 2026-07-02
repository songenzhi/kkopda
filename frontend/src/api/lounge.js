import api from './axios'; // 카페에서 쓰신 것과 동일한 axios 인스턴스

// 1. 전체 채팅방 목록 조회
export const getRooms = () => {
  return api.get('/lounge');
};

// 2. 채팅방 생성
export const createRoom = (name) => {
  // 백엔드 컨트롤러가 @RequestParam으로 받으므로 params에 담아 전송
  return api.post('/lounge/rooms', null, {
    params: { name },
  });
};

// 3. 특정 채팅방 상세 정보 조회 (입장 전 확인용 등)
export const getRoomById = (roomId) => {
  return api.get(`/lounge/rooms/${roomId}`);
};

export const getMessagesByRoomId = (roomId) => {
  return api.get(`/lounge/rooms/${roomId}/message`);
};

// 채팅방 삭제
// 5. 리뷰 삭제
export const deleteRoomById = (roomId) => {
  return api.delete(`/lounge/${roomId}`);
};
