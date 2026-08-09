# 백엔드 팀 전달 사항 (배포 전체 위임)

작성 기준: `develop` 브랜치 최신 커밋 / 프론트 `main` 브랜치 최신 커밋

**이 시점부터 배포·서버 연결 및 잔여 작업 전체를 백엔드 팀에 맡깁니다.**
프론트엔드는 아래 항목들이 없어도 mock 폴백으로 동작하도록 되어 있어 순서대로
처리하시면 되고, **1번(배포 설정)이 가장 급하고 2번은 배포 후 순차 진행 가능**합니다.

**프론트 코드 수정이 필요한 경우**: 새 화면 추가처럼 큰 작업이 아니라 문구·설정값
변경 같은 간단한 수정이라면, 별도 요청 없이 프론트 저장소 폴더를 직접 열어서
고쳐주시면 됩니다 (예: `app.config.js`, `.env`, 문서에 명시된 설정 플래그 등).
구조를 바꿔야 하거나 애매한 부분만 알려주세요.

---

## 1. 배포 시 반드시 설정해야 하는 환경변수

### 1-1. Hibernate ddl-auto (⚠️ 최우선)

```
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

**이유**: 현재 값이 `update` 계열로 추정됩니다. 그래서 Flyway 마이그레이션 없이
Hibernate 가 엔티티를 보고 테이블을 멋대로 만들거나 바꿔온 흔적이 있었고,
실제로 아래 문제들이 발생해 V22~V24 로 뒤늦게 정리했습니다.

- `todo` 테이블이 마이그레이션 없이 존재 (V22 로 백필)
- `user` 테이블에 정체불명의 중복 unique key 생성 (V23 으로 제거)
- `diary` 테이블 컬럼이 엔티티와 어긋남 (V24 로 정정)

**`validate` 로 바꾸지 않으면 같은 드리프트가 계속 재발합니다.**
`validate` 는 "엔티티와 실제 스키마가 다르면 부팅 실패" 라 스키마 불일치를
배포 시점에 즉시 잡아줍니다. 스키마 변경은 앞으로 Flyway 마이그레이션으로만
하시면 됩니다 (다음 버전은 **V26**).

### 1-2. CORS 허용 도메인

```
CORS_ALLOWED_ORIGINS=https://실제-웹도메인
```

기존에 `WebMvcConfig` 에 localhost 4개가 하드코딩돼 있어 운영 도메인을 넣을 수
없었습니다. 환경변수로 분리해두었으니 값만 지정하시면 됩니다 (콤마 구분).

> 참고: iOS/Android 네이티브 앱은 `Origin` 헤더를 보내지 않아 CORS 대상이
> 아닙니다. 이 값은 웹(Expo web, 관리자 페이지)에서 접근할 때만 필요합니다.

### 1-3. Swagger 노출 여부 (선택)

```
SPRINGDOC_SWAGGER_UI_ENABLED=false
SPRINGDOC_API_DOCS_ENABLED=false
```

현재 `/swagger-ui`, `/v3/api-docs` 가 인증 없이 완전히 공개되어 있습니다.
의도한 것이면 그대로 두셔도 되고, 운영에서 닫으려면 위 값을 `false` 로 주세요.
(환경변수로 제어 가능하도록 처리해두었습니다)

---

## 2. HTTPS 전환 (프론트 스토어 심사와 직결)

현재 백엔드가 HTTP(`http://13.125.195.245:8080`)라, 프론트가 `app.config.js`
에서 평문 통신을 강제로 허용하고 있습니다.

```js
// app.config.js
NSAllowsArbitraryLoads: true,   // iOS
usesCleartextTraffic: true,     // Android
```

**이 상태로는 App Store 심사에서 반려될 가능성이 높습니다.**
Cloud Run 이전이든 기존 AWS 서버에 HTTPS(예: ALB + ACM 인증서, 또는 Nginx +
Let's Encrypt)를 붙이는 것이든, 서버가 HTTPS로 응답하게 되면 **프론트 저장소를
직접 열어서** 위 두 줄을 지우고 `.env` 의 API 주소를 `https://` 로 바꿔주세요.
문구 두 줄 삭제하는 간단한 수정이라 별도 요청 없이 처리하시면 됩니다.

---

## 3. 미구현 API (프론트는 mock 으로 동작 중)

각 항목의 스텁 파일은 이미 존재하나 내용이 비어 있습니다.
프론트가 기대하는 정확한 규격은 아래와 같습니다.

### 3-1. 공지사항

| 메서드 | 경로 | 응답 |
|---|---|---|
| GET | `/api/notices` | `Notice[]` |
| GET | `/api/notices/{id}` | `Notice` |

```json
{ "id": "1", "title": "제목", "content": "본문", "publishedAt": "2026.01.30" }
```

빈 스텁: `Notice`, `NoticeRepository`, `NoticeService`, `NoticeRequestDto`
(`NoticeController` 는 파일 자체가 없어 새로 만드셔야 합니다)

### 3-2. 의견 남기기

| 메서드 | 경로 | 요청 |
|---|---|---|
| POST | `/api/feedback` | `{ "satisfaction": 1~5, "content": "..." }` |

빈 스텁: `Feedback`, `FeedbackController`, `FeedbackService`,
`FeedbackRepository`, `FeedbackRequestDto`

### 3-3. 프로필 수정

| 메서드 | 경로 | 요청 |
|---|---|---|
| PATCH | `/api/user/me` | `{ "nickname": "...", "profileUrl": "..." }` |

`User` 엔티티에 `updateProfile(nickname, profileUrl)` 메서드는 이미 있으나,
이를 노출하는 컨트롤러 매핑이 없고 `UserProfileUpdateDto` 가 빈 스텁입니다.

> **프로필 이미지 업로드 방식은 백엔드에서 정해서 그대로 구현해주세요**: 현재
> 프론트는 기기 로컬 파일 URI 를 문자열로 보내는 임시 구조입니다 (`features/user/api.ts`
> 의 `updateMyProfile`). 이미지 업로드용 멀티파트 엔드포인트든 S3/GCS presigned
> URL 방식이든 편하신 쪽으로 만드시고, 프론트 쪽 요청 형태를 그에 맞춰 바꾸는 것도
> 간단한 수정이니 백엔드에서 프론트 폴더 열어서 같이 고쳐주시면 됩니다.

### 3-4. 아이디/비밀번호 찾기 + SMS 인증

**`build.gradle` 에 `com.solapi:sdk:1.0.3` 의존성은 추가돼 있으나 실제 사용
코드가 프로젝트 전체에 한 줄도 없습니다.** (카카오 로그인 작업 시 미리 넣어두고
구현은 안 하신 것으로 보입니다)

제안 규격:

| 메서드 | 경로 | 요청/응답 |
|---|---|---|
| POST | `/api/auth/sms/send` | `{ phone }` |
| POST | `/api/auth/sms/verify` | `{ phone, code }` → `{ verified }` |
| GET | `/api/auth/find-id?phone=` | `[{ type, userId, joinedAt }]` |
| POST | `/api/auth/find-password` | `{ userId, phone }` → 재설정 토큰 |
| PATCH | `/api/auth/password` | `{ resetToken, newPassword }` |

현재 프론트는 SMS 인증 화면이 전부 `setTimeout` 시뮬레이션으로 동작합니다.
(회원가입 본인인증 포함)

### 3-5. 캘린더 일정 목록 (일부만 미구현)

관심 공고 기반 캘린더는 이번에 구현 완료(`/api/v1/calendar/*`)했습니다.
다만 **공고별 할 일 추가 UI 에서 쓸 일정-공고 연동은 이미 동작**하므로
추가 작업 없습니다.

---

## 4. 이번에 완료한 작업 (참고)

프론트 연동을 위해 아래는 이미 구현해서 `develop` 에 머지했습니다.

- **일기(Diary) CRUD** — `feature/#17-diary-crud` 브랜치가 `DiaryService` 가
  빈 스텁이라 컴파일조차 안 되던 상태여서 완성 후 머지.
  조회/수정/삭제 전 경로에 소유자 검증이 없어 **남의 일기 ID 만 알면 접근
  가능하던 문제**도 함께 수정했습니다.
- **캘린더 관심 공고** (V25) — `announcement_favorite` 테이블 신설,
  `todo.announcement_id` 연결, `/api/v1/calendar/*` 4개 엔드포인트
- **스키마 드리프트 정리** (V22~V24)
- **인증 응답 타입** — `AuthController` 가 전부 `ApiResponse<?>` 로 선언돼 있어
  Swagger 에 응답 스키마가 `object` 로만 나오던 것을 구체 타입으로 변경
- **로그인 차단 버그** — `LoginUserDTO.Req.userId` 에 `@Email` 검증이 걸려 있어,
  이메일 형식이 아닌 아이디로 가입한 사용자는 **로그인이 영구 불가**하던 문제 수정
- **공고 목록 응답** — 지도 마커에 필요한 `lat`/`lng` 가 빠져 있어 추가.
  RDB 조회 경로에서 `thumbnailUrl` 이 항상 null 이던 것도 수정

---

## 5. 확인만 필요한 사항

- **`Volunteer` 엔티티**가 빈 스텁으로 남아 있습니다. 사용 예정이 없으면
  삭제하는 게 좋습니다.
- **`AnnouncementCreatedEvent`, `SearchExcutedEvent`** 리스너 2개도 빈 스텁입니다.
  (`SearchExcuted` 는 오타로 보입니다 — `SearchExecuted`)
- **`AnnouncementSchedule.scheduleDate` 가 `String` 타입**입니다 (`"01.11"` 형식).
  날짜 연산·정렬이 필요해지면 `LocalDate` 로 바꾸는 게 좋습니다.
