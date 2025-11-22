package com.drinkspeed.scheduler;

import com.drinkspeed.domain.Room;
import com.drinkspeed.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReactionGameScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReactionGameScheduler.class);

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${scheduler.reaction-game.enabled:true}")
    private boolean enabled;

    /**
     * 1시간마다 자동으로 순발력 게임 트리거
     * 모든 활성화된 방에 게임 시작 알림 전송
     */
    @Scheduled(fixedRateString = "${scheduler.reaction-game.interval-hours:1}000", initialDelay = 3600000)
    public void triggerReactionGame() {
        if (!enabled) {
            return;
        }

        logger.info("Triggering reaction game for all active rooms");

        List<Room> activeRooms = roomService.getActiveRooms();

        for (Room room : activeRooms) {
            try {
                // 방의 모든 참여자에게 게임 시작 알림
                ReactionGameNotification notification = ReactionGameNotification.builder()
                        .roomId(room.getId())
                        .roomName(room.getRoomName())
                        .message("⚡ 순발력 게임 시작! 빠르게 반응하세요!")
                        .build();

                messagingTemplate.convertAndSend(
                        "/topic/room/" + room.getId() + "/game/start",
                        notification);

                logger.info("Sent reaction game notification to room {}", room.getId());
            } catch (Exception e) {
                logger.error("Failed to send reaction game notification to room {}", room.getId(), e);
            }
        }

        logger.info("Reaction game triggered for {} rooms", activeRooms.size());
    }

    /**
     * 순발력 게임 알림 DTO
     */
    @lombok.Builder
    @lombok.Getter
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ReactionGameNotification {
        private Long roomId;
        private String roomName;
        private String message;
    }
}
