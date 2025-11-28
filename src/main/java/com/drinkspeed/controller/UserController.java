package com.drinkspeed.controller;

import com.drinkspeed.domain.User;
import com.drinkspeed.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- DTOs for Request Bodies ---
    @Data
    private static class CreateUserRequest {
        private String userName;
    }

    @Data
    private static class DrinkRequest {
        private String drinkType;
        private int glassCount;
    }

    @Data
    private static class ReactionRequest {
        private int reactionTimeMs;
    }

    /**
     * 사용자 생성
     * POST /api/users
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User newUser = userService.createUser(request.getUserName());
        return ResponseEntity.ok(newUser);
    }

    /**
     * 잔 추가
     * POST /api/users/{userId}/drinks
     */
    @PostMapping("/users/{userId}/drinks")
    public ResponseEntity<User> addDrink(
            @PathVariable Long userId,
            @RequestBody DrinkRequest request) {
        User updatedUser = userService.addDrink(userId, request.getDrinkType(), request.getGlassCount());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 반응 속도 기록
     * POST /api/users/{userId}/reaction
     */
    @PostMapping("/users/{userId}/reaction")
    public ResponseEntity<Void> recordReaction(
            @PathVariable Long userId,
            @RequestBody ReactionRequest request) {
        userService.recordReactionTest(userId, request.getReactionTimeMs());
        return ResponseEntity.ok().build();
    }

    /**
     * 개인 종료
     * POST /api/users/{userId}/finish
     */
    @PostMapping("/users/{userId}/finish")
    public ResponseEntity<User> finishUser(@PathVariable Long userId) {
        User user = userService.finishUser(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * AI 메시지 조회 (Polling 용)
     * GET /api/users/{userId}/ai-message
     */
    @GetMapping("/users/{userId}/ai-message")
    public ResponseEntity<Map<String, String>> getAiMessage(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        Map<String, String> response = new HashMap<>();
        response.put("aiMessage", user.getAiMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 조회 (공유 링크 용)
     * GET /api/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 전체 랭킹 조회
     * GET /api/rankings
     */
    @GetMapping("/rankings")
    public ResponseEntity<List<User>> getRankings() {
        List<User> rankings = userService.getRankings();
        return ResponseEntity.ok(rankings);
    }
}
