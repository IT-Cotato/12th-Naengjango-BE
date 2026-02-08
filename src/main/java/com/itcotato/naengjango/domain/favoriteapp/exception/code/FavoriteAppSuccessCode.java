package com.itcotato.naengjango.domain.favoriteapp.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteAppSuccessCode implements BaseSuccessCode {

    FAVORITE_APP_ADD_SUCCESS(HttpStatus.OK,
            "FAVORITEAPP200_1",
            "즐겨찾기 앱이 성공적으로 추가되었습니다."),
    FAVORITE_APP_REMOVE_SUCCESS(HttpStatus.OK,
            "FAVORITEAPP200_2",
            "즐겨찾기 앱이 성공적으로 제거되었습니다."),
    FAVORITE_APP_LIST_RETRIEVE_SUCCESS(HttpStatus.OK,
            "FAVORITEAPP200_3",
            "즐겨찾기 앱 목록이 성공적으로 조회되었습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
