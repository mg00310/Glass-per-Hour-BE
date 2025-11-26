# 재미로 보는 주량 측정기 - Backend

**나의 주량은 시속 몇 잔?** 🍺

데이터베이스 없이 간단하게 실행하고, 재미있는 캐릭터와 AI 생성 설명으로 결과를 확인하는 주량 측정 애플리케이션의 백엔드입니다.

## 🎯 주요 기능

- **간편한 시작**: 별도의 방 생성 없이 닉네임 입력만으로 바로 시작
- **인메모리 작동**: 데이터베이스 설정 없이 즉시 실행 가능
- **전체 사용자 랭킹**: 모든 사용자의 주량을 비교하는 전체 랭킹 제공
- **다양한 주종 지원**: 소주, 맥주, 소맥, 막걸리, 과일소주
- **소주 환산 계산**: 모든 술을 소주 기준으로 자동 환산
- **시속 잔 계산**: 마신 양과 시간을 기반으로 시속 잔 수 계산
- **캐릭터 시스템**: 주량에 따라 재미있는 캐릭터 레벨 부여
- **AI 결과 설명**: Gemini API로 생성된 재치 있는 개인 결과 설명

## 🛠 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **AI**: Google Gemini API
- **Build Tool**: Gradle 8.5

## 📁 프로젝트 구조

```
backend/
├── src/main/java/com/drinkspeed/
│   ├── DrinkSpeedApplication.java    # 메인 애플리케이션
│   ├── config/                        # 설정 (CORS, Async)
│   ├── controller/                    # REST 컨트롤러
│   │   └── UserController.java
│   ├── service/                       # 비즈니스 로직
│   │   ├── UserService.java
│   │   └── GeminiService.java
│   ├── domain/                        # 도메인 객체
│   │   └── User.java
│   ├── dto/                           # 요청/응답 DTO
│   └── util/                          # 유틸리티
│       ├── AlcoholCalculator.java
│       └── RankingCalculator.java
└── src/main/resources/
    └── application.yml               # 설정 파일
```

## 🚀 빠른 시작

### 1. 사전 요구사항
- Java 17 이상
- Gradle 8.5 이상 (또는 포함된 gradlew 사용)

### 2. Gemini API Key 설정 (선택)
AI 결과 설명을 받으려면 API 키를 환경 변수로 설정해야 합니다.
```bash
# Windows PowerShell
$env:GEMINI_API_KEY="your-api-key-here"

# Linux/Mac
export GEMINI_API_KEY="your-api-key-here"
```
> **참고**: API key가 없으면 AI 결과 설명이 기본 메시지로 대체됩니다.

### 3. 빌드 및 실행
```bash
# Windows
gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

### 4. 서버 확인
- **서버 주소**: http://localhost:8000

## 📡 API 명세

### 사용자 (Users)
- `POST /api/users` : 사용자 생성
- `POST /api/users/{userId}/drinks` : 주량 기록 (잔 추가)
- `POST /api/users/{userId}/reaction` : 반응 속도 기록
- `POST /api/users/{userId}/finish` : 주량 측정 종료
- `GET /api/users/{userId}/ai-message` : AI 분석 메시지 조회

### 랭킹 (Rankings)
- `GET /api/rankings` : 전체 사용자 랭킹 조회

> 상세한 API 명세는 `API_SPEC_KR.md` 파일을 참고하세요.

## 💡 주요 로직

### 소주 환산 비율
| 술 종류 | 환산 비율 | 예시 |
|---------|----------|------|
| 소주 | 1.0 | 소주 2잔 = 2.0 |
| 맥주 | 0.3 | 맥주 3잔 = 0.9 |
| 소맥 | 0.65 | 소맥 2잔 = 1.3 |
| 막걸리 | 0.4 | 막걸리 2잔 = 0.8 |
| 과일소주 | 0.7 | 과일소주 2잔 = 1.4 |

### 캐릭터 레벨
- **술고래 🐋**: 시속 3잔 이상
- **주당 🍺**: 시속 2~3잔
- **알쓰 🥴**: 시속 1~2잔
- **술 취한 다람쥐 🐿️**: 시속 1잔 미만

## 👥 팀원

- **백엔드**: 김태희, 임민규
- **프론트엔드**: 김영은, bettytopy

## 📄 라이선스

This project is for educational purposes.

---

**즐거운 술자리 되세요! 🍻**
