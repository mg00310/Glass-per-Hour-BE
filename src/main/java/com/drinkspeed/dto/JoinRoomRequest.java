package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRoomRequest {

    private String roomCode;

    private String userName;
}
