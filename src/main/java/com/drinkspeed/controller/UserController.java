package com.drinkspeed.controller;

import com.drinkspeed.dto.DrinkAddRequest;
import com.drinkspeed.dto.DrinkAddResponse;
import com.drinkspeed.dto.ReactionTestResultRequest;
import com.drinkspeed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.drinkspeed.domain.User;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 잔 추가
     * POST /api/users/{userId}/drinks
     */
    @PostMapping("/{userId}/drinks")
    public ResponseEntity<DrinkAddResponse> addDrink(
            @PathVariable Long userId,
            @RequestBody DrinkAddRequest request) {

        request.setUserId(userId);
        DrinkAddResponse response = userService.addDrink(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 반응 속도 기록
     * POST /api/users/{userId}/reaction
     */
    @PostMapping("/{userId}/reaction")
    public ResponseEntity<Void> recordReaction(
            @PathVariable Long userId,
            @RequestBody ReactionTestResultRequest request) {

        userService.recordReactionTest(userId, request.getReactionTimeMs());
        return ResponseEntity.ok().build();
    }

    /**
     * 개인 종료
     * POST /api/users/{userId}/finish
     */
    @PostMapping("/{userId}/finish")
    public ResponseEntity<User> finishUser(@PathVariable Long userId) {
        // 1. 트랜잭션 내에서 DB 업데이트 (User 반환)
        User user = userService.finishUser(userId);

        // 2. 비동기로 AI 메시지 생성 요청 (결과 기다리지 않음)
        userService.generateAndSaveAiMessage(userId);

        // 3. 즉시 응답 반환
        return ResponseEntity.ok(user);
    }

    /**
     * AI 메시지 조회 (Polling 용)
     * GET /api/users/{userId}/ai-message
     */
    @GetMapping("/{userId}/ai-message")
    public ResponseEntity<Map<String, String>> getAiMessage(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("aiMessage", user.getAiMessage());
        return ResponseEntity.ok(response);
    }
}
