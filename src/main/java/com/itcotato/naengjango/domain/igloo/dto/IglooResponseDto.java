package com.itcotato.naengjango.domain.igloo.dto;

public class IglooResponseDto {

    public record Status(
            int iglooLevel,
            int snowballBalance,
            Integer requiredSnowballsForNextLevel, // max면 null
            int freezeFailCount
    ) {}

    public record UpgradeResult(
            int beforeLevel,
            int afterLevel,
            int snowballBalanceAfter,
            int spentSnowballs
    ) {}
}
