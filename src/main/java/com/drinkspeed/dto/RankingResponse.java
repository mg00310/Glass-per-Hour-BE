package com.drinkspeed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingResponse {

    private Long userId;

    private String userName;

    private Integer rank;

    private Double glassPerHour;

    private Double totalSojuEquivalent;

    private Integer characterLevel;

    private Boolean isFinished;
}
