# 📚 Glass‑per‑Hour BE API 명세서 (v4 - In-Memory)

> **프로젝트 개요**
> - 데이터베이스 설정 없이 간단히 실행하는 인메모리 기반 주량 측정 백엔드 서비스
> - 주요 기능: 사용자 생성, 주량 기록, 전체 랭킹 조회, Gemini AI 기반 결과 설명 생성

---

## 📌 공통 규칙

| 구분 | 내용 |
|------|------|
| **Base URL** | `http://localhost:8000` |
| **데이터 포맷** | `application/json` (요청·응답) |
| **인증** | 별도 인증 로직 없음 |
| **오류 응답** | `{ "timestamp": "...", "status": 4xx/5xx, "error": "...", "message": "...", "path": "/api/..." }` |
| **시간** | ISO‑8601 형식 (`yyyy-MM-dd'T'HH:mm:ss.SSSXXX`) |

---

## 📂 엔드포인트 목록

| 구분 | 메서드 | URL | 설명 |
|------|-------|-----|------|
| **사용자 생성** | `POST` | `/api/users` | 닉네임으로 새 사용자를 생성하고 정보를 반환한다. |
| **주량 기록** | `POST` | `/api/users/{userId}/drinks` | 사용자가 마신 술 종류와 잔 수를 기록한다. |
| **측정 종료** | `POST` | `/api/users/{userId}/finish` | 사용자 측정을 종료하고 최종 결과를 계산한다. |
| **AI 메시지 조회** | `GET` | `/api/users/{userId}/ai-message`| AI가 생성한 결과 메시지를 조회한다. (폴링용) |
| **사용자 상세 조회** | `GET` | `/api/users/{userId}` | ID로 특정 사용자의 상세 정보를 조회한다. (공유용) |
| **전체 랭킹 조회** | `GET` | `/api/rankings` | 모든 사용자의 랭킹을 주량 순으로 정렬하여 반환한다. |

---

## 🛠 DTO 정의 (Data Transfer Object)

### 1️⃣ 요청 (Requests)

#### `POST /api/users`
```json
{
  "userName": "홍길동"
}
```

#### `POST /api/users/{userId}/drinks`
```json
{
  "drinkType": "SOJU",
  "glassCount": 2
}
```
- `drinkType`: "SOJU", "BEER", "SOMAEK", "MAKGEOLLI", "FRUIT_SOJU" 중 하나

### 2️⃣ 응답 (Responses)

#### `User` 객체
대부분의 API는 `User` 객체 또는 `User` 객체의 리스트를 반환합니다.
```java
// com.drinkspeed.domain.User
public class User {
    private Long id;
    private String userName;
    private LocalDateTime joinedAt;
    private LocalDateTime finishedAt;

    private Double totalSojuEquivalent; // 총 소주 환산량

    // 개별 주종별 잔 수
    private Double sojuCount;
    private Double beerCount;
    private Double somaekCount;
    private Double makgeolliCount;
    private Double fruitsojuCount;

    private Integer characterLevel;     // 캐릭터 레벨
    private String aiMessage;           // AI 분석 메시지
}
```
**샘플 응답 (`POST /api/users` 성공 시)**
```json
{
    "id": 1,
    "userName": "홍길동",
    "joinedAt": "2023-11-27T10:00:00.000Z",
    "finishedAt": null,
    "totalSojuEquivalent": 0.0,
    "sojuCount": 0.0,
    "beerCount": 0.0,
    "somaekCount": 0.0,
    "makgeolliCount": 0.0,
    "fruitsojuCount": 0.0,
    "characterLevel": null,
    "aiMessage": null
}
```

#### `GET /api/rankings`
- `User` 객체의 리스트 `List<User>` 를 반환합니다.
- `totalSojuEquivalent`가 높은 순으로 정렬됩니다.

---

## 🧭 Gemini AI 연동 상세

- **사용 API**: Google Gemini API
- **트리거**: `POST /api/users/{userId}/finish` API가 호출되면, 비동기적으로 AI 메시지 생성을 요청합니다.
- **결과 확인**: `GET /api/users/{userId}/ai-message` 를 주기적으로 호출(Polling)하여 `aiMessage` 필드가 채워졌는지 확인해야 합니다.

> **폴백 로직**: API 키가 없거나 호출에 실패하면 "AI 분석에 실패했습니다." 라는 기본 메시지가 저장됩니다.

---

## 📄 전체 API 흐름 예시

1.  **사용자 생성**
    -   `POST /api/users` 에 `{ "userName": "주량측정맨" }` 요청
    -   응답으로 `User` 객체를 받고, `id` (예: `1`)를 저장해 둔다.

2.  **주량 기록**
    -   `POST /api/users/1/drinks` 에 `{ "drinkType": "BEER", "glassCount": 2 }` 요청
    -   소주 환산량이 업데이트된 `User` 객체를 응답으로 받는다.

3.  **측정 종료**
    -   `POST /api/users/1/finish` 호출
    -   `finishedAt` 시간이 기록되고 최종 `characterLevel`이 계산된 `User` 객체를 응답으로 받는다.
    -   동시에 백그라운드에서는 AI 메시지 생성이 시작된다.

4.  **결과 확인 및 공유**
    -   `GET /api/rankings` 를 호출하여 전체 사용자 순위를 확인한다.
    -   `GET /api/users/1/ai-message` 를 주기적으로 호출하여 AI 분석 메시지를 받아온다.
    -   결과 페이지 공유가 필요할 경우, `GET /api/users/1` 을 호출하여 해당 사용자의 전체 데이터를 조회할 수 있다.

---

## 📦 에러 코드 및 메시지

| HTTP 상태 | 상황 | 응답 예시 |
|-----------|------|-----------|
| **400** | 파라미터 누락 / 형식 오류 | `{ "status":400, "error":"Bad Request", ... }` |
| **404** | 존재하지 않는 사용자 | `{ "status":404, "error":"Not Found", "message":"사용자를 찾을 수 없습니다: 99", "path":"/api/users/99/drinks" }` |
| **500** | 서버 내부 오류 | `{ "status":500, "error":"Internal Server Error", ... }` |
