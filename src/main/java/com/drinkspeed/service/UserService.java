package com.drinkspeed.service;

import com.drinkspeed.domain.DrinkRecord;
import com.drinkspeed.domain.ReactionTest;
import com.drinkspeed.domain.User;
import com.drinkspeed.dto.DrinkAddRequest;
import com.drinkspeed.dto.DrinkAddResponse;
import com.drinkspeed.repository.DrinkRecordRepository;
import com.drinkspeed.repository.ReactionTestRepository;
import com.drinkspeed.repository.UserRepository;
import com.drinkspeed.util.AlcoholCalculator;
import com.drinkspeed.util.RankingCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

        private static final Logger logger = LoggerFactory.getLogger(UserService.class);

        private final UserRepository userRepository;
        private final DrinkRecordRepository drinkRecordRepository;
        private final ReactionTestRepository reactionTestRepository;
        private final AlcoholCalculator alcoholCalculator;
        private final RankingCalculator rankingCalculator;

        /**
         * 잔 추가
         */
        public DrinkAddResponse addDrink(DrinkAddRequest request) {
                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "사용자를 찾을 수 없습니다: " + request.getUserId()));

                // 소주 환산량 계산
                double sojuEquivalent = alcoholCalculator.calculateSojuEquivalent(
                                request.getDrinkType(),
                                request.getGlassCount());

                // 잔 기록 저장
                DrinkRecord record = DrinkRecord.builder()
                                .user(user)
                                .drinkType(request.getDrinkType())
                                .glassCount(request.getGlassCount())
                                .sojuEquivalent(sojuEquivalent)
                                .build();

                drinkRecordRepository.save(record);

                // 사용자의 총 소주 환산량 업데이트
                user.setTotalSojuEquivalent(user.getTotalSojuEquivalent() + sojuEquivalent);

                // 시속 잔 수 계산 및 캐릭터 레벨 업데이트
                double glassPerHour = calculateGlassPerHour(user);
                Double avgReactionTime = reactionTestRepository.findAvgReactionTimeByUserId(user.getId());
                updateCharacterLevel(user, glassPerHour, avgReactionTime);

                userRepository.save(user);

                logger.info("User {} added {} glasses of {} (soju equiv: {})",
                                user.getUserName(), request.getGlassCount(), request.getDrinkType(), sojuEquivalent);

                return DrinkAddResponse.builder()
                                .userId(user.getId())
                                .drinkType(request.getDrinkType())
                                .glassCount(request.getGlassCount())
                                .sojuEquivalent(sojuEquivalent)
                                .totalSojuEquivalent(user.getTotalSojuEquivalent())
                                .characterLevel(user.getCharacterLevel())
                                .build();
        }

        /**
         * 반응 속도 기록
         */
        public void recordReactionTest(Long userId, Integer reactionTimeMs) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

                ReactionTest test = ReactionTest.builder()
                                .user(user)
                                .reactionTimeMs(reactionTimeMs)
                                .build();

                reactionTestRepository.save(test);

                logger.info("User {} recorded reaction time: {}ms", user.getUserName(), reactionTimeMs);
        }

        /**
         * 개인 타이머 종료
         */
        public void finishUser(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

                user.finish();
                double glassPerHour = calculateGlassPerHour(user);
                Double avgReactionTime = reactionTestRepository.findAvgReactionTimeByUserId(user.getId());
                updateCharacterLevel(user, glassPerHour, avgReactionTime);

                userRepository.save(user);

                logger.info("User {} finished drinking session", user.getUserName());
        }

        /**
         * 시속 잔 수 계산 및 업데이트
         */
        public double calculateGlassPerHour(User user) {
                LocalDateTime startTime = user.getJoinedAt();
                LocalDateTime endTime = user.getFinishedAt() != null ? user.getFinishedAt() : LocalDateTime.now();

                Duration duration = Duration.between(startTime, endTime);
                double hours = duration.toMinutes() / 60.0;

                // 최소 6분 (0.1시간)은 경과해야 의미있는 시속 계산
                if (hours < 0.1) {
                        hours = 0.1;
                }

                return alcoholCalculator.calculateGlassPerHour(
                                user.getTotalSojuEquivalent(),
                                hours);
        }

        /**
         * 캐릭터 레벨 업데이트
         */
        private void updateCharacterLevel(User user, double glassPerHour, Double avgReactionTime) {
                Integer characterLevel = rankingCalculator.determineCharacterLevel(glassPerHour, avgReactionTime);
                user.setCharacterLevel(characterLevel);
        }

        /**
         * 사용자 조회
         */
        @Transactional(readOnly = true)
        public User getUser(Long userId) {
                return userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        }
}
