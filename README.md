# ☕ kkopda (꼽다) - 사용자 맞춤형 카페 정보 & 커뮤니티 플랫폼

<div align="center">
  <img src="https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot_3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL_8.4-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/Vue.js_3-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white">
  <img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white">
</div>

<br>

## 📝 프로젝트 소개

**'kkopda(꼽다)'**는 콘센트, 와이파이, 주차 여부 등 사용자가 필요로 하는 맞춤형 카페 정보를 제공하고, 유저들 간 자유롭게 소통할 수 있는 커뮤니티 게시판과 실시간 채팅을 지원하는 웹 서비스입니다.

단순한 기능 구현(CRUD)을 넘어, **세션 기반 인증(Session Auth)을 통한 철저한 API 보안 설계**와 **대용량 데이터 처리를 고려한 페이징(Pagination) 아키텍처**, 그리고 **웹소켓(WebSocket)을 활용한 실시간 통신** 등 웹 애플리케이션의 핵심 이론을 실제 서비스에 견고하게 적용하는 데 집중하여 개발했습니다.

<br>

## 🛠 기술 스택 (Tech Stack)

### Backend
- **Framework:** Spring Boot 3.x, Spring Security
- **Data Access:** Spring Data JPA
- **Database:** MySQL 8.4.9
- **Build Tool:** Gradle / Maven

### Frontend
- **Framework:** Vue.js 3 (Composition API), Vite
- **State Management:** Pinia
- **Routing & Http:** Vue Router, Axios

<br>

## ✨ 핵심 기능 (Key Features)

### 1. 🗺️ 카카오 API 기반 카페 탐색 및 관리 (Cafe Management)
- **카카오 맵 API 연동:** 지도 기반으로 위치를 정밀하게 탐색하고 새로운 카페 정보를 등록하는 기능 제공
- **카페 정보 CRUD:** 카페 등록, 상세 조회, 정보 수정 및 삭제 기능 구현
- **실시간 혼잡도 관리:** 직관적인 뱃지 형태의 **'혼잡도'** 등록 시스템을 통해 유저들이 카페의 실시간 상태를 파악하고 공유할 수 있도록 지원
- 와이파이, 콘센트, 주차 공간 등 카테고리별 맞춤형 세부 옵션 제공

### 2. 💬 실시간 라운지 채팅 (Real-time Lounge Chat)
- **WebSocket 프로토콜 활용:** 서버와 클라이언트 간의 양방향 실시간 통신을 통해 유저 간 소통이 가능한 라운지 채팅방 구현
- 채팅방 생성, 삭제 기능 및 실시간 유저 입장/퇴장 이벤트 처리 시스템 구축

### 3. 🔒 안전한 인증 및 유저 시스템 (Security & Member)
- **Spring Security & HttpSession:** 클라이언트와 서버 간의 세션 구조를 활용하여 안전한 로그인/로그아웃 프로세스 구현
- 회원가입, 회원탈퇴 및 개인 활동 내역을 확인할 수 있는 개인화된 '마이페이지' 기능 제공
- 컨트롤러와 서비스 레이어의 검증 분리를 통해 타인의 리소스에 대한 비정상적인 접근을 서버 단에서 원천 차단 (`401 Unauthorized` 반환)

### 4. 📄 커뮤니티 및 맞춤형 리뷰 시스템 (Community & Review)
- **Spring Data JPA Paging:** `Pageable` 인터페이스를 활용하여 서버 사이드 페이징(Server-side Paging) 쿼리를 최적화하고 대규모 데이터 처리 인프라 구축
- 자유롭게 소통하고 정보를 공유할 수 있는 **커뮤니티 게시판 CRUD**
- 개별 카페 상세 페이지 하위에서 독립적으로 작동하는 **평점 및 리뷰 작성/관리(CRUD)** 기능

<br>

## 🔥 트러블 슈팅 (Troubleshooting)

<details>
<summary><b>1. 클라이언트 파라미터 조작을 통한 인가(Authorization) 우회 취약점(IDOR) 해결</b></summary>
<br>

- **문제 상황:** 초기 설계 시 API URL 파라미터(`?userId=X`)나 요청 바디에 유저 식별자를 직접 전송하여 검증함. 이로 인해 악의적인 사용자가 개발자 도구(F12) 네트워크 탭에서 파라미터를 타인의 식별자로 변조하여 요청할 경우, 권한 없이 타인의 리소스를 수정/삭제할 수 있는 심각한 보안 취약점(IDOR)을 인지함.
- **해결 과정:**
  1. 클라이언트(프론트엔드) 통신 규격에서 유저 식별자 파라미터를 전면 제거하여 변조 가능성을 차단함.
  2. `LoginController`에서 로그인 성공 시 인증된 유저 ID를 서버의 보안 영역인 `HttpSession`에 저장하도록 변경.
  3. API 요청이 들어올 때 백엔드 `Controller`에서 세션을 주입받아 서버 메모리에서 직접 유저 정보를 꺼내어 1차 검증하도록 개선.
  4. `Service` 레이어에서 데이터의 실제 원작성자 ID와 세션에서 추출한 ID를 비교하는 비즈니스 로직(Resource Ownership Verification)을 추가함.
- **결과:** 클라이언트 측 데이터 조작 위험을 원천 차단하고, 서버가 주도권을 갖는 견고한 권한 인증 체계를 구축 완료함.
</details>

<details>
<summary><b>2. Spring Data JPA 페이징 데이터 직렬화 및 프론트엔드 API 매핑 구조 불일치 해결</b></summary>
<br>

- **문제 상황:** 백엔드에서 페이징 처리된 데이터를 정상적으로 반환했음에도 불구하고, Vue 컴포넌트에서 데이터 리스트와 하단 페이지네이션 버튼이 정상적으로 렌더링되지 않는 문제 발생.
- **해결 과정:**
  1. 백엔드 컨트롤러의 반환 타입 정의와 실제 리턴 시 `PagedModel` 객체로 감싸서 반환하는 과정에서 JSON 응답 데이터 구조의 불일치가 발생했음을 파악함.
  2. 백엔드 반환 타입을 `ResponseEntity<PagedModel<DTO>>`로 명확히 통일하여 페이징 메타데이터 직렬화 규격을 표준화함.
  3. Vue의 Axios 응답 처리부에서 표준화된 응답 구조(`res.data.content` 및 `res.data.page.totalPages`)에 맞춰 페이징 상태 데이터를 정확하게 매핑하도록 프론트엔드 로직을 개선함.
- **결과:** 페이징 데이터가 프론트엔드와 완벽히 동기화되어 게시글 목록(10개 단위) 및 하단 네비게이션 버튼 UI가 유연하게 작동함.
</details>

<details>
<summary><b>3. 자바스크립트 느슨한 비교(Timing) 맹점에 의한 비정상 UI 렌더링 해결</b></summary>
<br>

- **문제 상황:** 로그아웃 상태이거나 서버로부터 게시글 데이터를 받아오기 전 순간에, 본인 게시글 상세 페이지에서만 노출되어야 할 '수정/삭제' 버튼이 화면에 일시적으로 노출되는 현상 발생 (클릭 시 백엔드 단에서는 세션 검증을 통해 401 에러로 안전하게 방어됨).
- **해결 과정:**
  1. Vue의 `computed` 속성 내부에서 두 식별자를 비교할 때(`userStore.user.id === post.value.userId`), 비로그인 상태이거나 데이터 로딩 전 단계에서 양쪽 값이 모두 존재하지 않아 `undefined === undefined` 형태로 평가되어 `true`를 반환하는 자바스크립트 언어적 맹점을 발견함.
  2. 조건 비교 연산을 수행하기 전, 유저의 로그인 상태와 객체의 존재 여부를 먼저 검증하는 Guard Clause(방어 코드)를 도입함.
  3. 옵셔널 체이닝(`?.`)을 적용하여 안전하게 프로퍼티에 접근하고 예외 상황 시 즉시 `false`를 반환하도록 리팩토링함.
- **결과:** 권한이 없는 사용자 및 비로그인 상태의 UI에서 수정/삭제 버튼 노출을 프론트엔드 레벨에서도 완벽히 차단하여 불필요한 API 요청을 줄이고 사용자 경험(UX)을 향상시킴.
</details>

<br>

## 🚀 시작하기 (Getting Started)

### Backend Setup

```bash
# Repository 클론
$ git clone [https://github.com/songenzhi/kkopda.git](https://github.com/songenzhi/kkopda.git)

# MySQL 데이터베이스 생성 (기본 포트: 3306)
CREATE DATABASE kkopja_db;

# 프로젝트 빌드 및 실행
$ ./gradlew bootRun

# 프론트엔드 디렉토리로 이동
$ cd frontend

# 패키지 설치
$ npm install

# 개발 서버 실행 (기본 포트: 5173)
$ npm run dev
