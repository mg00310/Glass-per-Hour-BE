package com.drinkspeed.util;

import com.drinkspeed.domain.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RankingCalculator {

    private static final Logger logger = LoggerFactory.getLogger(RankingCalculator.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final OkHttpClient client;
    private final Gson gson;

    public RankingCalculator() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * 사용자 캐릭터 레벨 결정
     * 
     * @param glassPerHour 시속 잔 수
     * @return 캐릭터 레벨 (술고래, 주당, 알쓰, 술 취한 다람쥐)
     */
    public String determineCharacterLevel(double glassPerHour) {
        if (glassPerHour >= 3.0) {
            return "술고래 🐋";
        } else if (glassPerHour >= 2.0) {
            return "주당 🍺";
        } else if (glassPerHour >= 1.0) {
            return "알쓰 🥴";
        } else {
            return "술 취한 다람쥐 🐿️";
        }
    }

    /**
     * 최종 점수 계산
     * (총 소주 환산량 × 0.7) + (반응속도 점수 × 0.3)
     * 
     * @param totalSojuEquivalent 총 소주 환산량
     * @param avgReactionTime     평균 반응 속도 (ms)
     * @return 최종 점수
     */
    public double calculateFinalScore(double totalSojuEquivalent, Double avgReactionTime) {
        double drinkScore = totalSojuEquivalent * 0.7;

        // 반응속도 점수: 빠를수록 높은 점수
        double reactionScore = 0.0;
        if (avgReactionTime != null && avgReactionTime > 0) {
            // 500ms 이하면 만점(10점), 2000ms 이상이면 0점
            // 점수 = max(0, 10 - (반응시간 - 500) / 150)
            reactionScore = Math.max(0, 10 - (avgReactionTime - 500) / 150);
        }

        return drinkScore + (reactionScore * 0.3);
    }

    /**
     * Gemini API를 사용하여 재미있는 결과 설명 생성
     * 
     * @param user 사용자 정보
     * @param rank 순위
     * @return AI가 생성한 재미있는 설명
     */
    public String generateFunnyDescription(User user, int rank) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-api-key-here")) {
            logger.warn("Gemini API key not configured. Using fallback description.");
            return generateFallbackDescription(user, rank);
        }

        try {
            String prompt = String.format(
                    "사용자 '%s'님의 술자리 결과를 재미있게 요약해줘. " +
                            "순위: %d등, 시속 잔: %.1f잔, 캐릭터: %s, 총 소주 환산: %.1f잔. " +
                            "2-3문장으로 유머러스하게 작성해줘. 술자리 분위기에 맞게!",
                    user.getUserName(),
                    rank,
                    user.getGlassPerHour() != null ? user.getGlassPerHour() : 0.0,
                    user.getCharacterLevel() != null ? user.getCharacterLevel() : "알쓰",
                    user.getTotalSojuEquivalent());

            String requestBody = buildGeminiRequest(prompt);
            Request request = new Request.Builder()
                    .url(apiUrl + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    String description = parseGeminiResponse(responseBody);

                    if (description != null && !description.isEmpty()) {
                        logger.info("Generated description via Gemini for user: {}", user.getUserName());
                        return description.trim();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate description via Gemini API", e);
        }

        return generateFallbackDescription(user, rank);
    }

    /**
     * Gemini API 요청 본문 생성
     */
    private String buildGeminiRequest(String prompt) {
        JsonObject request = new JsonObject();

        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();

        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);

        content.add("parts", parts);
        contents.add(content);

        request.add("contents", contents);

        return gson.toJson(request);
    }

    /**
     * Gemini API 응답 파싱
     */
    private String parseGeminiResponse(String responseBody) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("candidates")) {
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = candidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");

                    if (parts.size() > 0) {
                        JsonObject part = parts.get(0).getAsJsonObject();
                        return part.get("text").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse Gemini response", e);
        }

        return null;
    }

    /**
     * API 호출 실패 시 폴백 설명 생성
     */
    private String generateFallbackDescription(User user, int rank) {
        String characterLevel = user.getCharacterLevel() != null ? user.getCharacterLevel() : "알쓰";
        double glassPerHour = user.getGlassPerHour() != null ? user.getGlassPerHour() : 0.0;

        if (rank == 1) {
            return String.format("%s님, 오늘의 진정한 술고래! 시속 %.1f잔의 전설적인 페이스를 기록하셨습니다. 내일 간 건강 챙기세요! 🏆",
                    user.getUserName(), glassPerHour);
        } else if (characterLevel.contains("술고래")) {
            return String.format("%s님은 %d등으로 훌륭한 주량을 보여주셨어요! 시속 %.1f잔, 역시 술고래답습니다! 🐋",
                    user.getUserName(), rank, glassPerHour);
        } else if (characterLevel.contains("주당")) {
            return String.format("%s님, %d등 달성! 시속 %.1f잔으로 주당의 면모를 유감없이 발휘하셨습니다. 👍",
                    user.getUserName(), rank, glassPerHour);
        } else {
            return String.format("%s님, %d등으로 완주하셨어요! 시속 %.1f잔으로 건강하게 즐기셨네요. 이 페이스가 좋습니다! 😊",
                    user.getUserName(), rank, glassPerHour);
        }
    }
}
