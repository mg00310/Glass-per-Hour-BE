package com.drinkspeed.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;
    private String userName;
    private LocalDateTime joinedAt;
    private LocalDateTime finishedAt;

    @Builder.Default
    private Double totalSojuEquivalent = 0.0;

    // Individual drink counts
    @Builder.Default
    private Double sojuCount = 0.0;
    @Builder.Default
    private Double beerCount = 0.0;
    @Builder.Default
    private Double somaekCount = 0.0;
    @Builder.Default
    private Double makgeolliCount = 0.0;
    @Builder.Default
    private Double fruitsojuCount = 0.0;

    private Integer characterLevel;
    private String aiMessage;

    

    public void finish() {
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return finishedAt != null;
    }
}
