package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomResponse {

    

    private String roomCode;

    private String roomName;

    private Long hostUserId;

    private String hostUserName;
}
