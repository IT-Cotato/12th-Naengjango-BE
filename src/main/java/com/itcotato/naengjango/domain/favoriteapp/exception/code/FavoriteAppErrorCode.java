package com.itcotato.naengjango.domain.favoriteapp.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteAppErrorCode implements BaseErrorCode {

    DUPLICATE_FAVORITE_APP(HttpStatus.BAD_REQUEST,
            "FAVORITEAPP400_1",
            "이미 즐겨찾기에 추가된 앱입니다."),
    INVALID_FAVORITE_APP_NAME(HttpStatus.BAD_REQUEST,
            "FAVORITEAPP400_2",
            "즐겨찾기 앱 이름이 유효하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
