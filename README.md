# 백엔드 API 학습 프로젝트

이 프로젝트는 백엔드 개발이 처음인 분들을 위해 구성된 간단한 Spring Boot API 프로젝트입니다.
사용자(User) 정보를 관리하는 간단한 API를 제공하며, Postman을 통해 테스트해볼 수 있습니다.

## 1. 프로젝트 실행 방법

이 프로젝트를 실행하기 위해서는 Java 17 이상이 필요합니다.

### 방법 A: IntelliJ IDEA 사용 (권장)
1. IntelliJ IDEA를 실행하고 `Open`을 클릭합니다.
2. `C:\Users\민규\studyAPI` 폴더를 선택하여 엽니다.
3. 프로젝트가 로드되면 `src/main/java/com/example/studyapi/StudyApiApplication.java` 파일을 엽니다.
4. 파일 내의 초록색 재생 버튼(▶)을 클릭하여 실행합니다.

### 방법 B: Maven 명령어로 실행 (Maven 설치 필요)
터미널(CMD 또는 PowerShell)에서 다음 명령어를 실행합니다:
```bash
mvn spring-boot:run
```

서버가 정상적으로 실행되면 다음과 같은 로그가 보입니다:
`Tomcat started on port 8080 (http) with context path ''`

## 2. Postman 설정 및 테스트

API가 정상적으로 작동하는지 확인하기 위해 Postman을 사용합니다.

### Postman 설치
아직 설치하지 않았다면 [Postman 공식 홈페이지](https://www.postman.com/downloads/)에서 다운로드하여 설치하세요.

### 컬렉션 Import (불러오기)
1. Postman을 실행합니다.
2. 좌측 상단의 **Import** 버튼을 클릭합니다.
3. 프로젝트 폴더(`C:\Users\민규\studyAPI`)에 있는 `postman_collection.json` 파일을 드래그하여 넣거나 선택합니다.
4. **Study API** 컬렉션이 생성된 것을 확인합니다.

### API 테스트 해보기
생성된 컬렉션 안에 있는 요청들을 하나씩 실행해 보세요.

1. **Hello World Check**: 서버가 켜져 있는지 확인합니다. "Hello, World!" 메시지가 오면 성공입니다.
2. **Get All Users**: 현재 저장된 모든 사용자 목록을 가져옵니다.
3. **Create User**: 새로운 사용자를 생성합니다. Body 탭에서 JSON 데이터를 수정하여 다른 이름으로도 만들어보세요.
4. **Get User By ID**: 특정 ID(예: 1)를 가진 사용자를 조회합니다.
5. **Delete User**: 특정 ID의 사용자를 삭제합니다.

## 3. 코드 살펴보기

- **StudyApiApplication.java**: 프로그램의 시작점입니다.
- **controller/HelloController.java**: 간단한 인사말을 반환하는 API입니다.
- **controller/UserController.java**: 사용자 정보를 생성, 조회, 삭제하는 실제 로직이 들어있습니다.
- **model/User.java**: 사용자 데이터의 형태(이름, 이메일 등)를 정의합니다.