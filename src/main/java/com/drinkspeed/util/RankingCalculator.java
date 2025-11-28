package com.drinkspeed.util;

import com.drinkspeed.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RankingCalculator {

    private static final Logger logger = LoggerFactory.getLogger(RankingCalculator.class);

    public RankingCalculator() {
    }

    /**
     * AI를 사용하여 사용자 캐릭터 레벨 결정
     */
    /**
     * 사용자 캐릭터 레벨 결정 (AI 사용 제거 - Deadlock 방지)
     */
    public Integer determineCharacterLevel(double glassPerHour, Double avgReactionTime) {
        return getFallbackCharacterLevel(glassPerHour);
    }

    private Integer getFallbackCharacterLevel(double glassPerHour) {
        // 시속 잔수 기준
        if (glassPerHour > 8.0) // 8잔/시간 초과
            return 4;
        if (glassPerHour > 6.0) // 6잔/시간 초과 ~ 8잔/시간 이하
            return 3;
        if (glassPerHour > 4.0) // 4잔/시간 초과 ~ 6잔/시간 이하
            return 2;
        if (glassPerHour > 2.0) // 2잔/시간 초과 ~ 4잔/시간 이하
            return 1;
        return 0; // 2잔/시간 이하
    }

    /**
     * 최종 점수 계산
     */
    public double calculateFinalScore(double totalSojuEquivalent, Double avgReactionTime) {
        double drinkScore = totalSojuEquivalent * 0.7;
        double reactionScore = 0.0;
        if (avgReactionTime != null && avgReactionTime > 0) {
            reactionScore = Math.max(0, 10 - (avgReactionTime - 500) / 150);
        }
        return drinkScore + (reactionScore * 0.3);
    }

    /**
     * 재미있는 결과 설명 생성 (AI 사용 제거 - Deadlock 방지)
     */
    public String generateFunnyDescription(User user, int rank, double glassPerHour) {
        return generateFallbackDescription(user, rank, glassPerHour);
    }

    private String generateFallbackDescription(User user, int rank, double glassPerHour) {
        return String.format("수고하셨습니다! %s님은 %d등입니다. (시속 %.1f잔)", user.getUserName(), rank, glassPerHour);
    }
}
