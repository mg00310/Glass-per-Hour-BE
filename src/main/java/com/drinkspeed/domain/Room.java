package com.drinkspeed.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 4)
    private String roomCode;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime endedAt;

    /**
     * 0: WAITING, 1: IN_PROGRESS, 2: ENDED
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer status = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void endRoom() {
        this.status = 2; // ENDED
        this.endedAt = LocalDateTime.now();
    }

    public boolean isEnded() {
        return this.status == 2;
    }
}
