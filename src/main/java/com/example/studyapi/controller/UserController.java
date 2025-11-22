package com.example.studyapi.controller;

import com.example.studyapi.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 사용자(User) 관리를 위한 REST API 컨트롤러
 * CRUD (Create, Read, Update, Delete) 기능을 제공합니다.
 */
@RestController // REST API 컨트롤러임을 나타냅니다.
@RequestMapping("/api/users") // 이 컨트롤러의 기본 경로는 "/api/users"입니다.
public class UserController {

    /**
     * 사용자 목록을 저장하는 리스트
     * 실제 프로젝트에서는 데이터베이스를 사용하지만, 학습용으로 메모리에 저장합니다.
     */
    private final List<User> users = new ArrayList<>();

    /**
     * 사용자 ID를 자동으로 증가시키기 위한 카운터
     * AtomicLong은 멀티스레드 환경에서도 안전하게 숫자를 증가시킵니다.
     */
    private final AtomicLong counter = new AtomicLong();

    /**
     * 생성자: 컨트롤러가 생성될 때 실행됩니다.
     * 테스트를 위해 초기 데이터를 하나 추가합니다.
     */
    public UserController() {
        // 초기 데이터 추가 (ID=1, 이름="Test User", 이메일="test@example.com")
        users.add(new User(counter.incrementAndGet(), "Test User", "test@example.com"));
    }

    /**
     * 모든 사용자 목록을 조회하는 API
     * 
     * 접속 방법: GET http://localhost:8080/api/users
     * 
     * @return 전체 사용자 리스트 (JSON 배열 형태로 반환)
     */
    @GetMapping // GET 요청을 처리합니다. 경로는 "/api/users"
    public List<User> getAllUsers() {
        // 현재 저장된 모든 사용자를 반환합니다.
        return users;
    }

    /**
     * 새로운 사용자를 생성하는 API
     * 
     * 접속 방법: POST http://localhost:8080/api/users
     * Body (JSON): { "name": "홍길동", "email": "hong@example.com" }
     * 
     * @param user 클라이언트가 보낸 사용자 정보 (JSON을 자동으로 User 객체로 변환)
     * @return 생성된 사용자 정보 (ID가 자동으로 부여됨)
     */
    @PostMapping // POST 요청을 처리합니다. 경로는 "/api/users"
    public User createUser(@RequestBody User user) { // @RequestBody: 요청 본문의 JSON을 User 객체로 변환
        // ID를 자동으로 증가시켜 부여합니다.
        user.setId(counter.incrementAndGet());

        // 리스트에 새 사용자를 추가합니다.
        users.add(user);

        // 생성된 사용자 정보를 반환합니다.
        return user;
    }

    /**
     * 특정 ID의 사용자를 조회하는 API
     * 
     * 접속 방법: GET http://localhost:8080/api/users/1
     * (마지막 숫자가 사용자 ID)
     * 
     * @param id 조회할 사용자의 ID (URL 경로에서 추출)
     * @return 해당 ID의 사용자 정보, 없으면 null
     */
    @GetMapping("/{id}") // GET 요청, 경로는 "/api/users/1" 같은 형태
    public User getUser(@PathVariable Long id) { // @PathVariable: URL의 {id} 부분을 파라미터로 받음
        // Stream API를 사용하여 ID가 일치하는 사용자를 찾습니다.
        return users.stream()
                .filter(u -> u.getId().equals(id)) // ID가 일치하는 사용자만 필터링
                .findFirst() // 첫 번째로 찾은 사용자를 반환
                .orElse(null); // 없으면 null 반환
    }

    /**
     * 특정 ID의 사용자를 삭제하는 API
     * 
     * 접속 방법: DELETE http://localhost:8080/api/users/1
     * (마지막 숫자가 삭제할 사용자 ID)
     * 
     * @param id 삭제할 사용자의 ID
     * @return 삭제 성공/실패 메시지
     */
    @DeleteMapping("/{id}") // DELETE 요청, 경로는 "/api/users/1" 같은 형태
    public String deleteUser(@PathVariable Long id) {
        // removeIf: 조건에 맞는 항목을 리스트에서 제거합니다.
        boolean removed = users.removeIf(u -> u.getId().equals(id));

        // 삭제 성공 여부에 따라 다른 메시지를 반환합니다.
        if (removed) {
            return "User deleted successfully";
        } else {
            return "User not found";
        }
    }
}
