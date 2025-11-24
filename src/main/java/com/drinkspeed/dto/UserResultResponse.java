package com.drinkspeed.dto;

import com.drinkspeed.domain.DrinkRecord;
import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResultResponse {

    private Long userId;

    private String userName;

    private Integer characterLevel;

    private Double glassPerHour;

    private Map<DrinkRecord.DrinkType, Integer> totalDrinks;

    private String funnyDescription;

    private Integer rank;

    private Double totalSojuEquivalent;

    private Double averageReactionTime;
}
