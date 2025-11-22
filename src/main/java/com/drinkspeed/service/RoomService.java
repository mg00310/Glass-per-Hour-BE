package com.drinkspeed.service;

import com.drinkspeed.domain.Room;
import com.drinkspeed.domain.User;
import com.drinkspeed.dto.CreateRoomRequest;
import com.drinkspeed.dto.CreateRoomResponse;
import com.drinkspeed.dto.JoinRoomRequest;
import com.drinkspeed.dto.JoinRoomResponse;
import com.drinkspeed.dto.RoomInfoResponse;
import com.drinkspeed.repository.RoomRepository;
import com.drinkspeed.repository.UserRepository;
import com.drinkspeed.util.RoomNameGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private static final SecureRandom random = new SecureRandom();

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomNameGenerator roomNameGenerator;

    /**
     * 새로운 방 생성
     * - 4자리 중복되지 않는 방 코드 생성
     * - 방 이름이 없으면 AI가 생성
     */
    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        // 4자리 방 코드 생성
        String roomCode = generateUniqueRoomCode();

        // 방 이름 결정 (null이면 AI 생성)
        String roomName = (request.getRoomName() == null || request.getRoomName().isBlank())
                ? roomNameGenerator.generateRoomName()
                : request.getRoomName();

        // 방 생성
        Room room = Room.builder()
                .roomCode(roomCode)
                .roomName(roomName)
                .status(0)
                .build();

        room = roomRepository.save(room);
        logger.info("Created room: {} ({})", roomName, roomCode);

        // 방장 사용자 생성
        User host = User.builder()
                .userName(request.getHostName())
                .roomCode(roomCode)
                .totalSojuEquivalent(0.0)
                .build();

        host = userRepository.save(host);
        logger.info("Created host user: {} in room {}", request.getHostName(), roomCode);

        return CreateRoomResponse.builder()

                .roomCode(roomCode)
                .roomName(roomName)
                .hostUserId(host.getId())
                .hostUserName(host.getUserName())
                .build();
    }

    /**
     * 방 참여
     */
    public JoinRoomResponse joinRoom(JoinRoomRequest request) {
        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다: " + request.getRoomCode()));

        if (room.isEnded()) {
            throw new IllegalStateException("이미 종료된 방입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .userName(request.getUserName())
                .roomCode(room.getRoomCode())
                .totalSojuEquivalent(0.0)
                .build();

        user = userRepository.save(user);
        logger.info("User {} joined room {}", request.getUserName(), request.getRoomCode());

        return JoinRoomResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())

                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                
                .build();
    }

    /**
     * 방 종료
     */
    public void endRoom(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다: " + roomCode));

        room.endRoom();
        roomRepository.save(room);
        logger.info("Room with code {} ended", roomCode);
    }

    /**
     * 마지막 한 명 남았는지 확인 후 자동 종료
     */
    public boolean checkAndAutoEndRoom(String roomCode) {
        long activeUsers = userRepository.countActiveUsersByRoomCode(roomCode);

        if (activeUsers <= 1) {
            endRoom(roomCode);
            logger.info("Auto-ended room with code {} (only 1 or 0 active users remaining)", roomCode);
            return true;
        }

        return false;
    }

    /**
     * 방 정보 조회
     */
    @Transactional(readOnly = true)
    public RoomInfoResponse getRoomInfo(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다: " + roomCode));

        List<User> users = userRepository.findByRoomCode(roomCode);

        return RoomInfoResponse.builder()

                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                .createdAt(room.getCreatedAt())
                .status(room.getStatus())
                .participantCount(users.size())
                .build();
    }

    /**
     * 활성화된 모든 방 조회
     */
    @Transactional(readOnly = true)
    public List<Room> getActiveRooms() {
        return roomRepository.findByStatusNot(2); // 2: ENDED
    }

    /**
     * 중복되지 않는 4자리 방 코드 생성
     */
    private String generateUniqueRoomCode() {
        String roomCode;
        int attempts = 0;

        do {
            roomCode = String.format("%04d", random.nextInt(10000));
            attempts++;

            if (attempts > 100) {
                throw new RuntimeException("방 코드 생성 실패 (너무 많은 시도)");
            }
        } while (roomRepository.existsByRoomCode(roomCode));

        return roomCode;
    }
}
