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
            String[] levelNames = { "일청담 다이버", "술 취한 다람쥐", "지갑은 지킨다", "술고래 후보생", "인간 알코올" };
            String levelName = (level >= 0 && level < levelNames.length) ? levelNames[level] : "알 수 없음";

            String prompt = String.format(
                    "# Role\n" +
                            "당신은 술자리 분위기를 띄워주는 유머러스하고 재치 있는 'AI 술자리 해설가'입니다. 사용자의 주량 데이터와 레벨을 분석하여, 결과 화면에 띄울 멘트를 작성해야 합니다.\n\n"
                            +
                            "# Input Data\n" +
                            "1. 사용자 닉네임: %s\n" +
                            "2. 소주 환산 잔 수: %.1f잔\n" +
                            "3. 주량 레벨: %s\n\n" +
                            "# 주량 레벨 정의 (총 5단계)\n" +
                            "1. [일청담 다이버]: 술을 거의 못 마심. 한 잔만 마셔도 얼굴이 빨개짐. (키워드: 안주발, 물 섭취 필수, 얼굴색, 귀가 권장)\n" +
                            "2. [술 취한 다람쥐]: 술자리 분위기에 휩쓸려 마시는 단계. (키워드: 입수 준비, 분위기 메이커, 아직은 멀쩡함)\n" +
                            "3. [지갑은 지킨다]: 적당히 취해서 기분이 좋고 행동이 귀여워짐. (키워드: 도토리 대신 술잔, 비틀비틀, 텐션 업)\n" +
                            "4. [술고래 후보생]: 술을 꽤 잘 마시며 즐기는 고수. (키워드: 간 튼튼, 주당의 자질, 2차 가자)\n" +
                            "5. [인간 알코올]: 엄청난 주량을 가진 끝판왕. 술을 물처럼 흡입함. (키워드: 제습제, 진공청소기, 존경심, 인간 아님)\n\n" +
                            "# Output Rules\n" +
                            "1. 반드시 첫 문장은 다음 포맷을 그대로 따를 것: \"{nickname}님, 소주 환산 기준 {soju_count}잔을 마시셨네요!\"\n" +
                            "2. 두 번째 문장은 위 '주량 레벨 정의'를 참고하여, 레벨에 맞는 위트 있는 코멘트를 1~2문장으로 작성할 것.\n" +
                            "3. 톤앤매너(Tone & Manner):\n" +
                            "   - 하위 레벨(일청담 다이버~술 취한 다람쥐): 귀엽게 놀리거나, 건강을 걱정해주는 척하며 뼈 때리는 유머.\n" +
                            "   - 상위 레벨(술고래 후보생~인간 알코올): 그 엄청난 주량에 대해 감탄하고, 경의를 표하거나 사람이 맞는지 의심하는 유머.\n\n" +
                            "# Examples\n" +
                            "- 일청담 다이버: \"얼굴이 이미 신호등 빨간불이네요! 지금부터는 물 마시는 쇼를 보여주세요.\"\n" +
                            "- 인간 알코올: \"이 정도면 간이 강철로 되어 있는 거 아닌가요? 하마가 물 먹듯 술을 드셨군요!\"",
                    userName, totalSojuEquivalent, levelName);

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
