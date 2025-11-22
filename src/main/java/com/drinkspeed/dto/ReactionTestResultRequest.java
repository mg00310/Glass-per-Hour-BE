package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionTestResultRequest {

    private Long userId;

    private Integer reactionTimeMs;
}
