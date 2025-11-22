package com.drinkspeed.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drink_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrinkType drinkType;

    @Column(nullable = false)
    private Integer glassCount;

    @Column(nullable = false)
    private Double sojuEquivalent;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }

    public enum DrinkType {
        SOJU, // 소주
        BEER, // 맥주
        SOMAEK, // 소맥
        MAKGEOLLI, // 막걸리
        FRUIT_SOJU // 과일 소주
    }
}
