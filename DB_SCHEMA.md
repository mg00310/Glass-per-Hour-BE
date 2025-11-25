# Glass-per-Hour 데이터베이스 스키마

## 데이터베이스 개요

- **데이터베이스 이름**: `gph`
- **특징**: 사용자의 음주 관련 데이터를 기록하고, 실시간 랭킹을 제공하기 위한 데이터베이스입니다.

### 테이블 요약

| 테이블명          | 주요 역할                                       |
| ----------------- | ----------------------------------------------- |
| `rooms`           | 게임 방 정보를 저장합니다.                      |
| `users`           | 사용자 정보를 저장합니다.                       |
| `drink_records`   | 사용자의 음주 기록을 저장합니다.                |
| `reaction_tests`  | 사용자의 반응 속도 테스트 결과를 저장합니다.    |

### 관계 다이어그램

`rooms`와 `users`는 `room_code`를 통해 논리적으로 연결되며, `users`는 `drink_records` 및 `reaction_tests`와 직접적인 관계를 맺습니다.

```
[ rooms ] 1 -- (room_code로 연결) -- * [ users ]
                                          |
                                          +--* [ drink_records ]  (1:N)
                                          |
                                          +--* [ reaction_tests ] (1:N)
```

---

이 문서는 `gph` 데이터베이스의 테이블 구조를 상세히 설명합니다.

- **PRI**: Primary Key (기본 키), 각 행(row)의 고유 식별자입니다.
- **UNI**: Unique Key (고유 키), 중복될 수 없는 값입니다.
- **MUL**: Multiple Key (외래 키, Foreign Key), 다른 테이블의 행을 참조하는 인덱스입니다.

---

## 1. `users` 테이블

사용자 정보를 저장합니다.

| `user_name`           | varchar(255)  | NO   |     | NULL    |                | 사용자 이름                |
| `room_code`           | varchar(255)  | NO   |     | NULL    |                | 참가한 방의 코드           |
| `total_soju_equivalent` | double      | NO   |     | NULL    |                | 총 소주 환산량             |
| `character_level`     | int           | YES  |     | NULL    |                | 캐릭터 레벨 (0~4)          |
| `joined_at`           | datetime(6)   | NO   |     | NULL    |                | 방 참가 시간               |
| `finished_at`         | datetime(6)   | YES  |     | NULL    |                | 게임 종료 시간             |

---

## 2. `rooms` 테이블

게임 방 정보를 저장합니다.

| Field      | Type         | Null | Key | Default | Extra          | 설명                                   |
| ---------- | ------------ | ---- | --- | ------- | -------------- | -------------------------------------- |

| `room_code`| varchar(4)   | NO   | UNI | NULL    |                | 방 참가 코드 (중복 불가)               |
| `room_name`| varchar(255) | NO   |     | NULL    |                | 방 이름                                |
| `created_at` | datetime(6)  | NO   |     | NULL    |                | 방 생성 시간                           |
| `ended_at` | datetime(6)  | YES  |     | NULL    |                | 방 종료 시간                           |
| `status`   | int          | NO   |     | 0       |                | 방 상태 (0: 대기, 1: 진행, 2: 종료) |

---

## 3. `drink_records` 테이블

사용자의 음주 기록을 저장합니다. 한 명의 유저는 여러 개의 음주 기록을 가질 수 있습니다.

| Field           | Type                                                  | Null | Key | Default | Extra          | 설명                                                 |
| --------------- | ----------------------------------------------------- | ---- | --- | ------- | -------------- | ---------------------------------------------------- |
| `id`            | bigint                                                | NO   | PRI | NULL    | auto_increment | 음주 기록 ID (기본 키)                               |
| `drink_type`    | enum('SOJU','BEER','SOMAEK','MAKGEOLLI','FRUIT_SOJU') | NO   |     | NULL    |                | 주종                                                 |
| `glass_count`   | int                                                   | NO   |     | NULL    |                | 마신 잔 수                                           |
| `recorded_at`   | datetime(6)                                           | NO   |     | NULL    |                | 기록 시간                                            |
| `soju_equivalent` | double                                              | NO   |     | NULL    |                | 소주 환산량                                          |
| `user_id`       | bigint                                                | NO   | MUL | NULL    |                | `users` 테이블 ID (FK). 1:N 관계를 형성합니다. |

---

## 4. `reaction_tests` 테이블

사용자의 반응 속도 테스트 결과를 저장합니다. 한 명의 유저는 여러 개의 테스트 결과를 가질 수 있습니다.

| Field            | Type        | Null | Key | Default | Extra          | 설명                                                 |
| ---------------- | ----------- | ---- | --- | ------- | -------------- | ---------------------------------------------------- |
| `id`             | bigint      | NO   | PRI | NULL    | auto_increment | 테스트 결과 ID (기본 키)                             |
| `reaction_time_ms` | int         | NO   |     | NULL    |                | 반응 속도 (밀리초)                                   |
| `tested_at`      | datetime(6) | NO   |     | NULL    |                | 테스트 시간                                            |
| `user_id`        | bigint      | NO   | MUL | NULL    |                | `users` 테이블 ID (FK). 1:N 관계를 형성합니다. |