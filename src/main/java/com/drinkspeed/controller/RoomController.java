package com.drinkspeed.controller;

import com.drinkspeed.dto.*;
import com.drinkspeed.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 방 생성
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 방 참여
     * POST /api/rooms/join
     */
    @PostMapping("/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(@RequestBody JoinRoomRequest request) {
        JoinRoomResponse response = roomService.joinRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 방 종료
     * POST /api/rooms/{roomCode}/end
     */
    @PostMapping("/{roomCode}/end")
    public ResponseEntity<Void> endRoom(@PathVariable String roomCode) {
        roomService.endRoom(roomCode);
        return ResponseEntity.ok().build();
    }

    /**
     * 방 정보 조회
     * GET /api/rooms/{roomCode}
     */
    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(@PathVariable String roomCode) {
        RoomInfoResponse response = roomService.getRoomInfo(roomCode);
        return ResponseEntity.ok(response);
    }
}
