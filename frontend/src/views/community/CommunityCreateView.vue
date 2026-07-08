<template>
  <div class="write-page">
    <div class="edit-container">
      <h2>커뮤니티 글쓰기</h2>

      <div class="input-section">
        <label for="title">제목</label>
        <input
          id="title"
          v-model="post.title"
          type="text"
          placeholder="나누고 싶은 이야기의 제목을 적어주세요."
        />
      </div>

      <div class="input-section">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="post.description"
          placeholder="따뜻한 소통을 위해 서로를 배려하는 마음을 담아주세요."
        ></textarea>
      </div>

      <div class="input-section">
        <label for="image">사진 첨부</label>
        <input
          id="image"
          type="file"
          @change="handleFileChange"
          accept="image/*"
          class="file-input"
        />
      </div>

      <div class="actions">
        <button @click="$router.back()" class="cancel-btn">취소</button>
        <button @click="writePost" class="save-btn">등록하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { createCommunity } from '@/api/community';
import { useUserStore } from '@/stores/userStore';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();

const post = ref({
  title: '',
  description: '',
});

const imageFile = ref(null);

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
  }
};

const writePost = async () => {
  if (!post.value.title.trim() || !post.value.description.trim()) {
    alert('제목과 내용을 모두 입력해주세요.');
    return;
  }

  if (!userStore.isLogin) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  try {
    const formData = new FormData();
    formData.append('title', post.value.title);
    formData.append('description', post.value.description);

    if (imageFile.value) {
      formData.append('imageFile', imageFile.value);
    }

    await createCommunity(formData);

    alert('게시글이 성공적으로 등록되었습니다. ✨');
    router.push('/community');
  } catch (error) {
    console.error(error);
    alert('작성 중 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
.write-page {
  min-height: 100vh;
  padding: 60px 20px;
}
.edit-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid #f3f0ec;
}
h2 {
  color: #3d2b1f;
  margin-bottom: 35px;
  font-weight: 800;
  text-align: center;
  font-size: 1.8rem;
}
.input-section {
  margin-bottom: 25px;
}
label {
  display: block;
  font-weight: 700;
  margin-bottom: 10px;
  color: #8c7b6e;
  font-size: 0.95rem;
}
input[type='text'],
textarea {
  width: 100%;
  padding: 16px;
  border: 1px solid #e2ddd9;
  border-radius: 12px;
  outline: none;
  font-size: 1rem;
  color: #5d4a3e;
  background-color: #fcfaf8;
  transition: all 0.3s ease;
}
input[type='text']:focus,
textarea:focus {
  border-color: #3d2b1f;
  background-color: white;
  box-shadow: 0 0 0 4px rgba(61, 43, 31, 0.05);
}
textarea {
  min-height: 300px;
  resize: none;
  line-height: 1.7;
}
/* 💡 새로 추가된 파일 입력 폼 스타일 */
.file-input {
  width: 100%;
  padding: 12px;
  font-size: 0.95rem;
  color: #5d4a3e;
  background-color: #fcfaf8;
  border: 1px dashed #c2b9b0;
  border-radius: 12px;
  cursor: pointer;
}
.file-input:focus {
  border-color: #3d2b1f;
}
.actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}
.save-btn {
  flex: 2;
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 18px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition:
    transform 0.2s,
    opacity 0.2s;
}
.save-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}
.cancel-btn {
  flex: 1;
  background: #f3f0ec;
  color: #8c7b6e;
  border: none;
  padding: 18px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
.cancel-btn:hover {
  background: #ebe7e2;
}
::placeholder {
  color: #c2b9b0;
  font-weight: 400;
}
</style>
