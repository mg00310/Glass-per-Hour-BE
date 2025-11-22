package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequest {

    private String roomName; // null이면 AI가 생성

    private String hostName; // 방장 이름
}
