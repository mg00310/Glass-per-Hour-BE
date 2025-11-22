package com.drinkspeed.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfoResponse {

    

    private String roomCode;

    private String roomName;

    private LocalDateTime createdAt;

    private Integer status;

    private Integer participantCount;
}
