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
     * 클라이언트 → /app/room/{roomId}/drink
     * 서버 → /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/drink")
    public void handleDrinkAdd(@DestinationVariable Long roomId, DrinkAddRequest request) {
        logger.info("Received drink add request for room {}: {}", roomId, request);

        // 잔 추가 처리
        DrinkAddResponse response = userService.addDrink(request);

        // 실시간 순위 업데이트
        List<RankingResponse> rankings = resultService.getRoomRanking(roomId);

        // 방의 모든 참여자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drink", response);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/ranking", rankings);

        logger.info("Broadcasted drink add and rankings to room {}", roomId);
    }

    /**
     * 반응 속도 테스트 이벤트 처리
     * 클라이언트 → /app/room/{roomId}/reaction
     * 서버 → /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/reaction")
    public void handleReactionTest(@DestinationVariable Long roomId, ReactionTestResultRequest request) {
        logger.info("Received reaction test for room {}: {}", roomId, request);

        // 반응 속도 기록
        userService.recordReactionTest(request.getUserId(), request.getReactionTimeMs());

        // 방의 모든 참여자에게 알림
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/reaction", request);

        logger.info("Broadcasted reaction test to room {}", roomId);
    }

    /**
     * 사용자 종료 이벤트 처리
     * 클라이언트 → /app/room/{roomId}/finish
     * 서버 → /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/finish")
    public void handleUserFinish(@DestinationVariable Long roomId, Long userId) {
        logger.info("Received finish request for user {} in room {}", userId, roomId);

        // 사용자 종료 처리
        userService.finishUser(userId);

        // 마지막 1명 남았는지 확인 및 자동 종료
        boolean autoEnded = roomService.checkAndAutoEndRoom(roomId);

        // 실시간 순위 업데이트
        List<RankingResponse> rankings = resultService.getRoomRanking(roomId);

        // 방의 모든 참여자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/finish", userId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/ranking", rankings);

        if (autoEnded) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/autoend", true);
            logger.info("Room {} auto-ended", roomId);
        }

        logger.info("Broadcasted finish event to room {}", roomId);
    }

    /**
     * 순위 요청 처리
     * 클라이언트 → /app/room/{roomId}/ranking/request
     * 서버 → /topic/room/{roomId}/ranking
     */
    @MessageMapping("/room/{roomId}/ranking/request")
    @SendTo("/topic/room/{roomId}/ranking")
    public List<RankingResponse> handleRankingRequest(@DestinationVariable Long roomId) {
        logger.info("Received ranking request for room {}", roomId);
        return resultService.getRoomRanking(roomId);
    }
}
