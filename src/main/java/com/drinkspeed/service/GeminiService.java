package com.drinkspeed.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final OkHttpClient client;
    private final Gson gson;

    public GeminiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Gemini API를 사용하여 술자리 결과 메시지 생성
     */
    public String generateDrinkingResultMessage(String userName, int durationSeconds, double totalSojuEquivalent,
            int level) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-api-key-here")) {
            logger.warn("Gemini API key is missing or invalid. Key: {}", (apiKey == null ? "null" : "masked"));
            return generateFallbackMessage(userName, durationSeconds);
        }

        try {
            logger.info("Starting Gemini API request for user: {}", userName);
            String durationStr = formatDuration(durationSeconds);

            // 레벨 이름 매핑
            String[] levelNames = { "홍익인간", "일청담 다이버", "술 취한 다람쥐", "술고래 지망생", "술먹는 하마" };
            String levelName = (level >= 0 && level < levelNames.length) ? levelNames[level] : "알 수 없음";

            String prompt = String.format(
                    "# Role\n" +
                            "당신은 술자리 분위기를 띄워주는 유머러스하고 재치 있는 'AI 술자리 해설가'입니다. 사용자의 주량 데이터와 레벨을 분석하여, 결과 화면에 띄울 멘트를 작성해야 합니다.\n\n" +
                            "# Input Data\n" +
                            "1. 사용자 닉네임: %s\n" +
                            "2. 총 술자리 시간: %s\n" +
                            "3. 소주 환산 잔 수: %.1f잔\n" +
                            "4. 주량 레벨: %s\n\n" +
                            "# 주량 레벨 정의 (총 5단계)\n" +
                            "0. [홍익인간]: 술을 거의 못 마심. 한 잔만 마셔도 얼굴이 빨개짐.\n" +
                            "1. [일청담 다이버]: 술자리 분위기에 휩쓸려 마시는 단계.\n" +
                            "2. [술취한 다람쥐]: 적당히 취해서 기분이 좋고 행동이 귀여워짐.\n" +
                            "3. [술고래 후보생]: 술을 꽤 잘 마시며 즐기는 고수.\n" +
                            "4. [술먹는 하마]: 엄청난 주량을 가진 끝판왕.\n\n" +
                            "# Output Rules\n" +
                            "1. 반드시 첫 문장은 다음 포맷을 그대로 따를 것: \"{nickname}님, 소주 환산 기준 {soju_count}잔을 마시셨네요!\"\n" +
                            "2. 두 번째 문장은 '총 술자리 시간'과 '소주 환산 잔 수'의 관계를 깊이 있게 분석하여 코멘트를 작성할 것.\n" +
                            "   - (판단 기준 예시) 사용자의 공식 레벨이 높더라도(예: 술먹는 하마), 만약 '총 술자리 시간'이 30분 미만으로 매우 짧다면, 이는 '단거리 전력 질주형'입니다. 이 경우, '엄청난 속도였지만 페이스 조절은 아쉬웠다'는 뉘앙스로 코멘트해주세요.\n" +
                            "   - 반대로, 오랜 시간에 걸쳐 꾸준히 마셨다면, 그 꾸준함과 안정적인 페이스를 칭찬해주세요.\n" +
                            "   - 그 외에는 '주량 레벨 정의'에 맞는 일반적인 코멘트를 작성해주세요.\n" +
                            "3. 톤앤매너(Tone & Manner): 재치 있고, 유머러스하며, 긍정적인 톤을 유지할 것. 절대 사용자를 비난하거나 부정적으로 평가하지 말 것.\n\n" +
                            "# Examples\n" +
                            "- (LV4, 25분만에 5잔) → \"엄청난 속도지만, 너무 단거리 경주 아니었나요? 다음엔 마라톤처럼 길게 즐겨보세요!\"\n" +
                            "- (LV2, 3시간 동안 13잔) → \"3시간 동안 꾸준히 달리셨군요! 안정적인 페이스가 인상적입니다.\""
                    ,userName, durationStr, totalSojuEquivalent, levelName);

            String requestBody = buildGeminiRequest(prompt);
            Request request = new Request.Builder()
                    .url(apiUrl + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                logger.info("Gemini API Response Code: {}", response.code());

                if (response.isSuccessful()) {
                    String generatedMessage = parseGeminiResponse(responseBody);

                    if (generatedMessage != null && !generatedMessage.isEmpty()) {
                        logger.info("Successfully generated result message via Gemini for user: {}", userName);
                        return generatedMessage.trim();
                    } else {
                        logger.warn("Gemini response parsed to empty message. Body: {}", responseBody);
                    }
                } else {
                    logger.error("Gemini API Request Failed: Code {}, Body: {}", response.code(), responseBody);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate result message via Gemini API", e);
        }

        return generateFallbackMessage(userName, durationSeconds);
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0)
            sb.append(hours).append("시간 ");
        if (minutes > 0)
            sb.append(minutes).append("분 ");
        sb.append(seconds).append("초");
        return sb.toString();
    }

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
                        return parts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse Gemini response", e);
        }
        return null;
    }

    private String generateFallbackMessage(String userName, int durationSeconds) {
        return String.format("%s님! 총 %d초 동안 즐기셨네요.", userName, durationSeconds);
    }
}