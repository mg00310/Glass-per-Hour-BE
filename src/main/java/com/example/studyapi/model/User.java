package com.example.studyapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자(User) 정보를 담는 모델 클래스
 * 데이터베이스의 테이블 구조를 표현하는 엔티티 역할을 합니다.
 * (현재는 DB 없이 메모리에만 저장됩니다)
 */
@Data // Lombok: getter, setter, toString, equals, hashCode 메서드를 자동으로 생성합니다.
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자를 자동으로 생성합니다.
@NoArgsConstructor // Lombok: 파라미터가 없는 기본 생성자를 자동으로 생성합니다.
public class User {

    /**
     * 사용자 고유 ID
     * 각 사용자를 구분하기 위한 식별자입니다.
     */
    private Long id;

    /**
     * 사용자 이름
     */
    private String name;

    /**
     * 사용자 이메일 주소
     */
    private String email;
}
