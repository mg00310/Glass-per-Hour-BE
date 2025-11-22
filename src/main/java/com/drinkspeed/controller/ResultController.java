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
     * GET /api/results/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResultResponse> getUserResult(@PathVariable Long userId) {
        UserResultResponse response = resultService.getUserResult(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 방 순위 조회
     * GET /api/results/room/{roomId}/ranking
     */
    @GetMapping("/room/{roomId}/ranking")
    public ResponseEntity<List<RankingResponse>> getRoomRanking(@PathVariable Long roomId) {
        List<RankingResponse> rankings = resultService.getRoomRanking(roomId);
        return ResponseEntity.ok(rankings);
    }
}
