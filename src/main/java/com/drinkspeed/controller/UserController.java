package com.drinkspeed.controller;

import com.drinkspeed.dto.DrinkAddRequest;
import com.drinkspeed.dto.DrinkAddResponse;
import com.drinkspeed.dto.ReactionTestResultRequest;
import com.drinkspeed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> finishUser(@PathVariable Long userId) {
        userService.finishUser(userId);
        return ResponseEntity.ok().build();
    }
}
