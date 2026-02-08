package com.itcotato.naengjango.domain.favoriteapp.dto;

public class FavoriteAppRequestDto {

    public record FavoriteAppCreate(
            String appName
    ) {}
}
