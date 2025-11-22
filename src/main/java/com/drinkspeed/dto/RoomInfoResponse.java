package com.drinkspeed.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfoResponse {

    private Long roomId;

    private String roomCode;

    private String roomName;

    private LocalDateTime createdAt;

    private Boolean ended;

    private Integer participantCount;
}
