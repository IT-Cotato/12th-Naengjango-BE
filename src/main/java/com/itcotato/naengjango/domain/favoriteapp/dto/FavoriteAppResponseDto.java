package com.itcotato.naengjango.domain.favoriteapp.dto;

import com.itcotato.naengjango.domain.favoriteapp.entity.FavoriteApp;
import com.itcotato.naengjango.domain.favoriteapp.enums.SupportedApp;

import java.util.Optional;

public class FavoriteAppResponseDto {

    public record FavoriteAppResponse(
            Long id,
            String appName,
            boolean supported,
            String iconKey
    ) {
        public static FavoriteAppResponse from(FavoriteApp app) {
            Optional<SupportedApp> supportedApp =
                    SupportedApp.from(app.getAppName());

            return new FavoriteAppResponse(
                    app.getId(),
                    app.getAppName(),
                    supportedApp.isPresent(),
                    supportedApp.map(SupportedApp::getIconKey).orElse(null)
            );
        }
    }
}
