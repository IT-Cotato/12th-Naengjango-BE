package com.itcotato.naengjango.domain.snowball.dto;

import java.time.LocalDateTime;

public class SnowballResponseDto {

    public record Summary(
            int totalSnowballs,
            int todayEarned
    ) {}

    public record History(
            int amount,
            String reason,
            LocalDateTime createdAt
    ) {}
}
