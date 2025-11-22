# Glass-per-Hour 데이터베이스 스키마

이 문서는 `gph` 데이터베이스의 테이블 구조를 설명합니다.

- **PRI**: Primary Key (기본 키), 각 행(row)의 고유 식별자입니다.
- **UNI**: Unique Key (고유 키), 중복될 수 없는 값입니다.
- **MUL**: Multiple Key (외래 키, Foreign Key), 다른 테이블의 행을 참조하는 인덱스입니다.

---

## 1. `users` 테이블

사용자 정보를 저장합니다.

| Field                 | Type          | Null | Key | Default | Extra          | 설명                       |
| --------------------- | ------------- | ---- | --- | ------- | -------------- | -------------------------- |
| `id`                  | bigint        | NO   | PRI | NULL    | auto_increment | 사용자 고유 ID (기본 키)   |
| `character_level`     | varchar(255)  | YES  |     | NULL    |                | 캐릭터 레벨                |
| `final_rank`          | int           | YES  |     | NULL    |                | 최종 랭킹                  |
| `finished_at`         | datetime(6)   | YES  |     | NULL    |                | 게임 종료 시간             |
| `funny_description`   | varchar(1000) | YES  |     | NULL    |                | 웃긴 설명 (캐릭터)         |
| `glass_per_hour`      | double        | YES  |     | NULL    |                | 시간당 마신 잔 수          |
| `is_host`             | bit(1)        | NO   |     | NULL    |                | 방장 여부 (1=true, 0=false) |
| `joined_at`           | datetime(6)   | NO   |     | NULL    |                | 방 참가 시간               |
| `total_soju_equivalent` | double      | NO   |     | NULL    |                | 총 소주 환산량             |
| `user_name`           | varchar(255)  | NO   |     | NULL    |                | 사용자 이름                |
| `room_id`             | bigint        | NO   | MUL | NULL    |                | `rooms` 테이블의 ID (외래 키) |

---

## 2. `rooms` 테이블

게임 방 정보를 저장합니다.

| Field      | Type         | Null | Key | Default | Extra          | 설명                       |
| ---------- | ------------ | ---- | --- | ------- | -------------- | -------------------------- |
| `id`       | bigint       | NO   | PRI | NULL    | auto_increment | 방 고유 ID (기본 키)       |
| `created_at` | datetime(6)  | NO   |     | NULL    |                | 방 생성 시간               |
| `ended`    | bit(1)       | NO   |     | NULL    |                | 방 종료 여부 (1=true, 0=false) |
| `ended_at` | datetime(6)  | YES  |     | NULL    |                | 방 종료 시간               |
| `room_code`| varchar(4)   | NO   | UNI | NULL    |                | 방 참가 코드 (중복 불가)   |
| `room_name`| varchar(255) | NO   |     | NULL    |                | 방 이름                    |

---

## 3. `drink_records` 테이블

사용자의 음주 기록을 저장합니다.

| Field           | Type                                                  | Null | Key | Default | Extra          | 설명                       |
| --------------- | ----------------------------------------------------- | ---- | --- | ------- | -------------- | -------------------------- |
| `id`            | bigint                                                | NO   | PRI | NULL    | auto_increment | 음주 기록 ID (기본 키)     |
| `drink_type`    | enum('SOJU','BEER','SOMAEK','MAKGEOLLI','FRUIT_SOJU') | NO   |     | NULL    |                | 주종                       |
| `glass_count`   | int                                                   | NO   |     | NULL    |                | 마신 잔 수                 |
| `recorded_at`   | datetime(6)                                           | NO   |     | NULL    |                | 기록 시간                  |
| `soju_equivalent` | double                                              | NO   |     | NULL    |                | 소주 환산량                |
| `user_id`       | bigint                                                | NO   | MUL | NULL    |                | `users` 테이블의 ID (외래 키) |

---

## 4. `reaction_tests` 테이블

사용자의 반응 속도 테스트 결과를 저장합니다.

| Field            | Type        | Null | Key | Default | Extra          | 설명                       |
| ---------------- | ----------- | ---- | --- | ------- | -------------- | -------------------------- |
| `id`             | bigint      | NO   | PRI | NULL    | auto_increment | 테스트 결과 ID (기본 키)   |
| `reaction_time_ms` | int         | NO   |     | NULL    |                | 반응 속도 (밀리초)         |
| `tested_at`      | datetime(6) | NO   |     | NULL    |                | 테스트 시간                |
| `user_id`        | bigint      | NO   | MUL | NULL    |                | `users` 테이블의 ID (외래 키) |
