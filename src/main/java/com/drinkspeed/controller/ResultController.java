package com.drinkspeed.controller;

import com.drinkspeed.dto.RankingResponse;
import com.drinkspeed.dto.UserResultResponse;
import com.drinkspeed.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    /**
     * 개인 결과 조회
     * GET /api/results/room/{roomCode}/user/{userId}
     */
    @GetMapping("/room/{roomCode}/user/{userId}")
    public ResponseEntity<UserResultResponse> getUserResult(@PathVariable String roomCode, @PathVariable Long userId) {
        UserResultResponse response = resultService.getUserResult(roomCode, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 방 순위 조회
     * GET /api/results/room/{roomCode}/ranking
     */
    @GetMapping("/room/{roomCode}/ranking")
    public ResponseEntity<List<RankingResponse>> getRoomRanking(@PathVariable String roomCode) {
        List<RankingResponse> rankings = resultService.getRoomRanking(roomCode);
        return ResponseEntity.ok(rankings);
    }
}