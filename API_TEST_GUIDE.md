# Glass Per Hour API 테스트 가이드

## 📚 목차
1. [Postman 설정](#postman-설정)
2. [API 엔드포인트](#api-엔드포인트)
3. [테스트 시나리오](#테스트-시나리오)
4. [WebSocket 테스트](#websocket-테스트)
5. [주요 개념](#주요-개념)

---

## Postman 설정

### 1. Postman 설치
- [Postman 다운로드](https://www.postman.com/downloads/)
- Windows/Mac/Linux 버전 모두 지원

### 2. 컬렉션 Import
1. Postman 실행
2. 좌측 상단 "Import" 버튼 클릭
3. `postman/glass-per-hour.postman_collection.json` 파일 선택
4. 컬렉션이 자동으로 로드됨

### 3. 환경 변수 확인
컬렉션의 Variables 탭에서 다음 변수들이 설정되어 있는지 확인:
- `baseUrl`: `http://localhost:8080`
- `roomId`: (자동 설정됨)
- `roomCode`: (자동 설정됨)
- `userId`: (자동 설정됨)

---

## API 엔드포인트

### 1️⃣ Room Management (방 관리)

#### 방 생성
```http
POST /api/rooms
Content-Type: application/json

{
  "roomName": "",           // 비우면 AI가 자동 생성
  "hostName": "팥붕슈붕"      // 방장 이름
}
```

**응답 예시:**
```json
{
  "roomId": 1,
  "roomCode": "1234",
  "roomName": "오늘만 산다🍺",
  "hostUserId": 1,
  "hostUserName": "팥붕슈붕"
}
```

**설명:**
- 4자리 중복되지 않는 방 코드 자동 생성
- `roomName`이 비어있으면 Gemini AI가 재미있는 이름 생성
- 방장이 자동으로 방에 참여됨

---

#### 방 참여
```http
POST /api/rooms/join
Content-Type: application/json

{
  "roomCode": "1234",
  "userName": "임민규"
}
```

**응답 예시:**
```json
{
  "userId": 2,
  "userName": "임민규",
  "roomId": 1,
  "roomCode": "1234",
  "roomName": "오늘만 산다🍺",
  "isHost": false
}
```

**설명:**
- 4자리 방 코드로 방에 참여
- 종료된 방에는 참여 불가

---

#### 방 정보 조회
```http
GET /api/rooms/{roomCode}
```

**응답 예시:**
```json
{
  "roomId": 1,
  "roomCode": "1234",
  "roomName": "오늘만 산다🍺",
  "createdAt": "2025-11-22T15:30:00",
  "ended": false,
  "participantCount": 2
}
```

---

#### 방 종료
```http
POST /api/rooms/{roomId}/end
```

**설명:**
- 방장이 수동으로 방 종료
- 또는 마지막 1명 남으면 자동 종료

---

### 2️⃣ User Actions (사용자 액션)

#### 잔 추가
```http
POST /api/users/{userId}/drinks
Content-Type: application/json

{
  "drinkType": "SOJU",    // SOJU, BEER, SOMAEK, MAKGEOLLI, FRUIT_SOJU
  "glassCount": 2
}
```

**응답 예시:**
```json
{
  "userId": 1,
  "drinkType": "SOJU",
  "glassCount": 2,
  "sojuEquivalent": 2.0,
  "totalSojuEquivalent": 2.0
}
```

**술 종류별 소주 환산 비율:**
- `SOJU` (소주): 1.0
- `BEER` (맥주): 0.3
- `SOMAEK` (소맥): 0.65
- `MAKGEOLLI` (막걸리): 0.4
- `FRUIT_SOJU` (과일소주): 0.7

**예시:**
- 맥주 3잔 = 소주 0.9잔
- 소맥 2잔 = 소주 1.3잔

---

#### 반응 속도 기록
```http
POST /api/users/{userId}/reaction
Content-Type: application/json

{
  "reactionTimeMs": 350    // 밀리초 단위
}
```

**설명:**
- 순발력 게임 결과 기록
- 서버에서 1시간마다 자동으로 게임 트리거
- 반응 속도는 최종 점수 계산에 30% 반영

---

#### 개인 종료
```http
POST /api/users/{userId}/finish
```

**설명:**
- 개인 타이머 종료
- 시속 잔 수와 캐릭터 레벨 최종 계산
- 마지막 1명 남으면 방 자동 종료

---

### 3️⃣ Results (결과 조회)

#### 개인 결과 조회
```http
GET /api/results/user/{userId}
```

**응답 예시:**
```json
{
  "userId": 1,
  "userName": "김태희",
  "characterLevel": "술고래 🐋",
  "glassPerHour": 3.5,
  "totalDrinks": {
    "SOJU": 5,
    "BEER": 3,
    "SOMAEK": 2
  },
  "funnyDescription": "김태희님, 오늘의 진정한 술고래! 시속 3.5잔의 전설적인 페이스를 기록하셨습니다. 내일 간 건강 챙기세요! 🏆",
  "rank": 1,
  "totalSojuEquivalent": 7.2,
  "averageReactionTime": 420.5
}
```

**캐릭터 레벨:**
- **술고래 🐋**: 시속 3잔 이상
- **주당 🍺**: 시속 2~3잔
- **알쓰 🥴**: 시속 1~2잔
- **술 취한 다람쥐 🐿️**: 시속 1잔 미만

---

#### 방 순위 조회
```http
GET /api/results/room/{roomId}/ranking
```

**응답 예시:**
```json
[
  {
    "userId": 1,
    "userName": "김태희",
    "rank": 1,
    "glassPerHour": 3.5,
    "totalSojuEquivalent": 7.2,
    "characterLevel": "술고래 🐋",
    "isFinished": true
  },
  {
    "userId": 2,
    "userName": "임민규",
    "rank": 2,
    "glassPerHour": 2.1,
    "totalSojuEquivalent": 4.3,
    "characterLevel": "주당 🍺",
    "isFinished": false
  }
]
```

**순위 계산 공식:**
```
최종 점수 = (총 소주 환산량 × 0.7) + (반응속도 점수 × 0.3)
```

---

## 테스트 시나리오

### 시나리오 1: 기본 flow 테스트

1. **방 생성**
   - Request: "1. Create Room" 실행
   - `roomCode`와 `userId`가 자동으로 변수에 저장됨

2. **두 번째 사용자 참여**
   - Request: "2. Join Room" 실행
   - 새로운 `userId` 저장됨

3. **잔 추가 (사용자 1)**
   - Request: "1. Add Drink - Soju" 실행
   - 소주 2잔 추가

4. **잔 추가 (사용자 2)**
   - `userId` 변수를 수동으로 변경
   - Request: "2. Add Drink - Beer" 실행
   - 맥주 3잔 추가

5. **반응 속도 기록**
   - Request: "4. Record Reaction Time" 실행
   - 350ms 기록

6. **실시간 순위 확인**
   - Request: "2. Get Room Ranking" 실행
   - 현재 순위 확인

7. **개인 결과 조회**
   - Request: "1. Get User Result" 실행
   - AI 생성 설명 확인

---

### 시나리오 2: 여러 종류의 술 테스트

1. 소주 2잔 추가 → 소주 환산: 2.0
2. 맥주 5잔 추가 → 소주 환산: 1.5
3. 소맥 1잔 추가 → 소주 환산: 0.65
4. 총 소주 환산량: 4.15잔

---

### 시나리오 3: 자동 종료 테스트

1. 방 생성 (사용자 2명)
2. 사용자 1 종료
3. 사용자 2 종료 → **방 자동 종료**
4. 방 정보 조회 시 `ended: true` 확인

---

## WebSocket 테스트

### WebSocket 연결 정보
- **Endpoint**: `ws://localhost:8080/ws`
- **Protocol**: STOMP
- **Application Prefix**: `/app`
- **Broker Prefix**: `/topic`

### 구독 (Subscribe)
```javascript
// 방별 실시간 업데이트 구독
/topic/room/{roomId}/drink       // 잔 추가 이벤트
/topic/room/{roomId}/ranking     // 순위 업데이트
/topic/room/{roomId}/reaction    // 반응 속도 이벤트
/topic/room/{roomId}/finish      // 사용자 종료 이벤트
/topic/room/{roomId}/autoend     // 방 자동 종료 이벤트
/topic/room/{roomId}/game/start  // 순발력 게임 시작 (1시간마다)
```

### 메시지 전송 (Send)
```javascript
// 잔 추가
/app/room/{roomId}/drink
{
  "userId": 1,
  "drinkType": "SOJU",
  "glassCount": 2
}

// 반응 속도 기록
/app/room/{roomId}/reaction
{
  "userId": 1,
  "reactionTimeMs": 350
}

// 사용자 종료
/app/room/{roomId}/finish
1  // userId

// 순위 요청
/app/room/{roomId}/ranking/request
```

### JavaScript 예시 (SockJS + STOMP)
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 순위 업데이트 구독
    stompClient.subscribe('/topic/room/1/ranking', function(message) {
        const rankings = JSON.parse(message.body);
        console.log('Rankings updated:', rankings);
    });
    
    // 잔 추가 메시지 전송
    stompClient.send('/app/room/1/drink', {}, JSON.stringify({
        userId: 1,
        drinkType: 'SOJU',
        glassCount: 2
    }));
});
```

---

## 주요 개념

### 소주 환산량
모든 술은 소주 기준으로 환산되어 계산됩니다.
- 소주 50ml (도수 17%) = 1.0
- 맥주 500ml (도수 4.5%) = 0.3
- 소맥 1잔 = 0.65
- 막걸리 300ml (도수 6%) = 0.4
- 과일소주 (도수 13%) = 0.7

### 시속 잔 계산
```
시속 잔 = 총 소주 환산량 / 경과 시간(시간)
```

예: 2시간 동안 소주 환산 6잔 → 시속 3잔

### 최종 점수
```
최종 점수 = (총 소주 환산량 × 0.7) + (반응속도 점수 × 0.3)

반응속도 점수 = max(0, 10 - (반응시간 - 500) / 150)
```

- 500ms 이하: 만점 10점
- 2000ms 이상: 0점

---

## 💡 팁

### Postman 사용 팁
1. **자동 변수 저장**: "Create Room"과 "Join Room" 요청 후 자동으로 변수가 저장됩니다
2. **순차 실행**: Runner를 사용하면 전체 시나리오를 자동으로 실행 가능
3. **환경 복제**: 여러 환경(dev, staging, prod)을 만들어서 사용 가능

### 디버깅
1. **H2 Console**: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:glassperhouр`
   - Username: `sa`
   - Password: (비어있음)

2. **로그 확인**: 콘솔에서 모든 API 호출과 DB 쿼리 확인 가능

3. **API 응답 확인**: Postman의 Response 탭에서 상세 정보 확인

---

## 🚀 빠른 시작

1. 백엔드 실행
   ```bash
   cd backend
   ./gradlew bootRun
   ```

2. Postman 실행 및 컬렉션 Import

3. "1. Create Room" 요청 실행

4. 반환된 `roomCode`로 다른 사용자 참여 테스트

5. 잔 추가 및 순위 확인

6. 개인 결과에서 AI 생성 설명 확인!

---

## 📞 문의
문제가 발생하면 백엔드 로그를 확인하거나 H2 Console에서 데이터를 직접 확인하세요.
