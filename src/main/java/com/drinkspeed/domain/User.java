package com.drinkspeed.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column
    private LocalDateTime finishedAt;

    @Builder.Default
    @Column(nullable = false)
    private Double totalSojuEquivalent = 0.0;

    @Column
    private Double glassPerHour;

    @Column
    private Integer finalRank;

    @Column
    private String characterLevel;

    @Column(length = 1000)
    private String funnyDescription;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isHost = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DrinkRecord> drinkRecords = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReactionTest> reactionTests = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public void finish() {
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return finishedAt != null;
    }
}
