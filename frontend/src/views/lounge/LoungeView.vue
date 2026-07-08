<template>
  <div class="lounge-container">
    <div class="lounge-header">
      <h2>🛋️ 채팅방 입장 (방 번호: {{ roomId }})</h2>
      <button @click="leaveRoom" class="btn-leave">나가기</button>
    </div>

    <hr class="divider" />

    <div class="chat-section">
      <div class="status-bar">
        <span :class="isConnected ? 'badge-success' : 'badge-danger'">
          {{ isConnected ? '● Connected' : '○ Disconnected' }}
        </span>
        <span class="user-info" v-if="userName">
          <strong>{{ userName }}</strong
          >님으로 참여 중
        </span>
      </div>

      <div ref="chatWindow" class="chat-window">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message-wrapper"
        >
          <div v-if="msg.isSystem" class="system-message">
            <span>{{ msg.content }}</span>
          </div>

          <div
            v-else
            :class="[
              'chat-bubble-group',
              msg.name === userName ? 'my-chat' : 'other-chat',
            ]"
          >
            <span class="chat-user" v-if="msg.name !== userName">{{
              msg.name
            }}</span>
            <div class="chat-bubble">
              {{ msg.content }}
            </div>
          </div>
        </div>
      </div>

      <div class="input-action-bar">
        <input
          v-model="inputMessage"
          @keyup.enter="sendMessage"
          :disabled="!isConnected"
          placeholder="메시지를 입력하세요..."
          class="styled-input"
        />
        <button @click="sendMessage" :disabled="!isConnected" class="btn-send">
          전송
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  onMounted,
  onBeforeUnmount,
  nextTick,
  watch,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Client } from '@stomp/stompjs';
import { useUserStore } from '@/stores/userStore';
import SockJS from 'sockjs-client';
// LoungeView.vue
// 💡 기존 import 문을 아래와 같이 수정 (getMessagesByRoomId 추가)
import {
  getRooms,
  createRoom as createRoomApi,
  getMessagesByRoomId,
} from '@/api/lounge';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const roomId = route.params.id;
const userName = computed(() => userStore.nickname || '알 수 없음');

const inputMessage = ref('');
const messages = ref([]);
const isConnected = ref(false);
const chatWindow = ref(null);

let stompClient = null;

// roomId 변경 감지
watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId !== oldId) {
      // 1. 기존 연결 종료
      if (stompClient) {
        stompClient.deactivate();
      }
      // 2. 메시지 초기화 (이전 방 데이터 삭제)
      messages.value = [];
      // 3. 재연결 및 재구독
      connectWebSocket();
    }
  },
);

const scrollToBottom = async () => {
  await nextTick();
  if (chatWindow.value) {
    chatWindow.value.scrollTop = chatWindow.value.scrollHeight;
  }
};

const connectWebSocket = () => {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/chat-app'),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = (frame) => {
    isConnected.value = true;

    // 구독 (Subscribe)
    stompClient.subscribe(`/topic/chat/${roomId}`, (chat) => {
      const data = JSON.parse(chat.body);

      messages.value.push({
        name: data.name,
        content: data.content,
        // 서버에서 오는 JSON 키값(isSystem)을 정확히 매칭
        isSystem: !!data.isSystem,
      });
      scrollToBottom();
    });

    // 입장 알림 발송
    stompClient.publish({
      destination: `/app/chat/${roomId}/enter`,
      body: JSON.stringify({
        roomId: roomId,
        id: userStore.id,
        name: userName.value,
        content: `${userName.value}님이 입장하셨습니다.`,
        isSystem: true, // 프론트에서 보낼 때는 isSystem 명시
      }),
    });
  };

  stompClient.onStompError = (frame) => {
    console.error('STOMP 에러 발생:', frame);
  };

  stompClient.activate();
};

const sendMessage = () => {
  if (!inputMessage.value.trim() || !stompClient || !stompClient.connected)
    return;

  stompClient.publish({
    destination: `/app/chat/${roomId}/message`,
    body: JSON.stringify({
      roomId: roomId,
      id: userStore.id,
      name: userName.value,
      content: inputMessage.value,
      isSystem: false,
    }),
  });

  inputMessage.value = '';
};

const leaveRoom = () => {
  router.push('/lounge'); // 목록 페이지로 이동
};

const fetchPreviousMessages = async () => {
  try {
    const response = await getMessagesByRoomId(roomId);
    // 서버에서 받은 과거 대화 내역을 기존 메시지 배열에 덮어씌움
    messages.value = response.data.map((msg) => ({
      // 💡 msg.name이 서버에서 보내주는 이름이라면 그대로 쓰고,
      // 💡 없으면 '알 수 없음' 등으로 처리합니다.
      name: msg.name || '알 수 없음',
      content: msg.content,
      isSystem: false,
    }));
    scrollToBottom();
  } catch (error) {
    console.error('과거 대화 내역을 불러오지 못했습니다.', error);
  }
};

onMounted(() => {
  fetchPreviousMessages(); // 💡 입장하자마자 내역 불러오기
  connectWebSocket(); // 그 다음 웹소켓 연결
});

onBeforeUnmount(() => {
  if (stompClient) {
    stompClient.deactivate();
  }
});
</script>

<style scoped>
.lounge-container {
  max-width: 700px;
  margin: 30px auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}
.lounge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.lounge-header h2 {
  margin: 0;
  flex: 1;
  min-width: 0;
}
.btn-leave {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-leave {
  background: #fff0f0;
  color: #e57373;
}

.btn-leave:hover {
  background: #ffe5e5;
  color: #d32f2f;
}
.divider {
  border: 0;
  height: 1px;
  background: #eee;
  margin: 20px 0;
}
.status-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 13px;
}
.badge-success {
  color: #4caf50;
  font-weight: bold;
}
.badge-danger {
  color: #ff4d4f;
  font-weight: bold;
}
.user-info {
  color: #555;
}
.chat-window {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  height: 500px;
  overflow-y: auto;
  padding: 15px;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.system-message {
  text-align: center;
  color: #888;
  font-size: 12px;
  margin: 5px 0;
  background: rgba(0, 0, 0, 0.05);
  padding: 4px 10px;
  border-radius: 20px;
  align-self: center;
}
.chat-bubble-group {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}
.chat-user {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
  padding: 0 4px;
}
.chat-bubble {
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.4;
  word-break: break-all;
}
.my-chat {
  margin-left: auto;
  align-items: flex-end;
}
.my-chat .chat-bubble {
  background-color: #4caf50;
  color: white;
  border-top-right-radius: 0;
}
.other-chat {
  margin-right: auto;
  align-items: flex-start;
}
.other-chat .chat-bubble {
  background-color: #ffffff;
  color: #333;
  border: 1px solid #e0e0e0;
  border-top-left-radius: 0;
}
.input-action-bar {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.styled-input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.styled-input:focus {
  border-color: #4caf50;
}
.btn-send {
  padding: 12px 20px;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}
.btn-send:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
