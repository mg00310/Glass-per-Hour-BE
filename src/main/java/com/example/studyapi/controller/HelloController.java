package com.example.studyapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 간단한 Hello World 컨트롤러
 * API가 정상적으로 작동하는지 확인하기 위한 테스트용 컨트롤러입니다.
 */
@RestController // 이 클래스가 REST API 컨트롤러임을 나타냅니다. 반환값이 자동으로 JSON/텍스트로 변환됩니다.
@RequestMapping("/api") // 이 컨트롤러의 모든 엔드포인트는 "/api"로 시작합니다.
public class HelloController {

    /**
     * Hello World 메시지를 반환하는 API
     * 
     * 접속 방법: GET http://localhost:8080/api/hello
     * 
     * @return "Hello, World!" 문자열
     */
    @GetMapping("/hello") // GET 요청을 처리합니다. 전체 경로는 "/api/hello"가 됩니다.
    public String hello() {
        // 단순히 문자열을 반환합니다. 서버가 정상 작동하는지 확인할 수 있습니다.
        return "Hello, World! API is working correctly.";
    }
}
