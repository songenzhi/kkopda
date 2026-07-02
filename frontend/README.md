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

**'kkopda'**는 콘센트, 와이파이, 주차 여부 등 사용자가 필요로 하는 맞춤형 카페 정보를 제공하고, 유저들 간 자유롭게 소통할 수 있는 커뮤니티 게시판을 지원하는 웹 서비스입니다.

단순한 기능 구현(CRUD)을 넘어, **세션 기반 인증(Session Auth)을 통한 철저한 API 보안 설계**와 **대용량 데이터 처리를 고려한 페이징(Pagination) 아키텍처**를 깊이 있게 고민하며 개발했습니다.

<br>

## 🛠 기술 스택 (Tech Stack)

### Backend

- **Framework:** Spring Boot 3.x, Spring Security
- **Data Access:** Spring Data JPA
- **Database:** MySQL 8.4.9
- **Build Tool:** Gradle

### Frontend

- **Framework:** Vue.js 3 (Composition API), Vite
- **State Management:** Pinia
- **Routing & Http:** Vue Router, Axios

<br>

## ✨ 핵심 기능 (Key Features)

### 1. 🔒 안전한 세션 기반 인증 및 인가 (Security & Auth)

- **Spring Security**와 `HttpSession`을 활용하여 클라이언트와 서버 간의 안전한 세션 로그인 구현
- 무상태(Stateless) API 통신의 한계를 보완하기 위해 CORS 환경에서 `allowCredentials = true` 설정을 통한 세션 쿠키(`JSESSIONID`) 동기화 처리
- 컨트롤러와 서비스 레이어 분리 및 인가(Authorization) 처리를 통해 타인의 게시글/리뷰에 대한 비정상적인 접근(수정/삭제) 원천 차단 (`401 Unauthorized` 반환)

### 2. 📄 커뮤니티 및 페이징 시스템 (Community & Pagination)

- **Spring Data JPA**의 `Pageable` 인터페이스를 활용하여 서버 사이드 페이징(Server-side Paging) 쿼리 최적화
- `PagedModel`을 통해 프론트엔드와 백엔드 간의 페이징 데이터 직렬화/역직렬화 규격 통일
- 게시글 작성, 단건/전체 조회, 수정, 삭제(CRUD) 기능 제공

### 3. ☕ 맞춤형 카페 리뷰 시스템 (Cafe Reviews)

- 와이파이, 콘센트, 주차 공간 등 카페 세부 옵션 제공 및 혼잡도 관리
- 해당 카페에 대한 사용자의 평점 및 리뷰 작성/관리 기능

<br>

## 🔥 트러블 슈팅 (Troubleshooting)

<details>
<summary><b>1. 클라이언트 파라미터 조작을 통한 인가(Authorization) 우회 취약점 해결</b></summary>
<br>

- **문제 상황:** 초기 설계 시 API URL 파라미터(`?userId=X`)로 유저 식별자를 전송함. 이로 인해 악의적인 사용자가 개발자 도구(F12) 네트워크 탭에서 파라미터를 타인의 식별자로 변조하여 요청할 경우, 권한 없이 타인의 게시글을 수정/삭제할 수 있는 심각한 보안 취약점(IDOR)을 인지함.
- **해결 과정:** 1. 클라이언트(프론트엔드)에서 보내는 식별자 파라미터를 통신 규격에서 전면 제거함. 2. `LoginController`에서 로그인 성공 시 인증된 유저 ID를 서버의 `HttpSession`에 안전하게 저장하도록 변경. 3. API 요청이 들어올 때 백엔드 `Controller`에서 `HttpSession`을 주입받아 서버 메모리에서 직접 유저 정보를 꺼내어 검증하도록 개선. (비로그인 요청 시 `401 Unauthorized` 차단) 4. `Service` 레이어에서 해당 데이터의 원작성자 ID와 세션 ID를 비교하는 비즈니스 검증 로직 추가.
- **결과:** 클라이언트 측 데이터 변조 위험을 원천 차단하고, 서버 주도의 견고한 권한 인증 시스템 구축 완료.
</details>

<details>
<summary><b>2. Spring Data JPA 페이징 데이터 직렬화 및 프론트엔드 매핑 에러</b></summary>
<br>

- **문제 상황:** 백엔드에서 페이징 처리된 데이터를 정상 반환했으나, Vue 컴포넌트에서 데이터 리스트와 페이지네이션 버튼 렌더링에 실패함.
- **해결 과정:** 백엔드 컨트롤러의 반환 타입이 `ResponseEntity<Page<DTO>>`로 선언되어 있었으나, 실제 리턴 시 `PagedModel` 객체로 감싸서 반환하고 있어 타입 불일치 및 JSON 응답 구조의 변경이 발생했음을 파악함. 백엔드 반환 타입을 `ResponseEntity<PagedModel<DTO>>`로 명확히 통일하고, Vue의 Axios 응답 처리부에서 `res.data.content`와 `res.data.page.totalPages` 규격에 맞춰 데이터를 맵핑하도록 개선.
- **결과:** 페이징 데이터가 프론트엔드에 정확히 직렬화되어, 게시글 목록(10개 단위)과 하단 페이지 버튼이 완벽하게 동기화되어 렌더링됨.
</details>

<details>
<summary><b>3. 자바스크립트 비교 연산 맹점에 의한 비정상 UI 렌더링 해결</b></summary>
<br>

- **문제 상황:** 로그아웃 상태임에도 불구하고 게시글 상세 페이지에서 본인만 볼 수 있어야 할 '수정/삭제' 버튼이 노출되는 현상 발생. (실제 클릭 시 백엔드 단에서 401 에러로 안전하게 방어됨).
- **해결 과정:** Vue의 `computed` 속성 내부에서 `userStore.user.id === post.value.userId`를 비교할 때, 비로그인 상태일 경우 `undefined === undefined` 형태로 평가되어 `true`를 반환하는 자바스크립트의 맹점을 확인. Guard Clause(방어 코드)를 도입하여 `userStore.user` 객체가 없을 경우 즉시 `false`를 반환하도록 로직 수정.
- **결과:** 비로그인 사용자 및 타인에게는 수정/삭제 버튼 UI 렌더링을 완전히 차단하여 프론트엔드 레벨의 보안성과 사용자 경험(UX) 개선.
</details>

<br>

## 🚀 시작하기 (Getting Started)

### Backend Setup

```bash
# Repository 클론
$ git clone [https://github.com/songenzhi/kkopda.git](https://github.com/songenzhi/kkopda.git)

# MySQL 데이터베이스 생성 (기본 포트: 3306)
CREATE DATABASE kkopja_db;

# 프로젝트 빌드 및 실행 (IntelliJ IDEA 권장)
$ ./gradlew bootRun

# 프론트엔드 디렉토리로 이동
$ cd frontend

# 패키지 설치
$ npm install

# 개발 서버 실행 (기본 포트: 5173)
$ npm run dev
```
