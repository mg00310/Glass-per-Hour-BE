package com.drinkspeed.util;

import com.drinkspeed.domain.DrinkRecord;
import org.springframework.stereotype.Component;

@Component
public class AlcoholCalculator {

    /**
     * 술 종류별 소주 환산 비율
     * 소주 1잔(50ml, 도수 17%) 기준
     */
    private static final double SOJU_RATE = 1.0;
    private static final double BEER_RATE = 0.3; // 맥주 1잔(500ml, 도수 4.5%)
    private static final double SOMAEK_RATE = 0.65; // 소맥 1잔
    private static final double MAKGEOLLI_RATE = 0.4; // 막걸리 1잔(300ml, 도수 6%)
    private static final double FRUIT_SOJU_RATE = 0.7; // 과일소주 1잔(도수 13%)

    /**
     * 술 종류와 잔 수를 기반으로 소주 환산량 계산
     * 
     * @param drinkType  술 종류
     * @param glassCount 잔 수
     * @return 소주 환산량
     */
    public double calculateSojuEquivalent(DrinkRecord.DrinkType drinkType, int glassCount) {
        double rate = switch (drinkType) {
            case SOJU -> SOJU_RATE;
            case BEER -> BEER_RATE;
            case SOMAEK -> SOMAEK_RATE;
            case MAKGEOLLI -> MAKGEOLLI_RATE;
            case FRUIT_SOJU -> FRUIT_SOJU_RATE;
        };

        return rate * glassCount;
    }

    /**
     * 시간당 잔 수 계산 (소주 환산 기준)
     * 
     * @param totalSojuEquivalent 총 소주 환산량
     * @param durationHours       지속 시간 (시간)
     * @return 시속 잔 수
     */
    public double calculateGlassPerHour(double totalSojuEquivalent, double durationHours) {
        if (durationHours <= 0) {
            return 0.0;
        }
        return totalSojuEquivalent / durationHours;
    }
}
