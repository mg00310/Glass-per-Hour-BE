# 재미로 보는 주량 측정기 - Backend

**나의 주량은 시속 몇 잔?** 🍺

실시간으로 친구들과 함께 주량을 측정하고, 재미있는 캐릭터와 AI 생성 설명으로 결과를 확인하는 주량 측정 애플리케이션의 백엔드입니다.

## 🎯 주요 기능

- **방 생성 및 참여**: 4자리 코드로 간편하게 방 생성 및 참여
- **AI 방 이름 생성**: Gemini API를 활용한 창의적인 방 이름 자동 생성
- **실시간 순위**: WebSocket을 통한 실시간 순위 업데이트
- **다양한 주종 지원**: 소주, 맥주, 소맥, 막걸리, 과일소주
- **소주 환산 계산**: 모든 술을 소주 기준으로 자동 환산
- **시속 잔 계산**: 마신 양과 시간을 기반으로 시속 잔 수 계산
- **캐릭터 시스템**: 술고래, 주당, 알쓰, 술 취한 다람쥐
- **순발력 게임**: 1시간마다 자동 트리거되는 반응 속도 테스트
- **AI 결과 설명**: Gemini API로 생성된 재미있는 개인 결과 설명

## 🛠 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (인메모리, 개발용)
- **Real-time**: WebSocket + STOMP
- **AI**: Google Gemini API
- **Build Tool**: Gradle 8.5
- **ORM**: Spring Data JPA / Hibernate

## 📁 프로젝트 구조

```
backend/
├── src/main/java/com/drinkspeed/
│   ├── DrinkSpeedApplication.java    # 메인 애플리케이션
│   ├── config/                        # 설정
│   │   ├── WebSocketConfig.java      # WebSocket 설정
│   │   └── CORSConfig.java           # CORS 설정
│   ├── controller/                    # REST 컨트롤러
│   │   ├── RoomController.java
│   │   ├── UserController.java
│   │   └── ResultController.java
│   ├── websocket/                     # WebSocket 컨트롤러
│   │   └── RoomMessageController.java
│   ├── service/                       # 비즈니스 로직
│   │   ├── RoomService.java
│   │   ├── UserService.java
│   │   └── ResultService.java
│   ├── domain/                        # 엔티티
│   │   ├── Room.java
│   │   ├── User.java
│   │   ├── DrinkRecord.java
│   │   └── ReactionTest.java
│   ├── repository/                    # JPA 리포지토리
│   │   ├── RoomRepository.java
│   │   ├── UserRepository.java
│   │   ├── DrinkRecordRepository.java
│   │   └── ReactionTestRepository.java
│   ├── dto/                          # 요청/응답 DTO
│   ├── util/                         # 유틸리티
│   │   ├── AlcoholCalculator.java
│   │   ├── RoomNameGenerator.java
│   │   └── RankingCalculator.java
│   └── scheduler/                    # 스케줄러
│       └── ReactionGameScheduler.java
└── src/main/resources/
    └── application.yml               # 설정 파일
```

## 🚀 빠른 시작

### 1. 사전 요구사항
- Java 17 이상
- Gradle 8.5 이상 (또는 포함된 gradlew 사용)

### 2. Gemini API Key 설정 (선택)
```bash
# Windows PowerShell
$env:GEMINI_API_KEY="your-api-key-here"

# Linux/Mac
export GEMINI_API_KEY="your-api-key-here"
```

> **참고**: API key가 없으면 랜덤 방 이름과 기본 설명으로 대체됩니다.

### 3. 빌드 및 실행
```bash
# Windows
gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

### 4. 서버 확인
- **서버 주소**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:glassperhouр`
  - Username: `sa`
  - Password: (비어있음)

## 📡 API 테스트

### Postman 사용
1. `postman/glass-per-hour.postman_collection.json` 파일을 Postman에 Import
2. [API_TEST_GUIDE.md](./API_TEST_GUIDE.md)를 참고하여 테스트 시작

### 주요 API 엔드포인트
```
POST   /api/rooms                        # 방 생성
POST   /api/rooms/join                   # 방 참여
GET    /api/rooms/{roomCode}             # 방 정보 조회
POST   /api/rooms/{roomId}/end           # 방 종료

POST   /api/users/{userId}/drinks        # 잔 추가
POST   /api/users/{userId}/reaction      # 반응 속도 기록
POST   /api/users/{userId}/finish        # 개인 종료

GET    /api/results/user/{userId}        # 개인 결과 조회
GET    /api/results/room/{roomId}/ranking # 방 순위 조회
```

### WebSocket 엔드포인트
```
연결: ws://localhost:8080/ws

구독:
/topic/room/{roomId}/drink       # 잔 추가 이벤트
/topic/room/{roomId}/ranking     # 순위 업데이트
/topic/room/{roomId}/reaction    # 반응 속도 이벤트
/topic/room/{roomId}/finish      # 사용자 종료 이벤트
/topic/room/{roomId}/game/start  # 순발력 게임 시작

전송:
/app/room/{roomId}/drink         # 잔 추가
/app/room/{roomId}/reaction      # 반응 속도 기록
/app/room/{roomId}/finish        # 사용자 종료
```

## 💡 주요 로직

### 소주 환산 비율
| 술 종류 | 환산 비율 | 예시 |
|---------|----------|------|
| 소주 | 1.0 | 소주 2잔 = 2.0 |
| 맥주 | 0.3 | 맥주 3잔 = 0.9 |
| 소맥 | 0.65 | 소맥 2잔 = 1.3 |
| 막걸리 | 0.4 | 막걸리 2잔 = 0.8 |
| 과일소주 | 0.7 | 과일소주 2잔 = 1.4 |

### 시속 잔 계산
```
시속 잔 = 총 소주 환산량 / 경과 시간(시간)
```

### 캐릭터 레벨
- **술고래 🐋**: 시속 3잔 이상
- **주당 🍺**: 시속 2~3잔
- **알쓰 🥴**: 시속 1~2잔
- **술 취한 다람쥐 🐿️**: 시속 1잔 미만

### 최종 점수 (순위 계산)
```
최종 점수 = (총 소주 환산량 × 0.7) + (반응속도 점수 × 0.3)
```

## ⚙️ 설정

### application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:glassperhouр
  h2:
    console:
      enabled: true
      path: /h2-console

gemini:
  api:
    key: ${GEMINI_API_KEY:your-gemini-api-key-here}
    url: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

scheduler:
  reaction-game:
    interval-hours: 1
    enabled: true
```

## 🔧 개발 가이드

### 빌드
```bash
gradlew build
```

### 테스트
```bash
gradlew test
```

### JAR 파일 생성
```bash
gradlew bootJar
```

### 실행
```bash
java -jar build/libs/backend-1.0.0.jar
```

## 📝 API 문서

상세한 API 문서는 [API_TEST_GUIDE.md](./API_TEST_GUIDE.md)를 참고하세요.

## 🐛 디버깅

### H2 Console 접속
1. 브라우저에서 http://localhost:8080/h2-console 접속
2. JDBC URL: `jdbc:h2:mem:glassperhouр`
3. Username: `sa`
4. Password: (비어있음)

### 로그 확인
- 모든 SQL 쿼리가 콘솔에 출력됩니다
- API 호출 로그 확인 가능

## 👥 팀원

- **백엔드**: 김태희, 임민규
- **프론트엔드**: 김영은, bettytopy

## 📄 라이선스

This project is for educational purposes.

---

**즐거운 술자리 되세요! 🍻**
