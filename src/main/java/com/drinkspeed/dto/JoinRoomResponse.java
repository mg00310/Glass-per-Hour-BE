package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRoomResponse {

    private Long userId;

    private String userName;

    private Long roomId;

    private String roomCode;

    private String roomName;

    private Boolean isHost;
}
