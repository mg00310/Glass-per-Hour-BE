# 실시간 주량 측정 서비스

> 실시간으로 술자리 참여자들의 음주량을 공유하며 함께 즐길 수 있는 온라인 술자리 플랫폼
주량을 ‘시속’의 관점에서 계산하여 보다 더 정확한 주량을 계산하도록 도와줍니다👍
> 

---

## 💡프로젝트 개요

### **“혹시… 주량이 어떻게 되세요?”**

술자리에서 어김없이 듣게 되는 이 질문.

주량을 과장해서 말하는 사람도 있고, 반대로 일부러 줄여 말하는 사람도 있다. 또한, 사회초년생이나 술 경험이 적은 사람들은 자신의 주량을 정확히 파악하지 못한 채 마시다 보니 곤란한 상황을 마주하기도 한다. 이런 모습을 보며 우리 조는 누구나 부담 없이 자신의 **실제 주량을 알아갈 수 있는 서비스**가 필요하다고 생각했다.

그래서 우리는 **실시간 주량 측정 웹 서비스**를 기획했다. 마시는 술의 종류와 속도를 기반으로 현재 자신의 상태를 확인하며, 술자리를 즐기는 동안 자연스럽게 자신의 주량을 파악해 나갈 수 있도록 돕는 서비스이다.

## 🎯 프로젝트 목표

### **1️⃣핵심 목표: '주량 인식 및 관리' 플랫폼 구축**

이 프로젝트의 최종 목표는 술자리 참여자들이 부담 없이, 재미있게 자신의 실제 주량을 파악하고, 안전한 음주 문화를 형성할 수 있도록 돕는 실시간 웹 서비스를 제공하는 것이다.

- **실시간 상호작용 극대화**: 실시간 랭킹 업데이트와 경과 시간 표시를 통해 술자리 참여자 간의 경쟁 및 재미 요소를 제공하고, 사용자의 몰입도를 높인다.
- **개인 맞춤형 피드백**: 음주 데이터와 AI 분석을 결합하여 개인의 주량 레벨과 코멘트를 산출하고, 이를 공유 가능한 형태로 제공함으로써 주량 기록 및 소셜 기능을 강화한다.

### 2️⃣**기술 및 구현 목표**

- **사용자 상태 관리 최적화**: useState와 이벤트 핸들링을 활용하여 사용자의 음주량 입력(주종, 잔 수)을 정확하고 빠르게 처리하는 반응성 높은 프론트엔드 환경을 구축한다.
- **데이터 활용 극대화**: 입력 데이터 뿐만 아니라 술 마신 시간을 랭킹에 명시적으로 표시하여, 사용자가 음주 속도에 따른 자신의 상태 변화를 더 심층적으로 파악할 수 있도록 데이터를 시각화한다.

### **3️⃣사용자 가치 및 사회적 목표**

- **불필요한 과음 예방**: 자신의 주량과 상태를 실시간으로 확인하며 마실 수 있도록 지원하여, 곤란한 상황을 미연에 방지하고 자기 주도적인 음주 문화를 조성한다.
- **재미 요소 제공**: 랭킹 경쟁을 통해 술자리 자체의 흥미요소를 만들고, 서비스 사용을 즐거운 경험으로 만든다.

---

## 👥 팀&팀원 소개

### 팀명 :  팥붕슈붕

팥붕파 2명&슈붕파 2명으로 취향이 갈렸지만 조화롭게 하나의 팀을 이루자는 의미에서 팥붕슈붕이라고 지었습니다.💓

### 팀원

- 김영은: 프론트엔드 개발
    - 리액트 및 기능 구현, 페이지 라우팅 담당
- 김수완: 프론트엔드 개발
    - 실시간 랭킹 업데이트 구현
    - AI를 활용한 이미지 생성 및 프롬프트 작성
- 김태희: 프론트엔드, 백엔드 개발
    - ux/ui 디자인
    - ver 0.5 제작
    - 발표자료 제작
- 임민규: 백엔드 개발
    - AI 기능 개발: Gemini API 연동 및 AI 기반 핵심 기능 구현
    - 서버 아키텍처 설계: Spring Boot 기반 백엔드 서버
    아키텍처 설계 및 구축
    - API 검증: Postman을 활용한 API 엔드포인트 테스트 및
    서비스 안정성 검증
    

## 📄 주요 기능

### 1️⃣ **실시간 주량 측정 및 개인 분석**

- 사용자의 음주 데이터를 실시간으로 수집하고 처리하여, 객관적인 상태 파악을 돕는다.
- 사용자가 웹에서 마신 술의 주종(소주, 맥주, 막걸리 등)과 잔 수 데이터를 입력하면, 시스템은 이 정보를 실시간으로 수집합다.
- 잔 수 계산 추가 로직을 통해 주종별 알코올 도수를 감안하여 총 음주량을 '소주 잔 기준' 등으로 표준화하여 환산하고, 그 결과를 화면에 실시간으로 표시한다.

### 2️⃣ AI 기반 주량 레벨 및 코멘트 산출

- 측정이 종료되면, 시스템은 누적된 음주 데이터와 경과 시간을 AI 분석 모듈로 전송한다.
- AI기능을 통해 사용자의 주량에 대한 최종 레벨과 맞춤형 코멘트가 생성된다.
- 실시간 상호작용 및 결과 공유
    - 술자리의 흥미를 높이고, 측정 결과를 간편하게 기록하고 확산할 수 있도록 돕는 기능이다.

### **3️⃣** 실시간 랭킹 업데이트 및 경과 시간 표시

- 전체 랭킹 조회 기능을 통해 모든 참여자의 음주량이 주기적으로 집계된다.
- 사용자 화면에서는 이 데이터를 주기적으로 반영하여 랭킹 순위와 잔 수를 실시간으로 업데이트한다.
- '술 마신 시간'을 함께 표시하여 사용자들은 자신의 음주 속도까지 고려하며 경쟁 및 상태 변화를 확인할 수 있다.
- 결과 페이지 생성 및 공유
    - 이미지 파일 저장: 이 결과 화면을 이미지 파일로 변환하여 갤러리에 저장하거나 다운로드할 수 있도록 기능을 제공한다.
- URL 링크 공유
    - 결과 페이지로 바로 연결되는 고유 URL을 복사하여 카카오톡 등 외부 메신저를 통해 간편하게 공유할 수 있다.

## ⚡ 사용 기술

- **Frontend**: React + Typescript
- **Backend**: Spring
- **AI** : Gemini 2.5 flash

:

## ⌛향후 발전/개선 방향

- ‘방 만들기’ 기능 추가
    
    기존에도 다수 사용자가 링크를 통한 서비스 동시 이용이 가능하기는 하였으나, 동일한 술자리 구성원만 참여 가능한 독립된 공간을 제공하기 위해 ‘방 만들기’ 기능을 도입한다.
    
- ‘순발력 테스트’ 미니 게임 추가
    
    화면에 빨간색 버튼이 나타난 뒤 일정 시간이 지나면 무작위로 초록색으로 변하고, 사용자는 초록색으로 바뀌는 즉시 화면을 터치해야 한다. 이 반응 속도가 이전에 비해 유의미하게 느려질 경우, 사용자가 음주로 인해 순발력이 저하된 상태임을 파악하는 지표로 활용할 수 있다.
    
- UI/UX 개선

---

# 🎨 FE(Frontend)

## ⚙️ 주요 기능

### 1️⃣ **실시간 음주량 측정**

사용자의 입력(주종 선택 및 잔 수 추가)을 받아서 상태를 관리하는 것이 핵심이다.

1. **React State 관리:**
    - `useState` 를 사용하여  술자리 가진 시간과 사용자가 마신 총 음주량 데이터를 관리한다.
    - 예시 상태 구조
        
        `const [drinks, setDrinks] = useState([]); // [{ type: '소주', count: 3 }, { type: '맥주', count: 5 }]
        const [seconds, setSeconds] = useState(0);` 
        
2. **잔 수 증가 및 감소 버튼 이벤트 처리:**
    - 사용자가 특정 주종의 `+ 버튼`을 클릭하면 이벤트 핸들러 함수가 실행된다.
    - 사용자가 실수로 잔 수 올렸을 때 `- 버튼`눌러서 취소할 수 있다.
    - 이 함수는 `setDrinks`를 호출하여 현재 상태(`drinks`)를 업데이트한다.
3. **시속 계산 및 주량 계산:**
    - 주종마다 도수가 다르다는 점을 감안하여, 주량을 소주 기준으로 환산하는 로직을 추가하였다.
    
    1. 술 종류별로 도수가 다르기 때문에 소주를 1잔로 잡고 맥주는 소주 0.7잔, 소맥 1.3잔, 막걸리는 0.8잔, 과일소주는 0.5잔으로 환산하여 합계를 계산한다.
    2. 소주 환산 합계를 이용해서 시속 몇 잔인지 구하고, 시속에 따라 레벨을 부여한다. level 0부터 level 4까지 있으며, 시속 2잔 미만이면 level 0, 시속 2잔~4잔이면 level1, 시속 4잔~6잔 이하면 level2, 시속 6잔~8잔이면 level3, 시속 8잔 초과면 level4로 계산한다.
    3. 주량 계산 방법
    : 소주 환산 합계를 이용해서 총 마신 양을 소주 병 단위로 변환한다. 소주 1병 = 7.2잔 기준으로 변환하며 0.5병(반 병) 단위로 표시한다.

### 2️⃣ **실시간 랭킹 업데이트**

1. **데이터 반영:** 주기적인 업데이트를 통해 새로운 랭킹 데이터를 받으면, 화면에 보이는 랭킹 목록을 즉시 업데이트하여 순위와 잔 수를 바꿔서 보여준다.
2. **타이머 표시:** 타이머를 구현하여 랭킹과는 별개로 사용자들이 술자리에 참여한 총 경과 시간을 화면에 표시한다.

### **3️⃣ 결과 페이지 공유**

1. **결과 페이지 렌더링**
데이터 처리: 입력된 음주 데이터를 기반으로 AI가 최종 주량 레벨과 코멘트를 산출한다.
UI 구성: React 컴포넌트 내에서 이 데이터를 받아 최종 결과 페이지를 렌더링한다.
2. **이미지 파일 저장 및 링크 공유**
이미지 파일을 갤러리에 저장하거나, URL를 복사하여 다른 사용자에게 자신의 결과를 공유할 수 있다.

## 📁 파일 구조

<aside>

```
src
 ┣ api
 ┃ ┗ api.ts
 ┣ assets
 ┃ ┗ images # 레벨, 술 이미지
 ┣ components
 ┃ ┗ ui
 ┃ ┃ ┗ Button.tsx
 ┣ pages
 ┃ ┣ MeasurePage
 ┃ ┃ ┣ DrinkCounter.tsx
 ┃ ┃ ┣ DrinkList.tsx
 ┃ ┃ ┣ Measure.tsx
 ┃ ┃ ┗ RankingList.tsx
 ┃ ┣ InputName.tsx
 ┃ ┣ Result.tsx
 ┃ ┗ StartPage.tsx
 ┣ api.ts
 ┣ App.css
 ┗App.tsx
```

</aside>

---

# 🛠️ BE(Backend)

---

# 💡 프로젝트 개요

- 데이터베이스 설정 없이 간단히 실행하는 인메모리 기반 주량 측정 백엔드 서비스
- **주요 기능**: 사용자 생성, 주량 기록, 전체 랭킹 조회, Gemini AI 기반 결과 설명 생성

---

## 📚 API 명세서

### 📌 공통 규칙

| **구분** | **내용** |
| --- | --- |
| **Base URL** | `http://localhost:8000` |
| **데이터 포맷** | `application/json` (요청·응답) |
| **인증 방식** | 별도 인증 로직 없음 (Open Access) |
| **오류 포맷** | `{ "timestamp": "...", "status": 4xx, "error": "...", "message": "..." }` |
| **시간 포맷** | ISO‑8601 (`yyyy-MM-dd'T'HH:mm:ss.SSSXXX`) |

---

### 📂 엔드포인트 상세

### 1. 사용자 생성

닉네임으로 새로운 사용자를 생성하고 고유 ID를 발급받습니다.

- **URL**: `/api/users`
- **Method**: `POST`
- **Description**: 측정 시작 시 최초 1회 호출

**Request Body**

```jsx
{
  "userName": "홍길동"
}
```

**Response (200 OK)**

```jsx
{
    "id": 1,
    "userName": "홍길동",
    "joinedAt": "2023-11-29T10:00:00.000Z",
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

---

### 2. 주량 기록 (잔 추가)

사용자가 마신 술의 종류와 잔 수를 기록하여 주량을 누적합니다.

- **URL**: `/api/users/{userId}/drinks`
- **Method**: `POST`
- **Path Variable**: `userId` (Long)

**Request Body**

```jsx
{
  "drinkType": "SOJU",
  "glassCount": 2
}
```

**`drinkType` Enum 종류**

- `"SOJU"` (소주)
- `"BEER"` (맥주)
- `"SOMAEK"` (소맥)
- `"MAKGEOLLI"` (막걸리)
- `"FRUIT_SOJU"` (과일소주)

**Response (200 OK)**

- 업데이트된 `User` 객체 반환

---

### 3. 측정 종료

측정을 종료하고 최종 레벨을 계산합니다. **백그라운드에서 AI 분석 요청이 시작됩니다.**

- **URL**: `/api/users/{userId}/finish`
- **Method**: `POST`
- **Path Variable**: `userId` (Long)

**Response (200 OK)**

```jsx
{
    "id": 1,
    "userName": "홍길동",
    "finishedAt": "2023-11-28T12:30:00.000Z",
    "totalSojuEquivalent": 2.5,
    "characterLevel": 3,
    "aiMessage": null, // 생성 대기 중
}
```

---

### 4. AI 메시지 조회 (Polling)

AI가 생성한 분석 코멘트를 조회합니다.

- **URL**: `/api/users/{userId}/ai-message`
- **Method**: `GET`
- **Description**: `finish` 호출 후 값이 채워질 때까지 주기적으로 호출 필요

**Response (200 OK)**

```jsx
{
  "aiMessage": "오늘 컨디션이 좋으시네요! 소주 2.5병에 해당하는 주량에도 불구하고 반응 속도가 준수합니다."
}
```

> 참고: 분석 중일 경우 aiMessage는 null일 수 있습니다.
> 

---

### 5. 전체 랭킹 조회

모든 사용자를 주량(`totalSojuEquivalent`) 내림차순으로 정렬하여 반환합니다.

- **URL**: `/api/rankings`
- **Method**: `GET`

**Response (200 OK)**

```jsx
[
    {
        "id": 2,
        "userName": "술고래",
        "totalSojuEquivalent": 3.0,
        "characterLevel": 4
    },
    {
        "id": 1,
        "userName": "홍길동",
        "totalSojuEquivalent": 2.5,
        "characterLevel": 3
    }
]
```

---

## 🛠 데이터 모델 (Domain)

### `User` Class

```jsx
// com.drinkspeed.domain.User

public class User {
    private Long id;                  // 사용자 ID
    private String userName;          // 닉네임
    private LocalDateTime joinedAt;   // 시작 시간
    private LocalDateTime finishedAt; // 종료 시간
    private Double totalSojuEquivalent; // 총 소주 환산량 (병)
    private Double sojuCount = 0.0;
    private Double beerCount = 0.0;
    private Double somaekCount = 0.0;
    private Double makgeolliCount = 0.0;
    private Double fruitsojuCount = 0.0;
    private Integer characterLevel;     // 계산된 레벨
    private String aiMessage;           // AI 분석 결과
}
```

---

## 🧭 API 호출 시나리오

1. **게임 시작 (`POST /users`)**
    - 유저 생성 후 `userId: 1` 획득
2. **술 마시기  (`POST /drinks`)**
    - 술 종류/잔 수 입력 전송 (반복)
3. **게임 종료 (`POST /finish`)**
    - 최종 결과 확인 (이 시점에는 AI 메시지 없음)
4. **결과 화면 (`GET /ai-message`, `GET /rankings`)**
    - AI 메시지가 생성되었는지 폴링하여 확인 후 표시
    - 전체 랭킹 리스트 호출하여 순위 표시

---

### 📦 에러 코드 (Error Codes)

| **HTTP Status** | **Error** | **설명** |
| --- | --- | --- |
| **400** | `Bad Request` | 요청 파라미터 누락 또는 JSON 형식 오류 |
| **404** | `Not Found` | 해당 ID의 사용자를 찾을 수 없음 |
| **500** | `Internal Server Error` | 서버 내부 로직 에러 |

## 💻 AI

- AI 기반 음주 결과 분석 기능
    - AI 술자리 해설가 페르소나를 기반으로, 사용자에게 개인화된 피드백을 제공하는 핵심 프롬프트를 설계
    - 사용자의 음주 시간과 주량을 복합적으로 분석하여, 음주 패턴(단기 집중, 장기 지속형 등)에 대한 깊이 있는 코멘트를 생성하는 AI 로직을 구현
    - Gemini API 연동의 안정성을 높이기 위해, API 호출 실패 시를 대비한 FallBack 시스템을 구축하여 사용자 경험이 저하되지않도록 처리했습니다.

## 📁 파일 구조

```
Glass-per-Hour-BE/
 ├─ src/main/java/com/drinkspeed/
 │   ├─ DrinkSpeedApplication.java
 │   ├─ config/
 │   │    ├─ AsyncConfig.java
 │   │    ├─ CORSConfig.java
 │   ├─ controller/
 │   │    ├─ UserController.java
 │   ├─ domain/
 │   │    ├─ User.java
 │   ├─ dto/
 │   │    ├─ RankingResponse.java
 │   ├─ exception/
 │   │    ├─ GlobalExceptionHandler.java
 │   ├─ service/
 │   │    ├─ GeminiService.java
 │   │    ├─ UserService.java
 │   ├─ util/
 │        ├─ AlcoholCalculator.java
 │        ├─ RankingCalculator.java
 └─ build.gradle

```
