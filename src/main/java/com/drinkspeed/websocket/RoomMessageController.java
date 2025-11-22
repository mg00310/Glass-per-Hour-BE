package com.drinkspeed.websocket;

import com.drinkspeed.dto.DrinkAddRequest;
import com.drinkspeed.dto.DrinkAddResponse;
import com.drinkspeed.dto.RankingResponse;
import com.drinkspeed.dto.ReactionTestResultRequest;
import com.drinkspeed.service.ResultService;
import com.drinkspeed.service.RoomService;
import com.drinkspeed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoomMessageController {

    private static final Logger logger = LoggerFactory.getLogger(RoomMessageController.class);

    private final UserService userService;
    private final RoomService roomService;
    private final ResultService resultService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 잔 추가 이벤트 처리
     * 클라이언트 → /app/room/{roomCode}/drink
     * 서버 → /topic/room/{roomCode}
     */
    @MessageMapping("/room/{roomCode}/drink")
    public void handleDrinkAdd(@DestinationVariable String roomCode, DrinkAddRequest request) {
        logger.info("Received drink add request for room {}: {}", roomCode, request);

        // 잔 추가 처리
        DrinkAddResponse response = userService.addDrink(request);

        // 실시간 순위 업데이트
        List<RankingResponse> rankings = resultService.getRoomRanking(roomCode);

        // 방의 모든 참여자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drink", response);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ranking", rankings);

        logger.info("Broadcasted drink add and rankings to room {}", roomCode);
    }

    /**
     * 반응 속도 테스트 이벤트 처리
     * 클라이언트 → /app/room/{roomCode}/reaction
     * 서버 → /topic/room/{roomCode}
     */
    @MessageMapping("/room/{roomCode}/reaction")
    public void handleReactionTest(@DestinationVariable String roomCode, ReactionTestResultRequest request) {
        logger.info("Received reaction test for room {}: {}", roomCode, request);

        // 반응 속도 기록
        userService.recordReactionTest(request.getUserId(), request.getReactionTimeMs());

        // 방의 모든 참여자에게 알림
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/reaction", request);

        logger.info("Broadcasted reaction test to room {}", roomCode);
    }

    /**
     * 사용자 종료 이벤트 처리
     * 클라이언트 → /app/room/{roomCode}/finish
     * 서버 → /topic/room/{roomCode}
     */
    @MessageMapping("/room/{roomCode}/finish")
    public void handleUserFinish(@DestinationVariable String roomCode, Long userId) {
        logger.info("Received finish request for user {} in room {}", userId, roomCode);

        // 사용자 종료 처리
        userService.finishUser(userId);

        // 마지막 1명 남았는지 확인 및 자동 종료
        boolean autoEnded = roomService.checkAndAutoEndRoom(roomCode);

        // 실시간 순위 업데이트
        List<RankingResponse> rankings = resultService.getRoomRanking(roomCode);

        // 방의 모든 참여자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/finish", userId);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ranking", rankings);

        if (autoEnded) {
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/autoend", true);
            logger.info("Room {} auto-ended", roomCode);
        }

        logger.info("Broadcasted finish event to room {}", roomCode);
    }

    /**
     * 순위 요청 처리
     * 클라이언트 → /app/room/{roomCode}/ranking/request
     * 서버 → /topic/room/{roomCode}/ranking
     */
    @MessageMapping("/room/{roomCode}/ranking/request")
    @SendTo("/topic/room/{roomCode}/ranking")
    public List<RankingResponse> handleRankingRequest(@DestinationVariable String roomCode) {
        logger.info("Received ranking request for room {}", roomCode);
        return resultService.getRoomRanking(roomCode);
    }
}