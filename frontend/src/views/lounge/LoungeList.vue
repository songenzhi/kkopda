<template>
  <div class="chat-list-container">
    <div class="header">
      <h2>💬 채팅 목록</h2>
      <p class="subtitle">참여하고 싶은 대화방을 선택해보세요.</p>
    </div>

    <div class="create-room-section">
      <input
        v-model="newRoomName"
        @keyup.enter="createRoom"
        placeholder="새로운 방 이름을 입력하세요"
        class="room-input"
      />
      <button @click="createRoom" class="btn-create">방 만들기</button>
    </div>

    <hr class="divider" />

    <div class="room-list">
      <div
        v-for="room in chatRooms"
        :key="room.roomId"
        class="room-card"
        @click="enterRoom(room.roomId)"
      >
        <div class="room-info">
          <h3>{{ room.name }}</h3>
        </div>
        <span
          ><button class="btn-enter">입장</button
          ><button @click.stop="deleteRoom(room.roomId)" class="btn-del">
            삭제
          </button>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  getRooms,
  createRoom as createRoomApi,
  deleteRoomById,
} from '@/api/lounge';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();

const chatRooms = ref([]);
const newRoomName = ref('');

// 방 목록 불러오기 (API 연동)
const fetchRooms = async () => {
  try {
    const response = await getRooms(); // 💡 깔끔해진 API 호출!
    chatRooms.value = response.data;
  } catch (error) {
    console.error('방 목록을 불러오지 못했습니다.', error);
  }
};

// 방 만들기 기능
const createRoom = async () => {
  if (!newRoomName.value.trim()) {
    alert('방 이름을 입력해주세요!');
    return;
  }

  if (!userStore.isLogin) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  try {
    await createRoomApi(newRoomName.value); // 💡 여기서도 함수 하나로 끝!

    newRoomName.value = ''; // 전송 후 입력창 비우기
    fetchRooms(); // 방이 만들어졌으니 목록 갱신!
  } catch (error) {
    console.error('방 생성에 실패했습니다.', error);
  }
};

onMounted(() => {
  fetchRooms();
});

const enterRoom = (roomId) => {
  // 💡 선택한 방의 ID를 파라미터로 담아 채팅창(ChatView)으로 이동
  if (!userStore.isLogin) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  router.push(`/lounge/${roomId}`);
};

const deleteRoom = async (roomId) => {
  if (!userStore.isLogin) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }
  if (confirm('정말로 이 채팅방을 삭제하시겠습니까?')) {
    try {
      await deleteRoomById(roomId);
      alert('삭제되었습니다.');
      router.push('/lounge');
      fetchRooms();
    } catch (error) {
      alert('삭제 실패');
    }
  }
};
</script>

<style scoped>
.chat-list-container {
  max-width: 700px;
  margin: 30px auto;
  padding: 20px;
}
.header h2 {
  color: #333;
  margin-bottom: 5px;
}
.subtitle {
  color: #666;
  font-size: 14px;
}

/* 💡 새로 추가된 방 만들기 영역 스타일 */
.create-room-section {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
.room-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}
.room-input:focus {
  border-color: #4caf50;
}
.btn-create {
  padding: 10px 15px;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
}
.btn-create:hover {
  background-color: #555;
}

.divider {
  border: 0;
  height: 1px;
  background: #eee;
  margin: 20px 0;
}
.room-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.room-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 12px;
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #4caf50;
}
.room-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}
.btn-enter,
.btn-del {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-enter {
  background: #f3f0ec;
  color: #8c7b6e;
}

.btn-entern:hover {
  background: #e2ddd9;
  color: #3d2b1f;
}

.btn-del {
  background: #fff0f0;
  color: #e57373;
}

.btn-del:hover {
  background: #ffe5e5;
  color: #d32f2f;
}
</style>
