package com.drinkspeed.dto;

import com.drinkspeed.domain.DrinkRecord;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkAddRequest {

    private Long userId;

    private DrinkRecord.DrinkType drinkType;

    private Integer glassCount;
}
