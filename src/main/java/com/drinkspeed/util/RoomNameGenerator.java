package com.drinkspeed.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RoomNameGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RoomNameGenerator.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final OkHttpClient client;
    private final Gson gson;

    public RoomNameGenerator() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Gemini API를 사용하여 재미있는 방 이름 생성
     * 
     * @return AI가 생성한 방 이름
     */
    public String generateRoomName() {
        logger.info("=== Starting room name generation ===");
        logger.info("API Key present: {}", apiKey != null && !apiKey.isEmpty());
        logger.info("API Key value (first 10 chars): {}",
                apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) : "null");

        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-api-key-here")) {
            logger.warn("Gemini API key not configured. Using fallback room name.");
            return generateFallbackRoomName();
        }

        try {
            logger.info("Calling Gemini API for room name generation...");
            String prompt = "재미있고 창의적인 술자리 방 이름을 하나만 생성해줘. " +
                    "방 이름은 한국어로 10자 이내로 작성하고, 술자리 분위기에 맞게 유머러스하게 만들어줘. " +
                    "방 이름만 출력하고 다른 설명은 하지 마. 그리고 맨뒤에 by AI라는 텍스트를 붙여줘";

            String requestBody = buildGeminiRequest(prompt);
            Request request = new Request.Builder()
                    .url(apiUrl + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            logger.info("Sending request to: {}", apiUrl);
            try (Response response = client.newCall(request).execute()) {
                logger.info("Response code: {}", response.code());
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    logger.info("Response body received (length: {})", responseBody.length());
                    String generatedName = parseGeminiResponse(responseBody);

                    if (generatedName != null && !generatedName.isEmpty()) {
                        logger.info("✅ Generated room name via Gemini: {}", generatedName);
                        return generatedName.trim();
                    } else {
                        logger.warn("Gemini response parsing failed - no name extracted");
                    }
                } else {
                    logger.error("API call failed with code: {}, message: {}", response.code(), response.message());
                    if (response.body() != null) {
                        logger.error("Error response: {}", response.body().string());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to generate room name via Gemini API", e);
        }

        logger.warn("Falling back to random room name");
        return generateFallbackRoomName();
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
     * API 호출 실패 시 랜덤 방 이름 생성 (폴백)
     */
    private String generateFallbackRoomName() {
        String[] names = {
                "오늘만 산다🍺",
                "술자리 레전드",
                "한잔의 여유",
                "취중진담방",
                "술고래들의 모임",
                "간이 부르는 곳",
                "해 뜰 때까지",
                "주량 측정소",
                "알쓰 탈출 프로젝트",
                "소주 한잔 해요",
                "맥주는 역시",
                "소맥 타임",
                "막걸리 한사발",
                "과일소주 파티",
                "주당들의 향연"
        };

        int randomIndex = (int) (Math.random() * names.length);
        return names[randomIndex];
    }
}
