# Glass-per-Hour 데이터베이스 스키마

이 문서는 'Glass-per-Hour' 프로젝트의 데이터베이스 스키마를 설명합니다.
데이터베이스는 총 4개의 테이블로 구성되어 있습니다.

## 📈 전체 구조 (ERD)

```
[ rooms ] 1--N [ users ] 1--N [ drink_records ]
                       1--N [ reaction_tests ]
```

-   하나의 **방(`rooms`)**에는 여러 명의 **사용자(`users`)**가 참여할 수 있습니다.
-   한 명의 **사용자(`users`)**는 여러 개의 **음주 기록(`drink_records`)**과 **반응 속도 테스트 기록(`reaction_tests`)**을 가질 수 있습니다.

---

## 🗂️ 테이블 명세

### 1. `rooms`

술 게임이 진행되는 '방' 정보를 저장합니다.

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PK, Auto-increment | 방의 고유 식별자 |
| `room_code` | VARCHAR(4) | Not Null, Unique | 방 참여를 위한 4자리 코드 |
| `room_name` | VARCHAR(255) | Not Null | 방 이름 (사용자 지정 또는 AI 생성) |
| `status` | VARCHAR(255) | Not Null | 방의 현재 상태 (`WAITING`, `PLAYING`, `FINISHED`) |
| `created_at` | TIMESTAMP | Not Null | 방 생성 시간 |
| `ended_at` | TIMESTAMP | | 방 종료 시간 (종료 시 기록) |

### 2. `users`

방에 참여한 '사용자' 정보를 저장합니다.

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PK, Auto-increment | 사용자의 고유 식별자 |
| `room_id` | BIGINT | FK (rooms.id) | 참여한 방의 ID |
| `user_name` | VARCHAR(255) | Not Null | 사용자 닉네임 |
| `joined_at` | TIMESTAMP | Not Null | 방에 참여한 시간 |
| `finished_at` | TIMESTAMP | | 사용자가 개인적으로 게임을 종료한 시간 |
| `total_soju_equivalent` | DOUBLE | Not Null | 해당 사용자의 총 소주 환산량 |
| `character_level` | INT | | 캐릭터 레벨 (0~4) |

### 3. `drink_records`

사용자가 '술을 마실 때마다'의 기록을 저장합니다.

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PK, Auto-increment | 음주 기록의 고유 식별자 |
| `user_id` | BIGINT | FK (users.id) | 기록을 남긴 사용자의 ID |
| `drink_type` | VARCHAR(255) | Not Null | 마신 술의 종류 (`SOJU`, `BEER` 등) |
| `glass_count` | INT | Not Null | 한 번에 마신 잔 수 |
| `soju_equivalent` | DOUBLE | Not Null | 마신 양을 소주 기준으로 환산한 값 |
| `recorded_at` | TIMESTAMP | Not Null | 기록이 등록된 시간 |

### 4. `reaction_tests`

사용자가 '반응 속도 게임'을 했을 때의 기록을 저장합니다.

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PK, Auto-increment | 반응 속도 테스트 기록의 고유 식별자 |
| `user_id` | BIGINT | FK (users.id) | 테스트를 진행한 사용자의 ID |
| `reaction_time_ms` | INT | Not Null | 반응하는 데 걸린 시간 (밀리초) |
| `tested_at` | TIMESTAMP | Not Null | 테스트를 진행한 시간 |
