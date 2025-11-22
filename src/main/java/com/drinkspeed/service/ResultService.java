package com.drinkspeed.service;

import com.drinkspeed.domain.DrinkRecord;
import com.drinkspeed.domain.User;
import com.drinkspeed.dto.RankingResponse;
import com.drinkspeed.dto.UserResultResponse;
import com.drinkspeed.repository.DrinkRecordRepository;
import com.drinkspeed.repository.ReactionTestRepository;
import com.drinkspeed.repository.UserRepository;
import com.drinkspeed.util.RankingCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

    private final UserRepository userRepository;
    private final DrinkRecordRepository drinkRecordRepository;
    private final ReactionTestRepository reactionTestRepository;
    private final RankingCalculator rankingCalculator;

    /**
     * 방 전체 순위 조회
     */
    public List<RankingResponse> getRoomRanking(Long roomId) {
        List<User> users = userRepository.findByRoomId(roomId);

        // 최종 점수 계산 및 정렬
        List<UserWithScore> usersWithScores = users.stream()
                .map(user -> {
                    Double avgReactionTime = reactionTestRepository.findAvgReactionTimeByUserId(user.getId());
                    double finalScore = rankingCalculator.calculateFinalScore(
                            user.getTotalSojuEquivalent(),
                            avgReactionTime);
                    return new UserWithScore(user, finalScore);
                })
                .sorted(Comparator.comparingDouble(UserWithScore::score).reversed())
                .toList();

        // 순위 응답 생성
        List<RankingResponse> rankings = new ArrayList<>();
        for (int i = 0; i < usersWithScores.size(); i++) {
            User user = usersWithScores.get(i).user();
            int rank = i + 1;

            // 사용자의 순위 업데이트
            user.setFinalRank(rank);

            rankings.add(RankingResponse.builder()
                    .userId(user.getId())
                    .userName(user.getUserName())
                    .rank(rank)
                    .glassPerHour(user.getGlassPerHour())
                    .totalSojuEquivalent(user.getTotalSojuEquivalent())
                    .characterLevel(user.getCharacterLevel())
                    .isFinished(user.isFinished())
                    .build());
        }

        logger.info("Calculated rankings for room {}: {} users", roomId, rankings.size());

        return rankings;
    }

    /**
     * 개인 결과 조회
     */
    public UserResultResponse getUserResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 주종별 마신 잔 수 집계
        List<DrinkRecord> records = drinkRecordRepository.findByUserId(userId);
        Map<DrinkRecord.DrinkType, Integer> totalDrinks = records.stream()
                .collect(Collectors.groupingBy(
                        DrinkRecord::getDrinkType,
                        Collectors.summingInt(DrinkRecord::getGlassCount)));

        // 평균 반응 속도
        Double avgReactionTime = reactionTestRepository.findAvgReactionTimeByUserId(userId);

        // 순위 조회 (없으면 계산)
        Integer rank = user.getFinalRank();
        if (rank == null) {
            List<RankingResponse> rankings = getRoomRanking(user.getRoom().getId());
            rank = rankings.stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .findFirst()
                    .map(RankingResponse::getRank)
                    .orElse(0);
        }

        // AI 설명 생성
        String funnyDescription = user.getFunnyDescription();
        if (funnyDescription == null || funnyDescription.isEmpty()) {
            funnyDescription = rankingCalculator.generateFunnyDescription(user, rank);
            user.setFunnyDescription(funnyDescription);
            userRepository.save(user);
        }

        return UserResultResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .characterLevel(user.getCharacterLevel())
                .glassPerHour(user.getGlassPerHour())
                .totalDrinks(totalDrinks)
                .funnyDescription(funnyDescription)
                .rank(rank)
                .totalSojuEquivalent(user.getTotalSojuEquivalent())
                .averageReactionTime(avgReactionTime)
                .build();
    }

    /**
     * 내부 레코드 클래스: 사용자와 점수를 함께 저장
     */
    private record UserWithScore(User user, double score) {
    }
}
