<template>
  <div class="edit-page">
    <div class="edit-container">
      <h2>게시글 수정</h2>

      <div class="input-section">
        <label for="title">제목</label>
        <input
          id="title"
          v-model="post.title"
          type="text"
          placeholder="제목을 입력하세요" />
      </div>

      <div class="input-section">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="post.description"
          placeholder="내용을 입력하세요"></textarea>
      </div>

      <div class="input-section">
        <label>사진 관리</label>

        <div v-if="post.imageUrl && !imageDeleted" class="existing-image-box">
          <img
            :src="'http://localhost:8080' + post.imageUrl"
            alt="기존 이미지"
            class="preview-img" />
          <button @click="removeExistingImage" class="remove-img-btn">
            사진 삭제 ✕
          </button>
        </div>

        <input
          id="image"
          type="file"
          @change="handleFileChange"
          accept="image/*"
          class="file-input" />
      </div>

      <div class="actions">
        <button @click="$router.back()" class="cancel-btn">취소</button>
        <button @click="handleUpdate" class="save-btn">수정 완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getCommunity, updateCommunity } from '@/api/community';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const postId = route.params.id;
const post = ref({
  title: '',
  description: '',
  imageUrl: '', // 기존 이미지 URL을 담을 공간
});

// 💡 이미지 처리를 위한 추가 상태값
const imageFile = ref(null); // 새로 첨부한 파일
const imageDeleted = ref(false); // 기존 사진을 지웠는지 여부 (백엔드 DTO와 연결됨!)

// 기존 데이터 불러오기
const fetchPost = async () => {
  try {
    const res = await getCommunity(postId);
    post.value = res.data;
  } catch (error) {
    alert('데이터를 불러오지 못했습니다.');
    router.back();
  }
};

// 💡 새 파일 첨부 시 실행되는 함수
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
    // 새 사진을 올리면 기존 사진은 어차피 덮어씌워지므로 화면에서 숨김
    imageDeleted.value = true;
  }
};

// 💡 기존 사진 삭제 버튼 클릭 시 실행되는 함수
const removeExistingImage = () => {
  imageDeleted.value = true; // 삭제 플래그 켜기
  imageFile.value = null; // 혹시 골라둔 새 파일이 있다면 초기화

  // 파일 입력창(input type="file")의 값도 초기화해주는 디테일
  const fileInput = document.getElementById('image');
  if (fileInput) fileInput.value = '';
};

// 수정 실행
const handleUpdate = async () => {
  if (!post.value.title.trim() || !post.value.description.trim()) {
    alert('제목과 내용을 모두 입력해주세요.');
    return;
  }

  try {
    // 🎯 백엔드가 받을 수 있도록 FormData 생성
    const formData = new FormData();
    formData.append('postId', postId);
    formData.append('title', post.value.title);
    formData.append('description', post.value.description);

    // 새 사진을 골랐다면 추가
    if (imageFile.value) {
      formData.append('imageFile', imageFile.value);
    }

    // 기존 사진을 지웠다면 플래그 추가 ('imageDeleted'는 백엔드 DTO 변수명과 일치해야 함!)
    if (imageDeleted.value) {
      formData.append('imageDeleted', true);
    }

    // JSON 객체(post.value) 대신 formData를 전송!
    await updateCommunity(postId, formData);

    alert('수정이 완료되었습니다.');
    router.push(`/community/${postId}`);
  } catch (error) {
    console.error(error);
    alert('수정 중 오류가 발생했습니다.');
  }
};

onMounted(() => {
  fetchPost();
});
</script>

<style scoped>
/* 기존 스타일은 그대로 유지 */
.edit-page {
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
  margin-bottom: 30px;
  font-weight: 800;
  text-align: center;
}
.input-section {
  margin-bottom: 25px;
}
label {
  display: block;
  font-weight: 700;
  margin-bottom: 10px;
  color: #8c7b6e;
}
input[type='text'],
textarea {
  width: 100%;
  padding: 15px;
  border: 1px solid #e2ddd9;
  border-radius: 12px;
  outline: none;
  font-size: 1rem;
  color: #5d4a3e;
  background-color: #fcfaf8;
}
input[type='text']:focus,
textarea:focus {
  border-color: #3d2b1f;
  background-color: white;
}
textarea {
  min-height: 300px;
  resize: none;
  line-height: 1.6;
}

/* 💡 추가된 이미지 처리 관련 스타일 */
.existing-image-box {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2ddd9;
}
.preview-img {
  display: block;
  max-width: 100%;
  max-height: 200px;
  object-fit: cover;
}
.remove-img-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(255, 255, 255, 0.9);
  color: #e57373;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}
.remove-img-btn:hover {
  background-color: #fff0f0;
}
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

.actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}
.save-btn {
  flex: 2;
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 15px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}
.cancel-btn {
  flex: 1;
  background: #f3f0ec;
  color: #8c7b6e;
  border: none;
  padding: 15px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}
</style>
