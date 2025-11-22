package com.example.studyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션의 메인 클래스
 * 이 클래스는 프로그램의 시작점(Entry Point)입니다.
 */
@SpringBootApplication // Spring Boot의 자동 설정을 활성화하는 어노테이션
public class StudyApiApplication {

	/**
	 * 프로그램의 시작 메서드
	 * 
	 * @param args 커맨드 라인 인자 (실행 시 전달되는 파라미터)
	 */
	public static void main(String[] args) {
		// Spring Boot 애플리케이션을 실행합니다.
		// 내장 톰캣 서버가 시작되고, 기본적으로 8080 포트에서 실행됩니다.
		SpringApplication.run(StudyApiApplication.class, args);
	}

}
