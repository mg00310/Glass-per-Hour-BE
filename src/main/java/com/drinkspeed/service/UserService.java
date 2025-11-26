package com.drinkspeed.service;

import com.drinkspeed.domain.User;
import com.drinkspeed.util.AlcoholCalculator;
import com.drinkspeed.util.RankingCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // In-memory data store
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    private final AlcoholCalculator alcoholCalculator;
    private final RankingCalculator rankingCalculator;
    private final GeminiService geminiService;

    /**
     * 사용자 생성
     */
    public User createUser(String userName) {
        long newId = idCounter.incrementAndGet();
        User user = User.builder()
                .id(newId)
                .userName(userName)
                .joinedAt(LocalDateTime.now())
                .build();
        userStore.put(newId, user);
        logger.info("Created new user: {} (ID: {})", userName, newId);
        return user;
    }

    /**
     * 잔 추가
     */
    public User addDrink(Long userId, String drinkType, int glassCount) {
        User user = findUserById(userId);

        double sojuEquivalent = alcoholCalculator.calculateSojuEquivalent(drinkType, glassCount);
        user.setTotalSojuEquivalent(user.getTotalSojuEquivalent() + sojuEquivalent);

        updateCharacterLevel(user);

        logger.info("User {} added {} glasses of {} (soju equiv: {})",
                user.getUserName(), glassCount, drinkType, sojuEquivalent);

        return user;
    }

    /**
     * 반응 속도 기록
     */
    public void recordReactionTest(Long userId, Integer reactionTimeMs) {
        User user = findUserById(userId);
        user.getReactionTimes().add(reactionTimeMs);
        logger.info("User {} recorded reaction time: {}ms", user.getUserName(), reactionTimeMs);
    }

    /**
     * 개인 타이머 종료
     */
    public User finishUser(Long userId) {
        User user = findUserById(userId);
        user.finish();
        updateCharacterLevel(user);
        logger.info("User {} finished drinking session", user.getUserName());
        // AI 메시지 생성은 비동기로 처리
        generateAndSaveAiMessage(userId);
        return user;
    }

    /**
     * 시속 잔 수 계산
     */
    public double calculateGlassPerHour(User user) {
        LocalDateTime startTime = user.getJoinedAt();
        LocalDateTime endTime = user.getFinishedAt() != null ? user.getFinishedAt() : LocalDateTime.now();

        Duration duration = Duration.between(startTime, endTime);
        double hours = duration.toMinutes() / 60.0;

        if (hours < 0.1) { // 최소 6분 (0.1시간)은 경과해야 의미있는 시속 계산
            hours = 0.1;
        }

        return alcoholCalculator.calculateGlassPerHour(user.getTotalSojuEquivalent(), hours);
    }

    /**
     * 캐릭터 레벨 업데이트
     */
    private void updateCharacterLevel(User user) {
        double glassPerHour = calculateGlassPerHour(user);
        Double avgReactionTime = user.getReactionTimes().stream()
                .mapToInt(val -> val)
                .average()
                .orElse(0.0);

        Integer characterLevel = rankingCalculator.determineCharacterLevel(glassPerHour, avgReactionTime);
        user.setCharacterLevel(characterLevel);
    }

    /**
     * 사용자 조회
     */
    public User findUserById(Long userId) {
        User user = userStore.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
        return user;
    }

    /**
     * 모든 사용자 랭킹 조회
     */
    public List<User> getRankings() {
        return userStore.values().stream()
                .sorted(Comparator.comparing(User::getTotalSojuEquivalent).reversed())
                .collect(Collectors.toList());
    }

    /**
     * AI 메시지 비동기 생성 및 저장
     */
    @Async
    public void generateAndSaveAiMessage(Long userId) {
        User user = findUserById(userId);
        if (user.getFinishedAt() == null) return;

        try {
            long durationSeconds = Duration.between(user.getJoinedAt(), user.getFinishedAt()).getSeconds();
            String message = geminiService.generateDrinkingResultMessage(
                    user.getUserName(),
                    (int) durationSeconds,
                    user.getTotalSojuEquivalent(),
                    user.getCharacterLevel() != null ? user.getCharacterLevel() : 0);

            user.setAiMessage(message);
            logger.info("Async AI message generated and saved for user: {}", user.getUserName());
        } catch (Exception e) {
            logger.error("Error generating AI message asynchronously", e);
            user.setAiMessage("AI 분석에 실패했습니다.");
        }
    }
}
