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

import java.util.Arrays;
import java.util.List;
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
     * AI를 사용하여 사용자 캐릭터 레벨 결정
     */
    public Integer determineCharacterLevel(double glassPerHour, Double avgReactionTime) {
        final List<String> levels = Arrays.asList(
                "일청담 다이버", // 0
                "술 취한 다람쥐", // 1
                "지갑은 지킨다", // 2
                "술고래 후보생", // 3
                "인간 알코올" // 4
        );

        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-api-key-here")) {
            logger.warn("Gemini API key not configured. Using fallback for character level.");
            return getFallbackCharacterLevel(glassPerHour);
        }

        try {
            String prompt = String.format(
                    "사용자의 시속 주량은 %.1f잔이고, 평균 반응 속도는 %.0fms입니다. " +
                            "이 두 가지 데이터를 바탕으로 다음 5개의 레벨 중 가장 적합한 레벨 하나만 골라줘. " +
                            "레벨 목록: [\"일청담 다이버\", \"술 취한 다람쥐\", \"지갑은 지킨다\", \"술고래 후보생\", \"인간 알코올\"]. " +
                            "반드시 목록에 있는 이름 중 하나만 골라서 이름만 정확히 출력해줘. 다른 설명은 절대 추가하지 마.",
                    glassPerHour,
                    avgReactionTime != null ? avgReactionTime : 2000.0);

            String requestBody = buildGeminiRequest(prompt);
            Request request = new Request.Builder()
                    .url(apiUrl + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    String characterLevelName = parseGeminiResponse(responseBody);

                    if (characterLevelName != null && !characterLevelName.isEmpty()) {
                        for (int i = 0; i < levels.size(); i++) {
                            // 응답에 레벨 이름이 포함되어 있는지 확인
                            if (characterLevelName.contains(levels.get(i))) {
                                logger.info("Generated character level via Gemini: {} -> {}", characterLevelName, i);
                                return i;
                            }
                        }
                    }
                } else {
                    logger.error("Gemini API Request Failed: Code {}", response.code());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate character level via Gemini API", e);
        }

        logger.warn("Falling back to simple logic for character level.");
        return getFallbackCharacterLevel(glassPerHour);
    }

    private Integer getFallbackCharacterLevel(double glassPerHour) {
        if (glassPerHour >= 3.0)
            return 4;
        if (glassPerHour >= 2.0)
            return 3;
        if (glassPerHour >= 1.0)
            return 2;
        if (glassPerHour > 0)
            return 1;
        return 0;
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
     * Gemini API를 사용하여 재미있는 결과 설명 생성
     */
    public String generateFunnyDescription(User user, int rank, double glassPerHour) {
        final List<String> levels = Arrays.asList(
                "일청담 다이버", // 0
                "술 취한 다람쥐", // 1
                "지갑은 지킨다", // 2
                "술고래 후보생", // 3
                "인간 알코올" // 4
        );

        String characterLevelName = "알 수 없음";
        if (user.getCharacterLevel() != null && user.getCharacterLevel() >= 0
                && user.getCharacterLevel() < levels.size()) {
            characterLevelName = levels.get(user.getCharacterLevel());
        }

        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-api-key-here")) {
            logger.warn("Gemini API key not configured. Using fallback description.");
            return generateFallbackDescription(user, rank, glassPerHour);
        }

        try {
            String prompt = String.format(
                    "사용자 '%s'님의 술자리 결과를 재미있고 창의적으로 요약해줘. " +
                            "이 사용자의 최종 순위는 %d등이고, 캐릭터 레벨은 '%s'이며, 시간당 소주 %.1f잔을 마셨어. 총 소주 환산량은 %.1f잔이야." +
                            "이 정보를 바탕으로, 사용자의 노고를 치하하고 다음에도 술자리를 함께하고 싶게 만드는, 유머러스한 2-3문장의 한 줄 평을 만들어줘." +
                            "반드시 한글로 작성하고, 한 줄 평만 출력해줘.",
                    user.getUserName(),
                    rank,
                    characterLevelName,
                    glassPerHour,
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
                } else {
                    logger.error("Gemini API Request Failed for description: Code {}", response.code());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate description via Gemini API", e);
        }

        return generateFallbackDescription(user, rank, glassPerHour);
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

    private String generateFallbackDescription(User user, int rank, double glassPerHour) {
        return String.format("수고하셨습니다! %s님은 %d등입니다. (시속 %.1f잔)", user.getUserName(), rank, glassPerHour);
    }
}
