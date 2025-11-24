package com.drinkspeed.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * com.drinkspeed.controller 패키지 하위의 모든 public 메서드를 대상으로 하는 Pointcut
     */
    @Pointcut("within(com.drinkspeed.controller..*)")
    public void controller() {}

    /**
     * com.drinkspeed.service 패키지 하위의 모든 public 메서드를 대상으로 하는 Pointcut
     */
    @Pointcut("within(com.drinkspeed.service..*)")
    public void service() {}

    /**
     * 컨트롤러 메서드 실행 전후에 로그를 남깁니다.
     */
    @Around("controller()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(this::toJson)
                .collect(Collectors.joining(", "));

        log.info("==================== [API 요청] ====================");
        log.info("➡️  요청 URI: [{}]{}", request.getMethod(), request.getRequestURI());
        log.info("➡️  컨트롤러: {}", methodName);
        log.info("➡️  파라미터: {}", args);
        log.info("==================================================");

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("==================== [API 응답] ====================");
        log.info("⬅️  컨트롤러: {}", methodName);
        log.info("⬅️  반환 값: {}", toJson(result));
        log.info("⬅️  실행 시간: {}ms", duration);
        log.info("==================================================\n");

        return result;
    }

    /**
     * 서비스 메서드 실행 전에 로그를 남깁니다.
     */
    @Before("service()")
    public void logServiceBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(this::toJson)
                .collect(Collectors.joining(", "));

        log.debug("  ┌─ [서비스 시작] ──────────────────────────────────");
        log.debug("  │  메서드: {}", methodName);
        log.debug("  │  파라미터: {}", args);
        log.debug("  └─────────────────────────────────────────────────");
    }

    /**
     * 서비스 메서드 실행 후 (성공적으로 반환될 때) 로그를 남깁니다.
     */
    @AfterReturning(pointcut = "service()", returning = "result")
    public void logServiceAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("  ┌─ [서비스 종료] ──────────────────────────────────");
        log.debug("  │  메서드: {}", methodName);
        log.debug("  │  반환 값: {}", toJson(result));
        log.debug("  └─────────────────────────────────────────────────");
    }

    /**
     * 객체를 JSON 문자열로 변환합니다. 변환에 실패하면 객체의 toString()을 반환합니다.
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            // HttpServletRequest, Response 등 프록시 객체나 직렬화가 어려운 객체는 간단히 클래스명만 출력
            String className = obj.getClass().getName();
            if (className.contains("Request") || className.contains("Response") || className.contains("Session")) {
                return obj.getClass().getSimpleName();
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[JSON 변환 불가: " + obj.getClass().getSimpleName() + "]";
        }
    }
}