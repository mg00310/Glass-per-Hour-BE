# 📚 Glass‑per‑Hour BE API 명세서 (Korean)

> **프로젝트 개요**
> - 실시간 주량 측정·방 생성·순위·반응 속도 테스트를 제공하는 백엔드 서비스
> - 주요 기능: 방 생성·참여, 사용자 음주 기록, 반응 속도 기록, 결과 조회, WebSocket 실시간 알림, Gemini AI 기반 방 이름·결과 설명 생성

---

## 📌 공통 규칙

| 구분 | 내용 |
|------|------|
| **Base URL** | `http://localhost:8080` (프로덕션에서는 환경 변수 `server.port` 로 변경) |
| **데이터 포맷** | `application/json` (요청·응답) |
| **인코딩** | UTF‑8 |
| **인증** | 현재 별도 인증/인가 로직 없음 (추후 JWT 등 적용 가능) |
| **오류 응답** | `{ "timestamp": "...", "status": 4xx/5xx, "error": "...", "message": "...", "path": "/api/..." }` |
| **시간** | ISO‑8601 형식 (`yyyy-MM-dd'T'HH:mm:ss.SSSXXX`) |
| **ID** | `Long` 타입, 자동 증가 (DB 기본키) |
| **코드 규칙** | DTO → `src/main/java/com/drinkspeed/dto` <br> Service → `src/main/java/com/drinkspeed/service` <br> Controller → `src/main/java/com/drinkspeed/controller` <br> WebSocket → `src/main/java/com/drinkspeed/websocket` |

---

## 📂 엔드포인트 목록

| 구분 | 메서드 | URL | 설명 |
|------|-------|-----|------|
| **방 생성** | `POST` | `/api/rooms` | 방을 생성하고 호스트 사용자를 자동으로 만든다. AI가 방 이름을 생성한다 (옵션). |
| **방 참여** | `POST` | `/api/rooms/join` | 방 코드(`roomCode`)와 사용자 이름(`userName`)을 받아 방에 참여한다. |
| **방 조회** | `GET` | `/api/rooms/{roomCode}` | 방 코드로 방 정보를 조회한다. |
| **방 종료** | `POST` | `/api/rooms/{roomId}/end` | 방을 종료하고 모든 사용자를 마감한다. |
| **잔 추가** | `POST` | `/api/users/{userId}/drinks` | 사용자가 마신 술 종류와 잔 수를 기록한다. |
| **반응 속도 기록** | `POST` | `/api/users/{userId}/reaction` | 사용자의 반응 속도(ms)를 기록한다. |
| **개인 종료** | `POST` | `/api/users/{userId}/finish` | 사용자를 방에서 퇴장시키고 최종 결과를 계산한다. |
| **개인 결과 조회** | `GET` | `/api/results/user/{userId}` | 사용자의 최종 결과(시속 잔, 캐릭터, AI 설명 등)를 반환한다. |
| **방 전체 순위 조회** | `GET` | `/api/results/room/{roomId}/ranking` | 방에 속한 모든 사용자의 순위와 점수를 반환한다. |
| **WebSocket 연결** | `ws` | `/ws` | STOMP 기반 실시간 이벤트 전송 (잔 추가, 순위 업데이트, 반응 속도, 방 종료 등). |

---

## 🛠 DTO 정의 (Data Transfer Object)

### 1️⃣ `CreateRoomRequest`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateRoomRequest {
    private String roomName;   // 비워두면 AI가 자동 생성
    private String hostName;   // 방장 이름 (예: "테스트유저")
}
```
**샘플 요청**
```json
{
  "roomName": "",
  "hostName": "테스트유저"
}
```

### 2️⃣ `CreateRoomResponse`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateRoomResponse {
    private Long roomId;
    private String roomCode;   // 4자리 코드 (예: "1234")
    private String roomName;   // AI가 만든 방 이름 혹은 폴백
    private Long hostUserId;
    private String hostUserName;
}
```
**샘플 응답**
```json
{
  "roomId": 2,
  "roomCode": "1468",
  "roomName": "술자리 파티 by AI",
  "hostUserId": 2,
  "hostUserName": "테스트유저"
}
```

### 3️⃣ `JoinRoomRequest`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JoinRoomRequest {
    private String roomCode;   // 방 코드 (4자리)
    private String userName;   // 참여자 이름
}
```
**샘플 요청**
```json
{
  "roomCode": "1468",
  "userName": "김민수"
}
```

### 4️⃣ `JoinRoomResponse`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JoinRoomResponse {
    private Long userId;
    private String userName;
    private Long roomId;
    private String roomCode;
}
```

### 5️⃣ `AddDrinkRequest`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddDrinkRequest {
    private String drinkType;   // "SOJU", "BEER", "SOMAEK", "MAKGEOLLI", "FRUIT_SOJU"
    private Integer glassCount; // 마신 잔 수
}
```
**샘플**
```json
{
  "drinkType": "BEER",
  "glassCount": 3
}
```

### 6️⃣ `AddDrinkResponse`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddDrinkResponse {
    private Double totalSojuEquivalent; // 누적 소주 환산량
    private Double glassPerHour;        // 현재 시속 잔
    private Integer characterLevel;      // 0: 일청담 다이버, 1: 술 취한 다람쥐, 2: 지갑은 지킨다, 3: 술고래 후보생, 4: 인간 알코올
}
```

### 7️⃣ `AddReactionRequest`
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddReactionRequest {
    private Long reactionTimeMs; // 반응 속도 (밀리초)
}
```

### 8️⃣ `UserResultResponse` (개인 결과)
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResultResponse {
    private Long userId;
    private String userName;
    private String characterLevel;
    private Double glassPerHour;
    private Map<DrinkRecord.DrinkType, Integer> totalDrinks;
    private String funnyDescription;   // Gemini AI가 만든 설명
    private Integer rank;
    private Double totalSojuEquivalent;
    private Double averageReactionTime;
}
```

### 9️⃣ `RankingResponse` (방 순위)
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RankingResponse {
    private Long userId;
    private String userName;
    private Integer rank;
    private Double glassPerHour;
    private Double totalSojuEquivalent;
    private Integer characterLevel;
    private Boolean isFinished;
}
```

---

## 📡 WebSocket (STOMP) 이벤트 정의

| 목적 | Destination (클라이언트 구독) | Payload 타입 |
|------|------------------------------|--------------|
| **잔 추가** | `/topic/room/{roomId}/drink` | `AddDrinkResponse` |
| **순위 업데이트** | `/topic/room/{roomId}/ranking` | `List<RankingResponse>` |
| **반응 속도 이벤트** | `/topic/room/{roomId}/reaction` | `AddReactionRequest` |
| **사용자 종료** | `/topic/room/{roomId}/finish` | `UserResultResponse` |
| **게임 시작** | `/topic/room/{roomId}/game/start` | (시작 알림, 문자열) |

> **클라이언트 예시 (JS)**
> ```js
> const stompClient = Stomp.over(new SockJS('/ws'));
> stompClient.connect({}, () => {
>   stompClient.subscribe('/topic/room/123/ranking', msg => console.log(JSON.parse(msg.body)));
> });
> ```

---

## 📦 에러 코드 및 메시지

| HTTP 상태 | 상황 | 응답 예시 |
|-----------|------|-----------|
| **400** | 파라미터 누락 / 형식 오류 | `{ "status":400, "error":"Bad Request", "message":"roomCode is required", "path":"/api/rooms/join" }` |
| **404** | 존재하지 않는 방·사용자·리소스 | `{ "status":404, "error":"Not Found", "message":"Room not found", "path":"/api/rooms/abcd" }` |
| **409** | 중복 방 코드·이미 종료된 방 | `{ "status":409, "error":"Conflict", "message":"Room already ended", "path":"/api/rooms/123/end" }` |
| **500** | 서버 내부 오류 (예: Gemini API 호출 실패) | `{ "status":500, "error":"Internal Server Error", "message":"Failed to generate room name via Gemini API", "path":"/api/rooms" }` |

---

## 🧭 Gemini AI 연동 상세

- **사용 모델**: `gemini-2.5-flash` (현재 `v1` 엔드포인트 사용) 
- **요청 포맷** (JSON) 

```json
{
  "contents": [
    {
      "parts": [
        { "text": "재미있고 창의적인 술자리 방 이름을 하나만 생성해줘. 방 이름은 한국어로 10자 이내로 작성하고, 술자리 분위기에 맞게 유머러스하게 만들어줘. 방 이름만 출력하고 다른 설명은 하지 마. 그리고 맨뒤에 by AI라는 텍스트를 붙여줘" }
      ]
    }
  ]
}
```

- **응답 예시** 

```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          { "text": "술자리 파티 by AI" }
        ]
      }
    }
  ]
}
```

> **폴백 로직**
> - API 키가 없거나 호출 실패 시 `RoomNameGenerator.generateFallbackRoomName()` 에서 미리 정의된 15개의 랜덤 방 이름 중 하나를 반환합니다.
> - `funnyDescription` 도 동일하게 `RankingCalculator.generateFallbackDescription()` 로 대체됩니다.

---

## 📄 전체 API 흐름 예시

1. **방 생성**
   - `POST /api/rooms` → `CreateRoomRequest` (roomName 빈값) 
   - 서버 → Gemini 호출 → 방 이름 반환 (`"술자리 파티 by AI"`). 
   - 응답: `CreateRoomResponse` (roomId, roomCode, roomName, hostUserId, hostUserName)

2. **사용자 참여**
   - `POST /api/rooms/join` → `JoinRoomRequest` (roomCode, userName) 
   - 응답: `JoinRoomResponse` (userId, userName, roomId, roomCode)

3. **잔 추가**
   - `POST /api/users/{userId}/drinks` → `AddDrinkRequest` 
   - 서버 → DB에 기록 → `AddDrinkResponse` 반환 (시속 잔, 캐릭터 레벨 등) 
   - WebSocket `/topic/room/{roomId}/drink` 로 실시간 전파

4. **반응 속도 기록**
   - `POST /api/users/{userId}/reaction` → `AddReactionRequest` 
   - WebSocket `/topic/room/{roomId}/reaction` 로 전파

5. **개인 종료**
   - `POST /api/users/{userId}/finish` → Gemini `generateFunnyDescription` 호출 → `UserResultResponse` 반환 
   - WebSocket `/topic/room/{roomId}/finish` 로 전파

6. **방 전체 순위 조회**
   - `GET /api/results/room/{roomId}/ranking` → `List<RankingResponse>` 반환 

---

## 🛠 추가 구현 팁

| 기능 | 구현 포인트 |
|------|-------------|
| **API 키 관리** | `application.yml`에 `gemini.api.key` 를 환경 변수(`$env:GEMINI_API_KEY`) 로 대체 → 보안 강화 |
| **버전 호환** | 현재 `gemini-2.5-flash` 모델은 `v1` 엔드포인트만 지원 → `application.yml`에 `v1` URL 사용 |
| **에러 로깅** | `RoomNameGenerator`와 `RankingCalculator`에 `logger.error` 로 전체 스택 트레이스 기록 |
| **테스트** | `src/test/java/...` 에 `MockWebServer` 로 Gemini API 모킹 → 단위 테스트 가능 |
| **Swagger/OpenAPI** | `springdoc-openapi-ui` 의존성 추가 → `/swagger-ui.html` 로 자동 문서 제공 (선택 사항) |

---


## 📚 마무리

- 위 명세서는 현재 구현된 **REST API**와 **WebSocket** 이벤트를 모두 포괄합니다.
- Gemini AI 연동은 **방 이름**과 **결과 설명** 두 곳에서 사용되며, **폴백** 로직을 통해 API 키가 없거나 호출 실패 시에도 서비스가 정상 동작하도록 설계되었습니다.
- 필요에 따라 **인증**, **권한**, **Swagger** 등을 추가하면 프로덕션 수준의 API가 완성됩니다.

궁금한 점이나 추가하고 싶은 기능이 있으면 언제든 알려 주세요! 🚀
